package com.example.pc.dijkstraatkaist

import com.naver.maps.geometry.LatLng

class Graph {
    val nodes = mutableSetOf<Node>()
    val edges = mutableListOf<Edge>()
    var selectedNode: Node? = null
        set(value) {
            if (value !in nodes) {
                value?.idx = nodes.size
                field = value
            } else {
                field = nodes.find { it == value }
            }
        }

    fun addNewNode(latLng: LatLng) {
        Node(latLng).let {
            if (it !in nodes) {
                it.idx = nodes.size
                nodes.add(it)
            }
        }
    }

    fun linkNode(latLng: LatLng) {
        addNewNode(latLng)
        selectedNode?.let { selected ->
            nodes.find { it.coordinates == latLng }?.let {
                edges.add(Edge(selected, it))
            }
        }
    }

    override fun toString(): String {
        return "Graph(selected: $selectedNode edges:$edges)"
    }
}