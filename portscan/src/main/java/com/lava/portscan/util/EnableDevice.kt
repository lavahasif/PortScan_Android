package com.shersoft.android_ip.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.LocalOnlyHotspotCallback
import android.net.wifi.WifiManager.LocalOnlyHotspotReservation
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi

import java.lang.reflect.Method


class EnableDevice(contexts: Context) : ConnectedDevice(contexts) {
//    interface Ihotspot {
//        fun onEnableHotspot(hospot: Hotspot);
//    }
//
//    fun SetHotspotEnable(callback: Ihotspot): Pigeon.Hotspot? {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            return turnOnHotspot(callback)
//        } else {
//            if (showWritePermissionSettings()) {
//                val wifiEnabled = setWifiEnabled(getWifiApConfiguration(), true)
//                callback.onEnableHotspot(wifiEnabled)
//                return wifiEnabled
//            }
//        }
//        return null;
//    }

    fun setWifiEnable() {
        val getwifiManager = getwifiManager()
        getwifiManager.isWifiEnabled = true

    }

    fun setWifiDisable() {
        val getwifiManager = getwifiManager()
        getwifiManager.isWifiEnabled = false

    }

    fun setHotspotName(newName: String?, context: Context): Boolean {
        return try {
            val wifiManager = getwifiManager()
            val getConfigMethod = wifiManager.javaClass.getMethod("getWifiApConfiguration")
            val wifiConfig = getConfigMethod.invoke(wifiManager) as WifiConfiguration
            wifiConfig.SSID = newName
            wifiConfig.preSharedKey = "12345678"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                wifiConfig.setSecurityParams(WifiConfiguration.SECURITY_TYPE_OPEN)
            }
            val setConfigMethod = wifiManager.javaClass.getMethod(
                "setWifiApConfiguration",
                WifiConfiguration::class.java
            )
            setConfigMethod.invoke(wifiManager, wifiConfig)
            true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        }
    }

//    fun setWifiEnabled(wifiConfig: WifiConfiguration?, enabled: Boolean): Pigeon.Hotspot {
//        wifiConfig?.SSID = "share"
//        wifiConfig?.preSharedKey = "12345678"
//        var wifiManager: WifiManager = getwifiManager()
//        val isenabled = try {
//            if (enabled) { //disables wifi hotspot if it's already enabled
//                wifiManager.isWifiEnabled = false
//            }
//            val method: Method = wifiManager.javaClass
//                .getMethod(
//                    "setWifiApEnabled",
//                    WifiConfiguration::class.java,
//                    Boolean::class.javaPrimitiveType
//                )
//            method.invoke(wifiManager, wifiConfig, enabled) as Boolean
//        } catch (e: Exception) {
//            Log.e(this.javaClass.toString(), "", e)
//            false
//        }
//        val hotspot = Pigeon.Hotspot()
//        hotspot.enabled = isenabled
//        hotspot.name = wifiConfig?.SSID
//        hotspot.presharedkey = wifiConfig?.preSharedKey
//        return hotspot
//    }

    private val currentConfig: WifiConfiguration? = null
    private fun getWifiApConfiguration(): WifiConfiguration? {
        return try {
            val wifiManager = getwifiManager()
            val method: Method = wifiManager.javaClass.getMethod("getWifiApConfiguration")
            method.invoke(wifiManager) as WifiConfiguration
        } catch (e: java.lang.Exception) {
            Log.e(this.javaClass.toString(), "", e)
            null
        }
    }

    private var mReservation: LocalOnlyHotspotReservation? = null

    @RequiresApi(api = Build.VERSION_CODES.O)
//    private fun turnOnHotspot(param: Ihotspot): Pigeon.Hotspot {
//        var name: String? = null
//        var pass: String? = null
//        var isenabled: Boolean? = null
//        val manager =
//            getwifiManager()
//        if (manager.isWifiEnabled)
//            manager.isWifiEnabled = true
//        manager.startLocalOnlyHotspot(object : LocalOnlyHotspotCallback() {
//            override fun onStarted(reservation: LocalOnlyHotspotReservation?) {
//                super.onStarted(reservation)
//                mReservation = reservation;
//                pass = reservation!!.wifiConfiguration?.preSharedKey
//                name = reservation!!.wifiConfiguration?.SSID
//                isenabled = true;
//                val hotspot = Pigeon.Hotspot()
//                hotspot.enabled = isenabled
//                hotspot.name = name
//                hotspot.presharedkey = pass
//                param.onEnableHotspot(hospot = hotspot)
//            }
//
//            override fun onStopped() {
//                super.onStopped()
//                isenabled = false;
//                val hotspot = Pigeon.Hotspot()
//                hotspot.enabled = isenabled
//                hotspot.name = name
//                hotspot.presharedkey = pass
//                param.onEnableHotspot(hospot = hotspot)
//            }
//
//            override fun onFailed(reason: Int) {
//                super.onFailed(reason)
//                isenabled = false;
//                val hotspot = Pigeon.Hotspot()
//                hotspot.enabled = isenabled
//                hotspot.name = name
//                hotspot.presharedkey = pass
//                param.onEnableHotspot(hospot = hotspot)
//            }
//        }, Handler());
//
//        val hotspot = Pigeon.Hotspot()
//        hotspot.enabled = isenabled
//        hotspot.name = name
//        hotspot.presharedkey = pass
//        return hotspot
//    }

    fun turnOffHotspot() {
        if (mReservation != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mReservation?.close()
            }
        }
    }

    private fun showWritePermissionSettings(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && Build.VERSION.SDK_INT < Build.VERSION_CODES.O
        ) {
            if (!Settings.System.canWrite(contexts)) {
                Log.v("DANG", " " + !Settings.System.canWrite(contexts))
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + contexts.getPackageName())
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                contexts.startActivity(intent)
                return false
            }
        }
        return true //Permission already given
    }
//    @RequiresApi(Build.VERSION_CODES.M)
//    fun enableTetheringNew(callback: MyTetheringCallback): Boolean {
//        val outputDir: File = contexts.getCodeCacheDir()
//        try {
//            proxy = ProxyBuilder.forClass(classOnStartTetheringCallback())
//                .dexCache(outputDir).handler(object : InvocationHandler() {
//                    @Throws(Throwable::class)
//                    operator fun invoke(proxy: Any?, method: Method, args: Array<Any?>?): Any? {
//                        when (method.getName()) {
//                            "onTetheringStarted" -> callback.onTetheringStarted()
//                            "onTetheringFailed" -> callback.onTetheringFailed()
//                            else -> ProxyBuilder.callSuper(proxy, method, args)
//                        }
//                        return null
//                    }
//                }).build()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        val manager = contexts.getApplicationContext().getSystemService(
//            ConnectivityManager::class.java
//        ) as ConnectivityManager
//        var method: Method? = null
//        try {
//            method = manager.javaClass.getDeclaredMethod(
//                "startTethering",
//                Int::class.javaPrimitiveType,
//                Boolean::class.javaPrimitiveType, classOnStartTetheringCallback(),
//                Handler::class.java
//            )
//            if (method == null) {
//                Log.e("TAG", "startTetheringMethod is null")
//            } else {
//                method.invoke(manager, TETHERING_WIFI, false, proxy, null)
//            }
//            return true
//        } catch (e: NoSuchMethodException) {
//            e.printStackTrace()
//        } catch (e: IllegalAccessException) {
//            e.printStackTrace()
//        } catch (e: InvocationTargetException) {
//            e.printStackTrace()
//        }
//        return false
//    }
//
//    private fun classOnStartTetheringCallback(): Class<*>? {
//        try {
//            return Class.forName("android.net.ConnectivityManager\$OnStartTetheringCallback")
//        } catch (e: ClassNotFoundException) {
//            e.printStackTrace()
//        }
//        return null
//    }

}
