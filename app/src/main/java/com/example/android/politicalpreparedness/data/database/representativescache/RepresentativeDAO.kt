package com.example.android.politicalpreparedness.data.database.representativescache

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RepresentativeDAO {

    /*  @Insert(onConflict = OnConflictStrategy.REPLACE)
      suspend fun insertRepresentative(representative: RepresentativeCacheDataItem)
  */
    @Transaction
    @Query("SELECT * FROM test WHERE country_state = :location")
    fun observeRepresentatives(location: String): LiveData<RepresentativeCache>


    @Insert
    fun insertState(state: Test)

    @Insert
    fun insertRepresentative(representative: RepresentativeCacheDataItem)

}