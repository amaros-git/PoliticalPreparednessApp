package com.example.android.politicalpreparedness.data.network.jsonadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*


class DateAdapter {
    @ToJson
    fun toJson(value: Date): String = FORMATTER.format(value)

    @FromJson
    fun fromJson(value: String): Date = FORMATTER.parse(value)

    companion object {
        private val FORMATTER =
                SimpleDateFormat("yyyy-MM-dd", Locale.US)
    }
}