package com.example.android.politicalpreparedness

import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.data.network.models.Election
import com.example.android.politicalpreparedness.data.network.models.ElectionResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApplicationRepository(
        private val network: CivicsApi,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun getElections(): Result<ElectionResponse> = withContext(ioDispatcher) {
        return@withContext try {
            network.retrofitService.getElections()
            Result.Success(network.retrofitService.getElections())
        } catch (e: Exception) {
            Result.Error(e.localizedMessage)
        }
    }
}