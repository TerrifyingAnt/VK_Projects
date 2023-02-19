package jg.com.vk_projects

import android.app.Activity
import android.content.Context
import android.net.wifi.WifiManager
import androidx.test.core.app.ApplicationProvider
import junit.framework.Assert.*
import org.junit.Test


class Tests {

    @Test
    fun test1() {
        //когда wi-fi включен должно возвращать true
        val context = ApplicationProvider.getApplicationContext<Context>()
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = true

        val result = MainActivity().isWifiEnabled(context as Activity)

        assertTrue(result)
    }

    @Test
    fun test2() {
        //когда wi-fi выключен должно возвращать false
        val context = ApplicationProvider.getApplicationContext<Context>()
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = false

        val result = MainActivity().isWifiEnabled(context as Activity)

        assertFalse(result)
    }
}