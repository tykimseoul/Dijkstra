package com.example.pc.dijkstraatkaist

import com.naver.maps.geometry.LatLng

class Node(c: LatLng) {
    var idx: Int? = null
    val coordinates: LatLng = c

    override fun equals(other: Any?): Boolean {
        if (other as? Node == null) {
            return false
        }
        return this.coordinates == other.coordinates
    }

    override fun hashCode(): Int {
        return this.coordinates.hashCode()
    }

    override fun toString(): String {
        return "Node(idx: $idx, crd: $coordinates)"
    }
}