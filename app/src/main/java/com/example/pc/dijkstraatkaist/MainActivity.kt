package com.example.pc.dijkstraatkaist

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*

class MainActivity : GraphActivity(), NaverMap.OnLocationChangeListener {
    private val CODE_MULTIPLE_PERMISSIONS = 10

    private val permissions = arrayOf(
        ACCESS_FINE_LOCATION
    )
    private var missingPermissions: MutableList<Int>? = null
    private val myMarker: Marker = Marker()
    private val selectedNodes = Array<Node?>(2) { null }

    private val sheetBehavior by lazy { BottomSheetBehavior.from(bottom_sheet) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestAllPermissions()
        setSupportActionBar(toolbar as Toolbar)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        bottom_sheet.setOnClickListener {
            if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        start.setOnClickListener {
            selectedNodes[0] = graph.selectedNode
            if ((selectedNodes[0] != null) and (selectedNodes[1] != null)) {
                val path = DijkstraUtil(graph).shortestPath(selectedNodes[0] as Node, selectedNodes[1] as Node)
                distance.text = path.second.toString()
                updatePath()
            }
        }
        end.setOnClickListener {
            selectedNodes[1] = graph.selectedNode
            if ((selectedNodes[0] != null) and (selectedNodes[1] != null)) {
                val path = DijkstraUtil(graph).shortestPath(selectedNodes[0] as Node, selectedNodes[1] as Node)
                distance.text = path.second.toString()
                updatePath()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        naverMap?.let {
            loadGraph()
        }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.edit -> {
                val intent = Intent(this, AdminActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: NaverMap) {
        super.onMapReady(p0)
        naverMap?.apply {
            locationTrackingMode = LocationTrackingMode.Follow
            addOnLocationChangeListener(this@MainActivity)
        }
    }

    override fun onLocationChange(p0: Location) {
        Log.e("location", p0.toString())
        val cameraUpdate = CameraUpdate.toCameraPosition(CameraPosition(LatLng(p0.latitude, p0.longitude), 15.0)).animate(CameraAnimation.Easing)
        naverMap?.moveCamera(cameraUpdate)
        myMarker.position = LatLng(p0.latitude, p0.longitude)
        myMarker.map = naverMap
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