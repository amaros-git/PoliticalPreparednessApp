package com.example.android.politicalpreparedness.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.DataSource
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.data.network.models.Election
import com.example.android.politicalpreparedness.data.network.models.ElectionResponse
import com.example.android.politicalpreparedness.data.network.models.VoterInfoResponse
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import retrofit2.HttpException
import java.net.SocketTimeoutException

class ApplicationRepository(
        private val localDataSource: DataSource,
        private val network: CivicsApi,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val TAG = ApplicationRepository::class.java.simpleName

    fun observeElections(): LiveData<Result<List<Election>>> = localDataSource.observeElections()

    /**
     * @throws HttpException
     * @throws SocketTimeoutException
     * @throws Exception is no data received
     * @throws JsonDataException error parsing jaon
     * @throws IOException error reading json
     *
     */
    suspend fun refreshElections() {
        val response = network.retrofitService.getElections()
        Log.d(TAG, "elections network response = $response")

        if ((null != response) && (response.elections.isNotEmpty())) {
            response.elections.forEach {
                addDummyStateIfStateIsEmptyInDivision(it) //TODO REMOVE ONCE elections are fixed
                localDataSource.saveElection(it) //elections shall be observed
            }
        } else {
            throw Exception("connection is OK, but no elections received")
        }
    }

    /**
     * at the moment Google Civics API returns only one election, but it doesn't have state
     * in the result voteinfo API returns error. So, if state is empty, add dummy state "ca"
     * In such case voteinfo returns some data
     */
    //TODO REMOVE ONCE elections are fixed
    private fun addDummyStateIfStateIsEmptyInDivision(election: Election) {
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
    ): Result<VoterInfoResponse> {
        return try {
            val response = network.retrofitService.getVoterInfo(electionId, address, officialOnly)
            Log.d(TAG, "voterinfo network response = $response")

            if (null != response) {
                Result.Success(response)
            } else {
                Result.Error("connection is OK, but no voterinfo received")
            }
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    Result.Error(e.localizedMessage)
                }
                is SocketTimeoutException -> {
                    Result.Error(e.localizedMessage)
                }
                is JsonDataException -> {
                    Result.Error(e.localizedMessage)
                }
                is IOException -> {
                    Result.Error(e.localizedMessage)
                }
                else -> Result.Error("Unknown exception: ${e.localizedMessage}")
            }
        }
    }
}