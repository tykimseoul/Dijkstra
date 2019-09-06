package com.example.pc.dijkstraatkaist

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.naver.maps.map.overlay.InfoWindow


class DijkstraInfoWindowAdapter(private val context: Context) : InfoWindow.ViewAdapter() {
    override fun getView(p0: InfoWindow): View {
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.info_window, null)
        val tvText = v.findViewById(R.id.text2) as TextView
        tvText.text = p0.marker?.position.toString()
        return v
    }
}