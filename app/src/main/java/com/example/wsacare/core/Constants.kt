package com.example.wsacare.core

import okhttp3.OkHttpClient

object Constants {
    const val URL = "https://wsa2021.mad.hakta.pro/api"
    const val USER = "user"
    val okHttp =  OkHttpClient.Builder().build()
}