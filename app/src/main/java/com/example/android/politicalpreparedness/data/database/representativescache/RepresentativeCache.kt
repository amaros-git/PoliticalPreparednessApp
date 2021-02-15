package com.example.android.politicalpreparedness.data.database.representativescache

import androidx.room.Embedded
import androidx.room.Relation

/**
 *  How does representative caching work:
 *
 *  Cache is the Room database containing two tables:
 *  RepresentativeCacheLocation and RepresentativeCacheDataItem. Both tables are like "related"
 *  via city_state column. This is one-to-many relationship  city_state column is like
 *  unique key which is result of address.city + address.state.
 *  RepresentativeCache class represents a result of a Room query which retrieves
 *  all representatives which has required value in city_state column
 *
 *  Cache update/refresh mechanism is NOT implemented yet. So, before to refresh  the cache of
 *  representatives of the correspond city_state, you must remove all representatives
 *  for the correspond city_state
 */

data class RepresentativeCache(
        @Embedded val countryState: RepresentativeCacheLocation,
        @Relation(
                parentColumn = "city_state",
                entityColumn = "city_state"
        )
        val representatives: List<RepresentativeCacheDataItem>
)