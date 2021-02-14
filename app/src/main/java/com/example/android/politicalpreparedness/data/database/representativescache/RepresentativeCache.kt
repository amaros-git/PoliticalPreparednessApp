package com.example.android.politicalpreparedness.data.database.representativescache

import androidx.room.Embedded
import androidx.room.Relation

data class RepresentativeCache(
        @Embedded val countryState: RepresentativeCacheLocation,
        @Relation(
                parentColumn = "city_state",
                entityColumn = "city_state"
        )
        val representatives: List<RepresentativeCacheDataItem>
)