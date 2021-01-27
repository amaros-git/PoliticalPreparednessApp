package com.example.android.politicalpreparedness.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.DataSource
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.data.network.models.Election
import com.example.android.politicalpreparedness.data.network.models.ElectionResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApplicationRepository(
        private val localDataSource: DataSource,
        private val network: CivicsApi,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val TAG = ApplicationRepository::class.java.simpleName

    fun observeTasks(): LiveData<Result<List<Election>>> = localDataSource.observeElections()

    /**
     * @throws HttpException
     * @throws SocketTimeoutException
     * @throws Exception is no data received
     */
    suspend fun refreshElections() {
        val response = network.retrofitService.getElections()
        Log.d(TAG, "network response = $response")

        if ((null != response) && (response.elections.isNotEmpty())) {
            response.elections.forEach {
                localDataSource.saveElection(it) //elections shall be observed in advance
            }
        } else {
            throw Exception("connection is OK, but no elections received")
        }
    }

}