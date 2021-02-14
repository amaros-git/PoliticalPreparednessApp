package com.example.android.politicalpreparedness.data.database.representativeCache

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.politicalpreparedness.data.models.Division
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "election_table")
data class RepresentativeCacheData (
            @PrimaryKey val id: String, //id is country + state
            @ColumnInfo(name = "name")val name: String,
            @ColumnInfo(name = "electionDay")val electionDay: Date,
            @Embedded(prefix = "division_") @Json(name="ocdDivisionId") val division: Division,
            @ColumnInfo(name= "isFollowed") var isFollowed: Boolean = false //is changed later TODO should be it val and set on false on creation ?
    ): Parcelable
