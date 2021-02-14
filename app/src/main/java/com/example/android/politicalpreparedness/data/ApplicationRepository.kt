package com.example.android.politicalpreparedness.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.data.network.models.*
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.utils.convertExceptionToToastString
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*

class ApplicationRepository(
        private val localDataSource: DataSource,
        private val network: CivicsApi,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val TAG = ApplicationRepository::class.java.simpleName


    fun observeElections(): LiveData<Result<List<Election>>> = localDataSource.observeElections()

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

    /*//TODO FOR TEST ONLY
    private suspend fun insertMOARElections() {
        for (i in 0..10) {
            Log.d("TEST", i.toString())
            val election = Election(
                    i,
                    "Test name$i",
                    Date(0),
                    Division(i.toString(), "us", "ca")
            )
            localDataSource.insertOrUpdate(election)
        }
    }*/

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

    /**
     * throws. Check CivicsApiService interface
     */
    suspend fun getRepresentatives(
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