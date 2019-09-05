package com.example.pc.dijkstraatkaist

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import kotlinx.android.synthetic.main.activity_admin.*

class AdminActivity : AppCompatActivity(), OnMapReadyCallback {
    private val graph: Graph = Graph()

    private lateinit var naverMap: NaverMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        link.setOnClickListener {
            naverMap.cameraPosition.target.let {
                graph.linkNode(it)
                graph.selectedNode = Node(it)
            }
            Log.e("graph", graph.toString())
            try {
                graph.generatePath().map = naverMap
            } catch (e: IllegalArgumentException) {

            }
        }
    }

    override fun onMapReady(p0: NaverMap) {
        naverMap = p0
        Toast.makeText(this, "map ready", Toast.LENGTH_LONG).show()
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
}
