package com.example.gpstracker.ui

import android.content.Context
import android.Manifest
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.gpstracker.BuildConfig
import com.example.gpstracker.R
import com.example.gpstracker.databinding.FragmentHomeBinding
import com.example.gpstracker.utils.DialogManager
import com.example.gpstracker.utils.LocationService
import com.example.gpstracker.utils.TimeUtils
import com.example.gpstracker.utils.checkPermission
import org.osmdroid.config.Configuration
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.*

class HomeFragment: Fragment() {
    private var isServiceRunning: Boolean = false
    private lateinit var binding: FragmentHomeBinding
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var timer: Timer
    private var startTime = 0L
    private val timeData = MutableLiveData<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadMap()
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerPermissions()
        setOnClicks()
        checkServiceState()
        updateTime()
    }

    override fun onResume() {
        super.onResume()
        checkLocPermission()
    }

    private fun updateTime(){
        timeData.observe(viewLifecycleOwner){
            binding.tvTime.text = it
        }
    }

    private fun startTimer(){
        timer = Timer()
        startTime = LocationService.startTime
        timer.schedule(object: TimerTask(){
            override fun run() {
                activity?.runOnUiThread {
                    timeData.value = getTripTime()
                }
            }

        }, 1000, 1000)
    }

    private fun getTripTime():String{
        return "Время: ${TimeUtils.getTime(System.currentTimeMillis() - startTime)}"
    }

    private fun setOnClicks() = with(binding){
        val listener = onCLicks()
        fabStartStop.setOnClickListener(listener)
    }

    private fun onCLicks(): View.OnClickListener{
        return View.OnClickListener {
            when(it.id){
                R.id.fabStartStop -> startStopLocService()
            }
        }
    }

    private fun startStopLocService(){
        if(!isServiceRunning){
            startLocService()
        }else{
            activity?.stopService(Intent(activity, LocationService::class.java))
            binding.fabStartStop.setImageResource(R.drawable.ic_play)
            timer.cancel()
        }
        isServiceRunning = !isServiceRunning
    }

    private fun startLocService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.startForegroundService(Intent(activity, LocationService::class.java))
        }else{
            activity?.startService(Intent(activity, LocationService::class.java))
        }
        binding.fabStartStop.setImageResource(R.drawable.ic_stop)
        LocationService.startTime = System.currentTimeMillis()
        startTimer()
    }

    private fun loadMap(){
        Configuration.getInstance().load(
            activity as AppCompatActivity,
            activity?.getSharedPreferences("load_map_pref", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    private fun checkServiceState(){
        isServiceRunning = LocationService.isRunning
        if(isServiceRunning){
            binding.fabStartStop.setImageResource(R.drawable.ic_stop)
        }
    }

    private fun initOpenStreetMap() = with(binding){
        map.controller.setZoom(17.0)
        val myLocProvider = GpsMyLocationProvider(activity)
        val myLocOverlay = MyLocationNewOverlay(myLocProvider, map)
        myLocOverlay.enableMyLocation()
        myLocOverlay.enableFollowLocation()
        myLocOverlay.runOnFirstFix {
            map.overlays.clear()
            map.overlays.add(myLocOverlay)
        }
    }

    private fun registerPermissions(){
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){
            if(it[Manifest.permission.ACCESS_FINE_LOCATION] == true){
                initOpenStreetMap()
            }else{
                Toast.makeText(activity as AppCompatActivity, "Вы не дали разрешение на отслеживание местоположения", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkLocPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            checkPermissionsAfterQ()
        }else{
            checkPermissionBeforeQ()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermissionsAfterQ(){
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            && checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
            initOpenStreetMap()
            isLocationEnabled()
        }else{
            pLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            )
        }
    }

    private fun checkPermissionBeforeQ(){
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)){
            initOpenStreetMap()
            isLocationEnabled()
        }else{
            pLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    private fun isLocationEnabled(){
        val lManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if(!isEnabled){
            DialogManager.showLocationDialog(
                activity as AppCompatActivity,
                object : DialogManager.Listener{
                    override fun onAcceptClick() {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }

                }
            )
            Toast.makeText(activity as AppCompatActivity, "location disabled", Toast.LENGTH_LONG).show()
        }
    }

    companion object{
        @JvmStatic
        fun newInstance() = HomeFragment()
    }


}