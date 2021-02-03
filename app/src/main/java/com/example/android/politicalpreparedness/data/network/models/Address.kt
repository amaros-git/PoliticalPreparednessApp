package com.example.android.politicalpreparedness.data.network.models

data class Address (
        val line1: String?, // nullable, because, e.g. we are in the wood :) The same for zip
        val line2: String? = null, //optional
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