package com.example.android.politicalpreparedness.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.data.network.models.*
import com.example.android.politicalpreparedness.representative.model.Representative
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.*

class ApplicationRepository(
        private val localDataSource: DataSource,
        private val network: CivicsApi,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val TAG = ApplicationRepository::class.java.simpleName


    fun observeElections(): LiveData<Result<List<Election>>> = localDataSource.observeElections()

    /** Throws the following
     * @throws HttpException
     * @throws SocketTimeoutException
     * @throws Exception no data is received
     * @throws JsonDataException error parsing jaon
     * @throws IOException error reading json
     *
     */
    suspend fun refreshElections() = withContext(ioDispatcher) {
        val response = network.retrofitService.getElections()
        Log.d(TAG, "elections network response = $response")

        if ((null != response) && (response.elections.isNotEmpty())) {
            response.elections.forEach {
                addDummyStateIfIsEmptyInDivision(it) //TODO REMOVE ONCE elections are fixed
                localDataSource.insertOrUpdate(it) //elections shall be observed
            }

            //insertMOARElections()
        } else {
            throw Exception("connection is OK, but no elections received")
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
     * @throws HttpException
     * @throws SocketTimeoutException
     * @throws Exception is no data received
     */
    suspend fun getVoterInfo(
            electionId: Int,
            address: String,
            officialOnly: Boolean = false
    ): Result<VoterInfoResponse> = withContext(ioDispatcher) {
        return@withContext try {
            val response = network.retrofitService.getVoterInfo(electionId, address, officialOnly)
            Log.d(TAG, "voterinfo network response = $response")

            if (null != response) {
                Result.Success(response)
            } else {
                Result.Error("connection is OK, but no voterinfo received")
            }
        } catch (e: Exception) {
            Result.Error("Exception: " + convertExceptionToString(e))
        }
    }

    suspend fun getRepresentatives(
            address: String
    ): Result<List<Representative>> = withContext(ioDispatcher) {
        return@withContext try {
            val response = network.retrofitService.getRepresentatives(address)
            if (null != response) {
                Log.d(TAG, "response = $response")
                val representatives = response.offices.flatMap { office -> office.getRepresentatives(response.officials) }
                /*representatives.forEach {
                    Log.d(TAG, "representative = $it")
                }*/
                Result.Success(representatives)
            } else {
                Result.Error("connection is OK, but no representatives received")
            }
        } catch (e: Exception) {
            Result.Error("Exception: " + convertExceptionToString(e))
        }
    }

    private fun convertExceptionToString(e: Exception): String = when (e) {
        is HttpException -> {
            e.localizedMessage ?: "http exception"
        }
        is SocketTimeoutException -> {
            e.localizedMessage ?: "socket timeout exception"
        }
        is JsonDataException -> {
            e.localizedMessage ?: "json data exception"
        }
        is IOException -> {
            e.localizedMessage ?: "IO exception"
        }
        else -> {
            "Unknown exception: ${e.localizedMessage}"
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