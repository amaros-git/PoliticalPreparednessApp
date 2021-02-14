package com.example.android.politicalpreparedness.data.database.representativescache

import androidx.room.*

@Dao
interface RepresentativeDAO {

    @Transaction
    @Query("SELECT * FROM representative_locations WHERE city_state = :location")
    suspend fun getRepresentatives(location: String): RepresentativeCache?

    @Insert
    suspend fun insertState(state: RepresentativeCacheLocation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepresentative(representative: RepresentativeCacheDataItem)

    @Query("DELETE FROM representatives WHERE city_state = :location")
    suspend fun deleteRepresentatives(location: String)

}