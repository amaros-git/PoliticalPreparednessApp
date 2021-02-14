package com.example.android.politicalpreparedness.data.database.representativescache

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "representatives")
data class RepresentativeCacheDataItem(
        @PrimaryKey(autoGenerate = true) val id: Long,
        @ColumnInfo(name = "country_state") val countryState: String,
        @ColumnInfo(name = "name") val name: String? = null,
        @ColumnInfo(name = "party_name") val partyName: String? = null,
        @ColumnInfo(name = "photo_url") val photoUrl: String? = null,
        @ColumnInfo(name = "twitter_Url") val twitterUrl: String? = null,
        @ColumnInfo(name = "facebook_url") val facebookUrl: String? = null,
        @ColumnInfo(name = "www_url") val wwwUrl: String? = null
)
