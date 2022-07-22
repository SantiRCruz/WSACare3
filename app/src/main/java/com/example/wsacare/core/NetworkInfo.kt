package com.example.wsacare.core

import android.content.Context
import android.hardware.camera2.params.Capability
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

fun networkInfo(context:Context):Boolean{
    val conn = context.getSystemService(ConnectivityManager::class.java)
    val network = conn.activeNetwork ?: return false
    val capabilities = conn.getNetworkCapabilities(network) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

}