package com.no5ing.benchmark

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @RequiresApi(Build.VERSION_CODES.P)
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @RequiresApi(Build.VERSION_CODES.P)
    @Test
    fun startup() =
        baselineProfileRule.collect(
            packageName = "com.no5ing.bbibbi",
            maxIterations = 5,
        ) {
            pressHome()
            startActivityAndWait {
                it.putExtra("MACRO_TEST", "true")
            }
            device.waitAndFind(By.text("설정 및 개인정보"), 10_000)
        }

}