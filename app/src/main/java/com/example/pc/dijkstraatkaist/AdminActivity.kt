package com.example.pc.dijkstraatkaist

import android.os.Bundle
import android.util.Log
import com.naver.maps.geometry.LatLng
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_admin.*

class AdminActivity : GraphActivity() {

    private var db: GraphDatabase? = null
    private var edgeDAO: EdgeDAO? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        branch.setOnClickListener {
            naverMap?.cameraPosition?.target?.let {
                graph.linkNode(it)
                graph.select(Node(0, it))
            }
            Log.e("graph", graph.toString())
            updatePath()
            updateMarkers()
        }
        move.setOnClickListener {
            if (graph.movingNode == null) {
                graph.movingNode = graph.selectedNode
                move.text = "drop"
            } else {
                naverMap?.cameraPosition?.target?.let {
                    graph.edges.forEach { edge ->
                        if (edge.first == graph.movingNode) {
                            edge.first.coordinates = it
                        }
                        if (edge.second == graph.movingNode) {
                            edge.second.coordinates = it
                        }
                    }
                    graph.nodes.find { node -> node.idx == graph.movingNode?.idx }?.coordinates = it
                    graph.movingNode = null
                    updatePath()
                    updateMarkers()
                }
                move.text = "move"
            }
        }
        connect.setOnClickListener {
            if (graph.selectedNodes.filter { it != null }.size == 2) {
                graph.linkNode(graph.selectedNodes[0]?.coordinates as LatLng, graph.selectedNodes[1]?.coordinates as LatLng)
                updatePath()
                updateMarkers()
            }
        }
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
