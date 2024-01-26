package com.no5ing.bbibbi.util

import com.mixpanel.android.mpmetrics.MixpanelAPI

class MixpanelWrapper {
    lateinit var mixpanelAPI: MixpanelAPI
    fun track(eventName: String) {
        mixpanelAPI.track(eventName)
    }
}