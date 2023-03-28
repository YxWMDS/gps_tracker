package com.example.gpstracker.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.gpstracker.MainActivity
import com.example.gpstracker.R
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY

class LocationService: Service() {
    private lateinit var locRequest: LocationRequest
    private lateinit var fusedLocProvider: FusedLocationProviderClient

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startNotification()
        getLocationUpdates()
        isRunning = true
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        initLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        fusedLocProvider.removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object: LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.lastLocation?.latitude
        }
    }

    private fun startNotification(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val nChannel = NotificationChannel(
                CHANNEL_ID,
                "Трекер прогулок",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val nManager = getSystemService(NotificationManager::class.java) as NotificationManager
            nManager.createNotificationChannel(nChannel)
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_MUTABLE)
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_IMMUTABLE)
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        }
        val notification = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        ).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(getString(R.string.tracker_is_working))
            .setContentIntent(pendingIntent).build()
        startForeground(99, notification)

    }

    private fun initLocation(){
        locRequest = LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 5000).build()
        fusedLocProvider = LocationServices.getFusedLocationProviderClient(baseContext)
    }

    private fun getLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocProvider.requestLocationUpdates(
            locRequest,
            locationCallback,
            Looper.myLooper()
            )
    }

    companion object{
        const val CHANNEL_ID = "channel_1"
        var isRunning = false
        var startTime = 0L;
    }
}