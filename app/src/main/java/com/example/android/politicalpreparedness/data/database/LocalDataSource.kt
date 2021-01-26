package com.example.android.politicalpreparedness.data.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.android.politicalpreparedness.data.DataSource
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.network.models.Election

class LocalDataSource(private val database: ElectionDatabase) : DataSource {

    override suspend fun getElections(): Result<List<Election>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveElection(election: Election) {
        database.electionDao.insertElection(election)
    }

    override fun observeElections(): LiveData<Result<List<Election>>> {
        return database.electionDao.observeElections().map {
            Result.Success(it)
        }
    }

    override suspend fun deleteAllElections() {
        TODO("Not yet implemented")
    }

}