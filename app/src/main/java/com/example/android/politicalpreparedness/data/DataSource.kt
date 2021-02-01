package com.example.android.politicalpreparedness.data

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.network.models.Election

interface DataSource {

    suspend fun getElections(): Result<List<Election>>

    suspend fun insertOrUpdate(election: Election)

    fun observeElections(): LiveData<Result<List<Election>>>

    suspend fun deleteAllElections()
}