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
                addDummyStateIfIsEmptyInDivision(it) //TODO REMOVE ONCE elections are fixed
                localDataSource.insertOrUpdate(it) //elections shall be observed
            }
        }
    }

    suspend fun refreshRepresentativesCache(
            representatives: List<Representative>,
            address: Address
    ) = withContext(ioDispatcher) {
        Log.d(TAG, "refreshRepresentativesCache called")

        representatives.forEach {
            localDataSource.saveRepresentative(RepresentativeCacheDataItem(
                    0,
                    getCityState(address),
                    address.city,
                    address.state,
                    it.official.name,
                    it.official.party,
                    it.official.photoUrl,
                    getTwitterId(it.official.channels),
                    getFacebookId(it.official.channels),
                    it.office.division.id,
                    it.official.urls?.first())
            )

            localDataSource.saveState(
                    RepresentativeCacheLocation(
                            0, getCityState(address)
                    )
            )
        }
    }

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



    /**
     * at the moment Google Civics API returns only one election, but it doesn't have state
     * in the result voteinfo API returns error. So, if state is empty, add dummy state "ca"
     * In such case voteinfo returns some data
     */
    //TODO REMOVE ONCE elections are fixed
    private fun addDummyStateIfIsEmptyInDivision(election: Election) {
        if (election.division.state.isEmpty()) {
            election.division.state = "ca"
        }
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


    suspend fun changeFollowingStatus(
            electionId: Int,
            shouldFollow: Boolean) = withContext(ioDispatcher) {
        localDataSource.changeFollowingStatus(electionId, shouldFollow)
    }

    suspend fun getElection(electionId: Int): Result<Election> = withContext(ioDispatcher) {
        localDataSource.getElection(electionId)
    }
}