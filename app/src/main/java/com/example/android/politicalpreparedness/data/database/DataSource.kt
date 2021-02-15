package com.example.android.politicalpreparedness.data.database

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.database.representativescache.RepresentativeCache
import com.example.android.politicalpreparedness.data.database.representativescache.RepresentativeCacheDataItem
import com.example.android.politicalpreparedness.data.database.representativescache.RepresentativeCacheLocation
import com.example.android.politicalpreparedness.data.models.Election

interface DataSource {

    suspend fun insertOrUpdate(election: Election)

    fun observeElections(): LiveData<Result<List<Election>>>

    suspend fun getRepresentatives(location: String): Result<RepresentativeCache>

    suspend fun clearRepresentativeCache(location: String)

    suspend fun deleteAllElections()

    suspend fun changeFollowingStatus(electionId: Int, shouldFollow: Boolean)

    suspend fun getElection(electionId: Int): Result<Election>

    suspend fun saveLocation(state: RepresentativeCacheLocation)

    suspend fun saveRepresentative(representative: RepresentativeCacheDataItem)
}