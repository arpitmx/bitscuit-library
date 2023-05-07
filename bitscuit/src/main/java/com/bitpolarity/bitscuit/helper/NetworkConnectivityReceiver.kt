package com.bitpolarity.bitscuit.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi

/*
File : NetworkConnectivityReceiver.kt -> com.bitpolarity.bitscuit.helper
Description : Broadcast reciever for observing network activity 

Author : Alok Ranjan (VC uname : apple)
Link : https://github.com/arpitmx
From : Bitpolarity x Noshbae (@Project : BitscuitLibApp Android)

Creation : 11:31 pm on 03/05/23

Todo >
Tasks CLEAN CODE : 
Tasks BUG FIXES : 
Tasks FEATURE MUST HAVE : 
Tasks FUTURE ADDITION : 

*/



interface NetworkStatusCallback{
    fun connected()
    fun disconnected()
}


class NetworkConnectivityReceiver(val call: NetworkStatusCallback) : BroadcastReceiver(){



    lateinit var networkStatusCallback : NetworkStatusCallback

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && intent.action != null) {

            networkStatusCallback = call

            when (intent.action) {
                ConnectivityManager.CONNECTIVITY_ACTION -> {
                    val connectivityManager =
                        context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val network = connectivityManager.activeNetwork
                    val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                    if (networkCapabilities != null && networkCapabilities.hasCapability(
                            NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                        Toast.makeText(context, "Resumed Download", Toast.LENGTH_SHORT).show()
                        networkStatusCallback.connected()

                    } else {
                        Toast.makeText(context, "Paused Download", Toast.LENGTH_SHORT).show()
                        networkStatusCallback.disconnected()
                    }
                }
            }
        }
    }
}
