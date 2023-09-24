package com.petpal.mungmate.services

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*

class TrackingService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private var startTimestamp: Long = 0L
    private var elapsedTime: String = "0"
    private var totalDistance: Float = 0.0f
    private var lastLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    companion object{
        val elapsedTimeLiveData = MutableLiveData<String>()
        val distanceMovedLiveData = MutableLiveData<Float>()
    }


    private val timerRunnable = object : Runnable {
        override fun run() {
            elapsedTime = ((System.currentTimeMillis() - startTimestamp) / 1000).toString()
            elapsedTimeLiveData.postValue(elapsedTime)  // LiveData 업데이트
            handler.postDelayed(this, 1000)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            p0 ?: return
            for (location in p0.locations) {
                updateDistance(location)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTimer()
        startLocationUpdates()
        return START_STICKY
    }

    private fun startTimer() {
        startTimestamp = System.currentTimeMillis()
        handler.post(timerRunnable)
    }

    private fun updateDistance(location: Location) {
        lastLocation?.let {
            val distance = it.distanceTo(location)
            totalDistance += distance
            distanceMovedLiveData.postValue(totalDistance)  // LiveData 업데이트
        }
        lastLocation = location
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 2000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    // elapsedTime 저장 함수
    private fun saveElapsedTime() {
        // 여기에서 elapsedTime를 저장하거나 처리할 수 있음
    }

    // totalDistance 저장 함수
    private fun saveTotalDistance() {
        // 여기에서 totalDistance를 저장하거나 처리할 수 있음
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timerRunnable)
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}