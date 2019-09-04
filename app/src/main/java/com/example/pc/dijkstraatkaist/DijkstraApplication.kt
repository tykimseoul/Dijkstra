package com.example.pc.dijkstraatkaist

import android.app.Application
import com.naver.maps.map.NaverMapSdk

class DijkstraApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient(BuildConfig.APP_KEY)
    }
}