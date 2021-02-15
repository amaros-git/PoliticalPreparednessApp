package com.example.android.politicalpreparedness.data

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.database.DataSource
import com.example.android.politicalpreparedness.data.database.representativescache.*
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