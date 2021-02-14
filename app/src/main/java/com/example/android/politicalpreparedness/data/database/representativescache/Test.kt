package com.example.android.politicalpreparedness.data.database.representativescache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test")
data class Test (
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "city_state") val cityState: String
)