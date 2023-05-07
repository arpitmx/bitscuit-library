package com.bitpolarity.bitscuit.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

/*
File : NetworkStatusHelper.kt -> com.bitpolarity.bitscuit.helper
Description : Helper for network observation 

Author : Alok Ranjan (VC uname : apple)
Link : https://github.com/arpitmx
From : Bitpolarity x Noshbae (@Project : BitscuitLibApp Android)

Creation : 8:19 pm on 03/05/23

Todo >
Tasks CLEAN CODE : 
Tasks BUG FIXES : 
Tasks FEATURE MUST HAVE : 
Tasks FUTURE ADDITION :

*/



sealed class NetworkStatus {
    object Online :NetworkStatus()
    object Offline : NetworkStatus()
}


class NetworkStatusHelper(private val context : Context) : LiveData<NetworkStatus>() {

    val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    val valideNetworkConnections: ArrayList<Network> = ArrayList()

    fun getConnectivityiManagerCallback() =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                val networkCapability = connectivityManager.getNetworkCapabilities(network)
                val hasNetworkConnection =
                    networkCapability?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        ?: false
                if (hasNetworkConnection) {
                    // network connection available
                    CoroutineScope(Dispatchers.IO).launch{
                        if (InernetAvailablity.check()){
                            withContext(Dispatchers.Main){
                                valideNetworkConnections.add(network)
                                announceStatus()
                            }
                        }
                    }
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                valideNetworkConnections.remove(network)
                announceStatus()
            }


            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)

                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)){
                    CoroutineScope(Dispatchers.IO).launch{
                        if (InernetAvailablity.check()){
                            withContext(Dispatchers.Main){
                                valideNetworkConnections.add(network)
                                announceStatus()
                            }
                        }
                    }
                }
            }
        }


    private val connectivityManagerCallback by lazy {
        getConnectivityiManagerCallback()
    }

    override fun onActive() {
        super.onActive()
        val networkRequest = NetworkRequest
            .Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, connectivityManagerCallback)

    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
    }


    object InernetAvailablity {

        fun check() : Boolean {
            return try {
                val socket = Socket()
                socket.connect(InetSocketAddress("8.8.8.8",53))
                socket.close()
                true
            } catch ( e: Exception){
                e.printStackTrace()
                false
            }
        }

    }


    fun announceStatus() {
        if (valideNetworkConnections.isNotEmpty()) {
            postValue(NetworkStatus.Online)
        } else {
            postValue(NetworkStatus.Offline)
        }
    }

}



