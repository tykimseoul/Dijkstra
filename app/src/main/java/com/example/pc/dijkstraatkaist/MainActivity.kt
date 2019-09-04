package com.example.pc.dijkstraatkaist

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback, NaverMap.OnLocationChangeListener {


    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    val CODE_MULTIPLE_PERMISSIONS = 10

    private val permissions = arrayOf(
        ACCESS_FINE_LOCATION
    )
    private var missingPermissions: MutableList<Int>? = null
    private lateinit var naverMap: NaverMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestAllPermissions()
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: NaverMap) {
        naverMap = p0
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        naverMap.addOnLocationChangeListener(this)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            Log.e("locccc", it.toString())
            val cameraUpdate = CameraUpdate.toCameraPosition(CameraPosition(LatLng(it.latitude, it.longitude), 15.0))
                .animate(CameraAnimation.Easing)
            naverMap.moveCamera(cameraUpdate)
        }
        Toast.makeText(this, "map ready", Toast.LENGTH_LONG).show()
    }

    override fun onLocationChange(p0: Location) {
        Log.e("location", p0.toString())
        val cameraUpdate = CameraUpdate.toCameraPosition(CameraPosition(LatLng(p0.latitude, p0.longitude), 15.0))
            .animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)
    }

    private fun requestAllPermissions() {
        missingPermissions?.apply {
            if (isNotEmpty()) {
                forEach { requestPermissions(arrayOf(permissions[it]), CODE_MULTIPLE_PERMISSIONS) }
            }
            return
        }
        requestPermissions(permissions, CODE_MULTIPLE_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CODE_MULTIPLE_PERMISSIONS -> if (grantResults.isNotEmpty()) {
                missingPermissions = grantResults.indices.filter {
                    grantResults[it] != PackageManager.PERMISSION_GRANTED
                }.toMutableList()
            }
        }
    }
}