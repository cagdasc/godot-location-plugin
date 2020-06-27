/*
Copyright 2020 Cagdas Caglak(cagdascaglak@gmail.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.cacaosd.locationplugin

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import org.godotengine.godot.Dictionary
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo

class LocationKotlinPlugin(godot: Godot) : GodotPlugin(godot) {

    enum class ErrorCodes(val errorCode: Int, val message: String) {
        ACTIVITY_NOT_FOUND(100, "Godot Activity is null!"),
        LOCATION_UPDATES_NULL(
            101,
            "Location Updates object is null!"
        ),
        LAST_KNOWN_LOCATION_NULL(
            102,
            "Last Know Location object is null!"
        ),
        LOCATION_PERMISSION_MISSING(103, "Missing location permissions!");

    }

    private var mLocationCallback: LocationCallback? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private var locationUpdatesStart = false

    private val locationUpdateSignal =
        SignalInfo("onLocationUpdates", Dictionary::class.java)
    private val lastKnownLocationSignal =
        SignalInfo("onLastKnownLocation", Dictionary::class.java)
    private val errorSignal =
        SignalInfo("onLocationError", Int::class.javaObjectType, String::class.java)

    override fun onMainCreate(activity: Activity?): View? {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        return super.onMainCreate(activity)
    }

    override fun getPluginSignals(): Set<SignalInfo> {
        return setOf(locationUpdateSignal, lastKnownLocationSignal, errorSignal)
    }

    override fun getPluginMethods(): List<String> {
        return listOf(
            "startLocationUpdates",
            "stopLocationUpdates",
            "getLastKnowLocation"
        )
    }

    override fun getPluginName(): String {
        return "LocationPlugin"
    }

    private fun initializeLocationCallback() {
        if (activity == null) {
            emitError(errorSignal, ErrorCodes.ACTIVITY_NOT_FOUND)
            return
        }
        emitError(errorSignal, ErrorCodes.LOCATION_PERMISSION_MISSING)
        locationUpdatesStart = true
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.forEach {
                    emitLocation(locationUpdateSignal, it)
                }
            }
        }
    }

    fun startLocationUpdates(interval: Int, maxWaitTime: Int) {
        if (locationUpdatesStart) return
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(
                "GODOT",
                "If you want to location requests, you need to give permission \"ACCESS_FINE_LOCATION\"."
            )
            emitError(errorSignal, ErrorCodes.LOCATION_PERMISSION_MISSING)
            return
        }
        initializeLocationCallback()
        val request = LocationRequest()
        request.interval = interval.toLong()
        request.maxWaitTime = maxWaitTime.toLong()
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mFusedLocationClient!!.requestLocationUpdates(request, mLocationCallback, null)
        Log.d("GODOT", "Location update started.")
    }

    fun stopLocationUpdates() {
        if (locationUpdatesStart && mLocationCallback != null) {
            locationUpdatesStart = false
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
            Log.d("GODOT", "Location update stopped.")
        }
    }

    fun getLastKnowLocation() {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(
                "GODOT",
                "If you want to location requests, you need to give permission \"ACCESS_FINE_LOCATION\"."
            )
            emitError(errorSignal, ErrorCodes.LOCATION_PERMISSION_MISSING)
            return
        }

        mFusedLocationClient!!.lastLocation.addOnSuccessListener(activity!!) { location ->
            if (location != null) {
                emitLocation(lastKnownLocationSignal, location)
            } else {
                emitError(errorSignal, ErrorCodes.LAST_KNOWN_LOCATION_NULL)
            }
        }

        mFusedLocationClient!!.lastLocation.addOnFailureListener {
            emitError(errorSignal, ErrorCodes.LAST_KNOWN_LOCATION_NULL)
        }
    }

    private fun emitLocation(signalInfo: SignalInfo, location: Location) {
        val longitude = location.longitude.toFloat()
        val latitude = location.latitude.toFloat()
        val accuracy = location.accuracy
        var verticalAccuracyMeters = 0.0f
        val altitude = location.altitude.toFloat()
        val speed = location.speed
        val time = location.time.toInt()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            verticalAccuracyMeters = location.verticalAccuracyMeters
        }
        val locationDictionary = Dictionary()
        locationDictionary["longitude"] = longitude
        locationDictionary["latitude"] = latitude
        locationDictionary["accuracy"] = accuracy
        locationDictionary["verticalAccuracyMeters"] = verticalAccuracyMeters
        locationDictionary["altitude"] = altitude
        locationDictionary["speed"] = speed
        locationDictionary["time"] = time
        emitSignal(signalInfo.name, locationDictionary)
    }

    private fun emitError(signalInfo: SignalInfo, errorCodes: ErrorCodes) {
        emitSignal(signalInfo.name, errorCodes.errorCode, errorCodes.message)
    }
}