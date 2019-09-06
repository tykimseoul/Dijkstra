package com.example.pc.dijkstraatkaist

import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_admin.*

class AdminActivity : AppCompatActivity(), OnMapReadyCallback {
    private val graph: Graph = Graph()

    private lateinit var naverMap: NaverMap
    private val myMarker: Marker = Marker()
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private var db: GraphDatabase? = null
    private var edgeDAO: EdgeDAO? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        link.setOnClickListener {
            naverMap.cameraPosition.target.let {
                graph.linkNode(it)
                graph.selectedNode = Node(0, it)
            }
            Log.e("graph", graph.toString())
            drawGraph()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: NaverMap) {
        naverMap = p0
        naverMap.uiSettings.isZoomControlEnabled = false
        fusedLocationClient.lastLocation.addOnSuccessListener {
            val cameraUpdate = CameraUpdate.toCameraPosition(CameraPosition(LatLng(it.latitude, it.longitude), 15.0)).animate(CameraAnimation.Easing)
            naverMap.moveCamera(cameraUpdate)
            myMarker.position = LatLng(it.latitude, it.longitude)
            myMarker.map = naverMap
        }
        loadGraph()
    }

    private fun saveGraph() {
        Observable.fromCallable {
            db = GraphDatabase.getDatabase(this)
            edgeDAO = db?.edgeDAO()

            with(edgeDAO) {
                graph.edges.forEach {
                    this?.insert(it)
                }
            }
            db?.edgeDAO()?.getAllEdges()
        }.doOnNext { list ->
            list?.forEach {
                Log.e("edge", it.toString())
            }
            finish()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    private fun loadGraph() {
        Observable.fromCallable {
            GraphDatabase.getDatabase(this).edgeDAO().getAllEdges()
        }.doOnNext { list ->
            list?.forEach {
                Log.e("edge", it.toString())
            }
            graph.edges.addAll(list)
            drawGraph()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    private fun drawGraph(){
        runOnUiThread {
            graph.path?.map = null
            graph.path = Graph.generatePath(graph.edges)
            graph.path?.map = naverMap
            graph.markers.forEach { it.map = null }
            graph.markers.clear()
            Graph.findNodes(graph.edges).forEach {
                graph.markers.add(
                    Marker().apply {
                        position = it.coordinates
                        icon = OverlayImage.fromResource(R.drawable.ic_marker_black_48dp)
                        anchor = PointF(0.5f, 1.0f)
                        onClickListener = Overlay.OnClickListener {
                            graph.selectedNode = Node(0, position)
                            true
                        }
                        map = naverMap
                    }
                )
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
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        saveGraph()
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
}
