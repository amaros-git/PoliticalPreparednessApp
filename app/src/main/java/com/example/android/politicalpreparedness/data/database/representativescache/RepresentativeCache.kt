package com.example.android.politicalpreparedness.data.database.representativescache

import androidx.room.Embedded
import androidx.room.Relation

data class RepresentativeCache(
        @Embedded val countryState: Test,
        @Relation(
                parentColumn = "country_state",
                entityColumn = "country_state"
        )
        val representatives: List<RepresentativeCacheDataItem>
)