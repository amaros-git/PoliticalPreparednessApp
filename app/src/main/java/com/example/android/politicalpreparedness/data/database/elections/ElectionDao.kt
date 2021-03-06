package com.example.android.politicalpreparedness.data.database.elections

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.data.models.Election

@Dao
interface ElectionDao {

    /**
     * Insert a election in the database. If the election already exists, replace it.
     *
     * @param election the election to be inserted.
     *
     * @throws SQLiteConstraintException is there is election with the same id. Use update
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertElection(election: Election)

    /**
     * Observes list of elections.
    *
    * @return all elections.
    */
    @Query("SELECT * FROM election_table")
    fun observeElections(): LiveData<List<Election>>


    @Update(entity = Election::class)
    suspend fun updateElection(electionUpdate: ElectionUpdate)

    @Query("UPDATE election_table SET isFollowed = :shouldFollow WHERE id = :electionId")
    suspend fun changeFollowingStatus(electionId: Int, shouldFollow: Boolean)

    @Query("SELECT * FROM election_table WHERE id = :electionId")
    suspend fun getElection(electionId: Int): Election?

}