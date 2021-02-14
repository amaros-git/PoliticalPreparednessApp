package com.example.android.politicalpreparedness.utils

import android.content.Context
import com.example.android.politicalpreparedness.R
import com.squareup.moshi.JsonDataException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun convertExceptionToToastString(context: Context, e: Exception): String =
        when (e) {
            is HttpException -> {
                e.printStackTrace()
                context.getString(R.string.cannot_connect_services)
            }
            is SocketTimeoutException -> {
                e.printStackTrace()
                context.getString(R.string.cannot_connect_services)
            }
            is JsonDataException -> {
                e.printStackTrace()
                context.getString(R.string.wrong_response)
            }
            is IOException -> {
                e.printStackTrace()

                if (e is UnknownHostException) { //because UnknownHostException extends IOException
                    context.getString(R.string.cannot_connect_services_check)
                } else {
                    context.getString(R.string.please_retart_app)
                }
            }
            else -> {
                e.printStackTrace()
                context.getString(R.string.please_retart_app)
            }
        }