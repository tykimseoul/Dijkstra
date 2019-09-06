package com.example.pc.dijkstraatkaist

import android.graphics.Color
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.MultipartPathOverlay

class Graph {
    val nodes = mutableSetOf<Node>()
    val edges = mutableListOf<Edge>()
    var path: MultipartPathOverlay? = null
    val markers = mutableListOf<Marker>()
    var selectedNode: Node? = null
        set(value) {
            if (value !in nodes) {
                value?.idx = nodes.size
                field = value
            } else {
                field = nodes.find { it == value }
            }
        }

    private fun addNewNode(latLng: LatLng) {
        Node(nodes.size, latLng).let {
            if (it !in nodes) {
                nodes.add(it)
            }
        }
    }

    fun linkNode(latLng: LatLng) {
        addNewNode(latLng)
        selectedNode?.let { selected ->
            nodes.find { it.coordinates == latLng }?.let {
                edges.add(Edge(edges.size, selected, it))
            }
        }
    }

    companion object {
        fun generatePath(edges: List<Edge>): MultipartPathOverlay {
            return MultipartPathOverlay().apply {
                coordParts = edges.map { listOf(it.first.coordinates, it.second.coordinates) }
                colorParts =
                    edges.map { MultipartPathOverlay.ColorPart(Color.RED, Color.WHITE, Color.GRAY, Color.LTGRAY) }
            }
        }

        fun findNodes(edges: List<Edge>): MutableSet<Node> {
            val nodeSet = mutableSetOf<Node>()
            edges.forEach {
                nodeSet.add(it.first)
                nodeSet.add(it.second)
            }
            return nodeSet
        }
    }

    override fun toString(): String {
        return "Graph(selected: $selectedNode edges:$edges)"
    }
}