package com.example.android.politicalpreparedness.data.database

import androidx.room.*
import com.example.android.politicalpreparedness.data.models.Division
import com.squareup.moshi.*
import java.util.*

/**
 * For Room update query.
 */
@Entity
data class ElectionUpdate(
        @ColumnInfo(name="id") val id: Int,
        @ColumnInfo(name = "name")val name: String,
        @ColumnInfo(name = "electionDay")val electionDay: Date,
        @Embedded(prefix = "division_") @Json(name="ocdDivisionId") val division: Division
)