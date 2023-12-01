/*
 * Copyright (C) 2023 The risingOS Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.systemui.statusbar

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.util.AttributeSet
import android.widget.ImageView
import com.android.systemui.tuner.TunerService
import com.android.systemui.tuner.TunerService.Tunable

import com.android.systemui.R
import com.android.systemui.Dependency

class WifiStandardImageView : ImageView {

    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var wifiStandardEnabled: Boolean = false
    private var isTunerRegistered: Boolean = false
    private var isRegistered: Boolean = false
    
    private val tunerService = Dependency.get(TunerService::class.java)

    private val WIFI_STANDARD_ICON: String =
            "system:" + "wifi_standard_icon"

    constructor(context: Context) : super(context) {
        registerTunerService(context)
        showWifiStandard(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        registerTunerService(context)
        showWifiStandard(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        registerTunerService(context)
        showWifiStandard(context)
    }

    private fun registerTunerService(context: Context) {
        if (isTunerRegistered) return
        tunerService.addTunable(object : TunerService.Tunable {
            override fun onTuningChanged(key: String?, value: String?) {
                val newValue = TunerService.parseIntegerSwitch(value, false)
                if (wifiStandardEnabled && !newValue) {
                    wifiStandardEnabled = newValue
                    unregisterNetworkCallback(context)
                } else if (!wifiStandardEnabled && newValue) {
                    wifiStandardEnabled = newValue
                    showWifiStandard(context)
                }
            }
        }, WIFI_STANDARD_ICON)
        isTunerRegistered = true
    }
    
    private fun showWifiStandard(context: Context) {
        if (!wifiStandardEnabled) return
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                networkCapabilities?.let { 
                    setWifiStandard(it) 
                }
            }
            override fun onLost(network: Network) {
                visibility = GONE
            }
        }
        registerNetworkCallback()
    }

    private fun setWifiStandard(networkCapabilities: NetworkCapabilities) {
        if (!wifiStandardEnabled) return
        post {
            val wifiStandard = getWifiStandard(context)
            if (wifiStandard >= 4) {
                val identifier = context.resources.getIdentifier(
                    "ic_wifi_standard_$wifiStandard", 
                    "drawable", 
                    context.packageName
                )
                if (identifier > 0) {
                    setImageDrawable(context.getDrawable(identifier))
                }
            }
        }
    }

    private fun getWifiStandard(context: Context): Int {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return if (networkCapabilities != null && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            val wifiInfo = wifiManager.connectionInfo
            wifiInfo.wifiStandard
        } else {
            -1 
        }
    }

    fun registerNetworkCallback() {
        if (isRegistered || !wifiStandardEnabled) return
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback!!)
        visibility = VISIBLE
        isRegistered = true
    }

    fun unregisterNetworkCallback(context: Context) {
        if (!isRegistered) return
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
        visibility = GONE
        isRegistered = false
    }
}
