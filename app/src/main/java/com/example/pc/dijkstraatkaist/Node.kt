package com.example.pc.dijkstraatkaist

import android.arch.persistence.room.Ignore
import com.naver.maps.geometry.LatLng

data class Node(var idx: Int = -1, var coordinates: LatLng) {

    @Ignore
    constructor(idx: Int) : this(idx, LatLng(0.0, 0.0))

    override fun equals(other: Any?): Boolean {
        if (other as? Node == null) {
            return false
        }
        return this.idx == other.idx
    }

    override fun hashCode(): Int {
        return this.coordinates.hashCode()
    }

    override fun toString(): String {
        return "Node(idx: $idx, crd: $coordinates)"
    }
}