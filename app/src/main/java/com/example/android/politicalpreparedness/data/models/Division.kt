package com.example.android.politicalpreparedness.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Division(
        val id: String,
        val country: String,
        var state: String //Change back to val once Google Civics API/elections returns valid state
): Parcelable