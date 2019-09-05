package com.example.pc.dijkstraatkaist

import android.location.Location

class Edge(f: Node, s: Node) {
    val first: Node = f
    val second: Node = s
    val length: Float
        get() {
            val results = FloatArray(1)
            Location.distanceBetween(
                first.coordinates.latitude, first.coordinates.longitude,
                second.coordinates.latitude, second.coordinates.longitude,
                results
            )
            return results[0]
        }

    override fun toString(): String {
        return "Edge($first <=> $second, $length m)"
    }
}