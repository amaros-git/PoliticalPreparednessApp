package com.example.android.politicalpreparedness.data.models

data class Address (
        val line1: String,
        val line2: String?,
        val city: String,
        val state: String,
        val zip: String?
) {
    fun toFormattedString(): String {
        var output = line1.plus("\n")
        if (!line2.isNullOrEmpty()) output = output.plus(line2).plus("\n")
        output = output.plus("$city, $state $zip")
        return output
    }
}