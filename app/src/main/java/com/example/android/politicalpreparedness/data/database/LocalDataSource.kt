package com.example.android.politicalpreparedness.data.database

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.android.politicalpreparedness.data.DataSource
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.network.models.Election
import com.example.android.politicalpreparedness.data.network.models.ElectionUpdate

class LocalDataSource(private val database: ElectionDatabase) : DataSource {

    override suspend fun getElections(): Result<List<Election>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertOrUpdate(election: Election) {
        try {
            database.electionDao.insertElection(election)
        } catch (e: SQLiteConstraintException) { //already exists, update
            Log.d("TEST", "Exists, updating")
            database.electionDao.updateElection(
                    ElectionUpdate(
                            election.id,
                            election.name,
                            election.electionDay,
                            election.division
                    )
            )
        }
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