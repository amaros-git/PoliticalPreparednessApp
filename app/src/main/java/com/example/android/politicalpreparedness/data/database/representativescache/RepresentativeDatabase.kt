package com.example.android.politicalpreparedness.data.database.representativescache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Test::class, RepresentativeCacheDataItem::class], version = 1, exportSchema = false)
abstract class RepresentativeDatabase: RoomDatabase() {

    abstract val representativeDAO: RepresentativeDAO

    companion object {

        @Volatile
        private var INSTANCE: RepresentativeDatabase? = null

        fun getInstance(context: Context): RepresentativeDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            RepresentativeDatabase::class.java,
                            "representative_database"
                    )
                            .fallbackToDestructiveMigration()
                            .build()

                    INSTANCE = instance
                }

                return instance
            }
        }

    }

}