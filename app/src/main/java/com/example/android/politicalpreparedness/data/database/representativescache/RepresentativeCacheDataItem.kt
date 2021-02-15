package com.example.android.politicalpreparedness.data.database.representativescache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "representatives")
data class RepresentativeCacheDataItem(
        @PrimaryKey(autoGenerate = true) val id: Long,
        @ColumnInfo(name = "city_state") val cityState: String, //this is a key to link with Representative Cache Location
        @ColumnInfo(name = "city") val city: String,
        @ColumnInfo(name = "state") val state: String,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "party_name") val partyName: String?,
        @ColumnInfo(name = "photo_url") val photoUrl: String? = null,
        @ColumnInfo(name = "twitter_id") val twitterId: String? = null,
        @ColumnInfo(name = "facebook_id") val facebookId: String? = null,
        @ColumnInfo(name = "www_url") val wwwUrl: String? = null,
        @ColumnInfo(name = "division_id") val divisionId: String
)
