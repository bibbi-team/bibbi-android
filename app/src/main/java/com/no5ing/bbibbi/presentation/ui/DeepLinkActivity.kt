package com.no5ing.bbibbi.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class DeepLinkActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(isTaskRoot) {
            Timber.d("[DeepLink] New Intent Activity!!")
        } else{
            Timber.d("[DeepLink] Already Task Exists!!")
           // finish()
        }

        val mainIntent = intent
        mainIntent.setClass(this, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        startActivity(mainIntent)
        finish()

    }
}