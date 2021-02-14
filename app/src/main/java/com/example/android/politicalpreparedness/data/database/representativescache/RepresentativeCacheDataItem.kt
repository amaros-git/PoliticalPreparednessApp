package com.example.android.politicalpreparedness.data.database.representativescache

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "representatives")
data class RepresentativeCacheDataItem(
        @PrimaryKey(autoGenerate = true) val id: Long,
        @ColumnInfo(name = "city_state") val cityState: String, //this is a key to link with Representative Cache Location
        @ColumnInfo(name = "city") val country: String,
        @ColumnInfo(name = "state") val state: String,
        @ColumnInfo(name = "name") val name: String? = null,
        @ColumnInfo(name = "party_name") val partyName: String? = null,
        @ColumnInfo(name = "photo_url") val photoUrl: String? = null,
        @ColumnInfo(name = "twitter_id") val twitterId: String? = null,
        @ColumnInfo(name = "facebook_id") val facebookId: String? = null,
        @ColumnInfo(name = "division_id") val divisionId: String? = null,
        @ColumnInfo(name = "www_url") val wwwUrl: String? = null
)
