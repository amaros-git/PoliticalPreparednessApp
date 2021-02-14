package com.example.android.politicalpreparedness.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ElectionResponse(
        val kind: String,
        val elections: List<Election>
)