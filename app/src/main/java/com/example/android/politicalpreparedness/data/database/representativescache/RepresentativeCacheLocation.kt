package com.example.android.politicalpreparedness.data.database.representativescache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "representative_locations")
data class RepresentativeCacheLocation (
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "city_state") val cityState: String
)