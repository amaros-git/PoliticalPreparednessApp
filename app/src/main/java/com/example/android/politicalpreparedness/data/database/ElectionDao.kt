package com.example.android.politicalpreparedness.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.data.network.models.Election

@Dao
interface ElectionDao {

    /**
     * Insert a election in the database. If the election already exists, replace it.
     *
     * @param election the election to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElection(election: Election)

    /**
     * Observes list of elections.
    *
    * @return all elections.
    */
    @Query("SELECT * FROM election_table")
    fun observeElections(): LiveData<List<Election>>


    //TODO: Add select all election query

    //TODO: Add select single election query

    //TODO: Add delete query

    //TODO: Add clear query

}