package com.example.android.politicalpreparedness.data.database.representativescache

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RepresentativeDAO {

    /*  @Insert(onConflict = OnConflictStrategy.REPLACE)
      suspend fun insertRepresentative(representative: RepresentativeCacheDataItem)
  */
    @Transaction
    @Query("SELECT * FROM test WHERE city_state = :location")
    suspend fun getRepresentatives(location: String): RepresentativeCache?


    @Insert
    suspend fun insertState(state: Test)

    @Insert
    suspend fun insertRepresentative(representative: RepresentativeCacheDataItem)

}