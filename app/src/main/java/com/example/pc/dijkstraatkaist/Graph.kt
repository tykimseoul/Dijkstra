package com.example.pc.dijkstraatkaist

import android.graphics.Color
import android.util.Log
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.MultipartPathOverlay

class Graph {
    val nodes = mutableSetOf<Node>()
    val edges = mutableListOf<Edge>()
    var path: MultipartPathOverlay? = null
    val markers = mutableListOf<Marker>()
    val selectedNodes = mutableListOf<Node?>()
    val selectedNode: Node?
        get() = selectedNodes.lastOrNull { it != null }

    var movingNode: Node? = null

    private fun addNode(latLng: LatLng) {
        Node(nodes.size, latLng).let {
            if (it !in nodes) {
                nodes.add(it)
            }
        }
    }

    fun linkNode(latLng: LatLng) {
        addNode(latLng)
        selectedNode?.let { selected ->
            nodes.find { it.coordinates == latLng }?.let {
                if (it.coordinates != selected.coordinates)
                    edges.add(Edge(edges.size, selected, it))
            }
        }
    }

    fun linkNode(first: LatLng, second: LatLng) {
        val f = nodes.find { it.coordinates == first } as Node
        val s = nodes.find { it.coordinates == second } as Node
        edges.add(Edge(edges.size, f, s))
    }

    fun removeNode(){
        selectedNode?.let {selected->
            edges.removeIf { (it.first== selected) or (it.second==selected)}
            nodes.remove(selected)
            selectedNodes.remove(selected)
        }
    }

    fun select(node: Node) {
        if (nodes.find { it.coordinates == node.coordinates } == null) {
            node.idx = nodes.size
            selectedNodes.addOrElse(node)
        } else {
            selectedNodes.addOrElse(nodes.find { it.coordinates == node.coordinates } as Node)
        }
    }

    private fun MutableList<Node?>.addOrElse(node: Node) {
        if (selectedNode != null) {
            if (selectedNode != node) {
                if (size >= 2) {
                    removeAt(0)
                    add(node)
                } else {
                    add(node)
                }
            }
        } else {
            add(node)
        }
    }

    companion object {
        fun generatePath(edges: List<Edge>): MultipartPathOverlay? {
            return if (edges.isNotEmpty()) {
                MultipartPathOverlay().apply {
                    coordParts = edges.map { listOf(it.first.coordinates, it.second.coordinates) }
                    colorParts = edges.map {
                        if (it.highlight) MultipartPathOverlay.ColorPart(Color.GREEN, Color.WHITE, Color.GRAY, Color.LTGRAY)
                        else MultipartPathOverlay.ColorPart(Color.RED, Color.WHITE, Color.GRAY, Color.LTGRAY)
                    }
                }
            } else {
                null
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