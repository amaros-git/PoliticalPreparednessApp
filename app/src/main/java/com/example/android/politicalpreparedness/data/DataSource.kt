package com.example.android.politicalpreparedness.data

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.database.representativescache.RepresentativeCache
import com.example.android.politicalpreparedness.data.database.representativescache.RepresentativeCacheDataItem
import com.example.android.politicalpreparedness.data.database.representativescache.Test
import com.example.android.politicalpreparedness.data.models.Election

interface DataSource {

    suspend fun insertOrUpdate(election: Election)

    fun observeElections(): LiveData<Result<List<Election>>>

    fun observeRepresentatives(): LiveData<Result<List<RepresentativeCache>>>

    suspend fun deleteAllElections()

    suspend fun changeFollowingStatus(electionId: Int, shouldFollow: Boolean)

    suspend fun getElection(electionId: Int): Result<Election>

    suspend fun saveState(state: Test)

    suspend fun saveRepresentative(representative: RepresentativeCacheDataItem)
}