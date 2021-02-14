package com.example.android.politicalpreparedness.data.database

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.android.politicalpreparedness.data.DataSource
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.models.Election

class LocalDataSource(private val database: ElectionDatabase) : DataSource {

    override suspend fun getElections(): Result<List<Election>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertOrUpdate(election: Election) {
        try {
            database.electionDao.insertElection(election)
        } catch (e: SQLiteConstraintException) { //already exists, update
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

    override suspend fun changeFollowingStatus(electionId: Int, shouldFollow: Boolean) {
        database.electionDao.changeFollowingStatus(electionId, shouldFollow)
    }

    override suspend fun getElection(electionId: Int): Result<Election> {
        val election = database.electionDao.getElection(electionId)
        return if (null != election) {
            Result.Success(election)
        } else {
            Result.Error("Election with id $electionId doesn't exist")
        }
    }

}