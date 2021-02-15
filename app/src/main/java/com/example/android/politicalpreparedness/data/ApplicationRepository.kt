package com.example.android.politicalpreparedness.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.database.DataSource
import com.example.android.politicalpreparedness.data.database.representativescache.RepresentativeCache
import com.example.android.politicalpreparedness.data.database.representativescache.RepresentativeCacheDataItem
import com.example.android.politicalpreparedness.data.database.representativescache.RepresentativeCacheLocation
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.data.models.*
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApplicationRepository(
        private val localDataSource: DataSource,
        private val network: CivicsApi,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val TAG = ApplicationRepository::class.java.simpleName


    fun observeElections(): LiveData<Result<List<Election>>> =
            localDataSource.observeElections()

    /**
     * Throws, check CivicsApiService interface
     */
    suspend fun refreshElections() = withContext(ioDispatcher) {
        val response = network.retrofitService.getElections()

        if ((null != response) && (response.elections.isNotEmpty())) {
            response.elections.forEach {
                localDataSource.insertOrUpdate(it) //elections shall be observed
            }
        }
    }

    suspend fun changeFollowingStatus(
            electionId: Int,
            shouldFollow: Boolean) = withContext(ioDispatcher) {
        localDataSource.changeFollowingStatus(electionId, shouldFollow)
    }

    suspend fun getElection(electionId: Int): Result<Election> = withContext(ioDispatcher) {
        localDataSource.getElection(electionId)
    }

    /**
     * throws. Check CivicsApiService interface
     */
    suspend fun getVoterInfo(
            electionId: Int,
            address: String,
            officialOnly: Boolean = false
    ): Result<VoterInfoResponse> = withContext(ioDispatcher) {
        val response = network.retrofitService.getVoterInfo(electionId, address, officialOnly)
        if (null != response) {
            Result.Success(response)
        } else {
            Result.Error("connection is OK, but no voterinfo received")
        }
    }


    suspend fun getRepresentatives(
            location: String
    ): Result<RepresentativeCache> = withContext(ioDispatcher) {
        localDataSource.getRepresentatives(location)
    }

    suspend fun refreshRepresentativesCache(
            representatives: List<Representative>,
            address: Address
    ) = withContext(ioDispatcher) {
        
        representatives.forEach {
            localDataSource.saveRepresentative(convertRepresentativeToCacheItem(it, address))
            localDataSource.saveLocation(RepresentativeCacheLocation(0, getCityState(address)))

        }
    }

    private fun convertRepresentativeToCacheItem(
            representative: Representative,
            address: Address
    ) =
            RepresentativeCacheDataItem(
                    0,
                    getCityState(address),
                    address.city,
                    address.state,
                    representative.official.name,
                    representative.official.party,
                    representative.official.photoUrl,
                    getTwitterId(representative.official.channels),
                    getFacebookId(representative.official.channels),
                    representative.official.urls?.first(),
                    representative.office.division.id
            )

    private fun getCityState(address: Address) = address.city + address.state

    private fun getFacebookId(channels: List<Channel>?): String? {
        return channels?.filter { channel -> channel.type == "Facebook" }
                ?.map { channel -> channel.id }
                ?.firstOrNull()
    }

    private fun getTwitterId(channels: List<Channel>?): String? {
        return channels?.filter { channel -> channel.type == "Twitter" }
                ?.map { channel -> channel.id }
                ?.firstOrNull()
    }


    suspend fun clearRepresentativeCache(address: Address) = withContext(ioDispatcher) {
        localDataSource.clearRepresentativeCache(getCityState(address))
    }


    /**
     * throws. Check CivicsApiService interface
     */
    suspend fun refreshRepresentativesFromNetwork(
            address: String
    ): Result<List<Representative>> = withContext(ioDispatcher) {
        val response = network.retrofitService.getRepresentatives(address)
        if (null != response) {
            val representatives = response.offices.flatMap { office -> office.getRepresentatives(response.officials) }
            Result.Success(representatives)
        } else {
            Result.Error("connection is OK, but no representatives received")
        }
    }
}