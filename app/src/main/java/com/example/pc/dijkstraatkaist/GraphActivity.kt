package com.example.pc.dijkstraatkaist

import android.annotation.SuppressLint
import android.graphics.PointF
import android.support.v7.app.AppCompatActivity
import android.util.Log
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

abstract class GraphActivity:AppCompatActivity(), OnMapReadyCallback {
    val graph: Graph = Graph()
    var naverMap: NaverMap? = null
    private val myMarker: Marker = Marker()
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    fun loadGraph() {
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

    fun drawGraph() {
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

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: NaverMap) {
        naverMap = p0
        naverMap?.uiSettings?.isZoomControlEnabled = false
        fusedLocationClient.lastLocation.addOnSuccessListener {
            val cameraUpdate = CameraUpdate.toCameraPosition(CameraPosition(LatLng(it.latitude, it.longitude), 15.0)).animate(CameraAnimation.Easing)
            naverMap?.moveCamera(cameraUpdate)
            myMarker.position = LatLng(it.latitude, it.longitude)
            myMarker.map = naverMap
        }
        loadGraph()
    }
}