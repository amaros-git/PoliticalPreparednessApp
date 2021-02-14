package com.example.android.politicalpreparedness.data.database

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.android.politicalpreparedness.data.DataSource
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.database.representativescache.RepresentativeCache
import com.example.android.politicalpreparedness.data.database.representativescache.RepresentativeCacheDataItem
import com.example.android.politicalpreparedness.data.database.representativescache.RepresentativeDatabase
import com.example.android.politicalpreparedness.data.database.representativescache.Test
import com.example.android.politicalpreparedness.data.models.Election

class LocalDataSource(
        private val electionDB: ElectionDatabase,
        private val representativeDB: RepresentativeDatabase
        ) : DataSource {

    override suspend fun insertOrUpdate(election: Election) {
        try {
            electionDB.electionDao.insertElection(election)
        } catch (e: SQLiteConstraintException) { //already exists, update
            electionDB.electionDao.updateElection(
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
        return electionDB.electionDao.observeElections().map {
            Result.Success(it)
        }
    }

    override fun observeRepresentatives(location: String): LiveData<Result<RepresentativeCache>> {
        return representativeDB.representativeDAO.observeRepresentatives(location).map {
            Result.Success(it)
        }
    }

    override suspend fun saveState(state: Test) {
        representativeDB.representativeDAO.insertState(state)
    }

    override suspend fun saveRepresentative(representative: RepresentativeCacheDataItem) {
        representativeDB.representativeDAO.insertRepresentative(representative)
    }

    override suspend fun deleteAllElections() {
        TODO("Not yet implemented")
    }

    override suspend fun changeFollowingStatus(electionId: Int, shouldFollow: Boolean) {
        electionDB.electionDao.changeFollowingStatus(electionId, shouldFollow)
    }

    override suspend fun getElection(electionId: Int): Result<Election> {
        val election = electionDB.electionDao.getElection(electionId)
        return if (null != election) {
            Result.Success(election)
        } else {
            Result.Error("Election with id $electionId doesn't exist")
        }
    }

}