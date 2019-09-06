package com.example.pc.dijkstraatkaist

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PointF
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback, NaverMap.OnLocationChangeListener {


    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private val CODE_MULTIPLE_PERMISSIONS = 10

    private val permissions = arrayOf(
        ACCESS_FINE_LOCATION
    )
    private var missingPermissions: MutableList<Int>? = null
    private var naverMap: NaverMap? = null
    private val myMarker: Marker = Marker()
    private var path: MultipartPathOverlay? = null
    private val markers = mutableListOf<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestAllPermissions()
        setSupportActionBar(toolbar as Toolbar)
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
        naverMap?.let {
            loadGraph()
        }
    }

    private fun loadGraph() {
        Observable.fromCallable {
            GraphDatabase.getDatabase(this).edgeDAO().getAllEdges()
        }.doOnNext { list ->
            list?.forEach {
                Log.e("edge", it.toString())
            }
            runOnUiThread {
                path?.map = null
                path = Graph.generatePath(list)
                path?.map = naverMap
                markers.forEach { it.map = null }
                markers.clear()
                Graph.findNodes(list).forEach {
                    val window = InfoWindow()
                    window.adapter = DijkstraInfoWindowAdapter(this@MainActivity)
                    markers.add(
                        Marker().apply {
                            position = it.coordinates
                            icon = OverlayImage.fromResource(R.drawable.ic_marker_black_48dp)
                            anchor = PointF(0.5f, 1.0f)
                            onClickListener = Overlay.OnClickListener {
                                if (window.marker == null) {
                                    window.open(this)
                                } else {
                                    window.close()
                                }
                                true
                            }
                            map = naverMap
                        }
                    )
                }
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
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
        naverMap = p0
        naverMap?.apply {
            locationTrackingMode = LocationTrackingMode.Follow
            uiSettings.apply {
                isZoomControlEnabled = false
            }
            addOnLocationChangeListener(this@MainActivity)
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            val cameraUpdate = CameraUpdate.toCameraPosition(CameraPosition(LatLng(it.latitude, it.longitude), 15.0)).animate(CameraAnimation.Easing)
            naverMap?.moveCamera(cameraUpdate)
            myMarker.position = LatLng(it.latitude, it.longitude)
            myMarker.map = naverMap
        }
        loadGraph()
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