package com.shersoft.android_ip.util

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Context.WIFI_SERVICE
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.StrictMode
import android.text.TextUtils
import android.text.format.Formatter
import android.util.Log
import androidx.activity.ComponentActivity
import com.lava.portscan.util.Utilss


import java.io.IOException
import java.math.BigInteger
import java.net.*
import java.util.*


class MyIp(var contexts: Context) {
    interface VolleyListner {

        fun Onresponse(data: String)
    }


    init {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

    }

    fun getWifiIp(context: Context): String? {
        val systemService = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return systemService.let {
            when {
                it == null -> "No wifi available"
                !it.isWifiEnabled -> "Wifi is disabled"
                it.connectionInfo == null -> "Wifi not connected"
                else -> {
                    val ip = it.connectionInfo.ipAddress
                    ((ip and 0xFF).toString() + "." + (ip shr 8 and 0xFF) + "." + (ip shr 16 and 0xFF) + "." + (ip shr 24 and 0xFF))
                }
            }
        }
    }

    fun getNetworkIp4LoopbackIps(): Map<String, String> = try {
        NetworkInterface.getNetworkInterfaces()
            .asSequence()
            .associate { it.displayName to it.ip4LoopbackIps() }
            .filterValues { it.isNotEmpty() }
    } catch (ex: Exception) {
        emptyMap()
    }

    private fun NetworkInterface.ip4LoopbackIps() =
        inetAddresses.asSequence()
            .filter { !it.isLoopbackAddress && it is Inet4Address }
            .map { it.hostAddress }
            .filter { it.isNotEmpty() }
            .joinToString()

    fun getdeviceIpAddress_Wifi(): String? {
        val context: Context = contexts.applicationContext
        val wifiManager = context.getSystemService(ComponentActivity.WIFI_SERVICE) as WifiManager
        val ip = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        if (ip.contains("0.0.0.0"))
            return "null"

        return ip;
    }

    fun getdeviceIpAddress_Wifi_2(): String? {
        try {
            val host = InetAddress.getByName("webAddress")
//            val host = InetAddress.getByName("nameOfDevice or webAddress")
            println(host.hostAddress)
            return host.hostAddress;
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            return "0.0.0.0" + e.message
        }

    }

    fun getdeviceIpAddress(): String? {
        try {
            val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf: NetworkInterface = en.nextElement()
                val enumIpAddr: Enumeration<InetAddress> = intf.getInetAddresses()
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress: InetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress() && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return null
    }


    fun getIpAddress(context: Context): String? {
        val wifiManager = context.applicationContext
            .getSystemService(WIFI_SERVICE) as WifiManager
        var ipAddress = intToInetAddress(wifiManager.dhcpInfo.ipAddress).toString()
        ipAddress = ipAddress.substring(1)
        return ipAddress
    }

    fun intToInetAddress(hostAddress: Int): InetAddress {
        val addressBytes = byteArrayOf(
            (0xff and hostAddress).toByte(),
            (0xff and (hostAddress shr 8)).toByte(),
            (0xff and (hostAddress shr 16)).toByte(),
            (0xff and (hostAddress shr 24)).toByte()
        )
        return try {
            InetAddress.getByAddress(addressBytes)
        } catch (e: UnknownHostException) {
            throw AssertionError()
        }
    }

    fun getIPAddress(useIPv4: Boolean): String {
        try {
            var interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                var addrs = Collections.list(intf.getInetAddresses());
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress()) {
                        var sAddr = addr.getHostAddress();
                        var isIPv4: Boolean
                        isIPv4 = sAddr.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                var delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                if (delim < 0) {
                                    return sAddr.toUpperCase()
                                } else {
                                    return sAddr.substring(0, delim).toUpperCase()
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
        }
        return ""
    }


    fun getIpAddress_2(): String {
        var ip = ""
        try {
            val wm = contexts.getApplicationContext().getSystemService(WIFI_SERVICE) as WifiManager
            ip = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
        } catch (e: java.lang.Exception) {

        }

        if (ip.isEmpty()) {
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val networkInterface = en.nextElement()
                    val enumIpAddr = networkInterface.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                            val host = inetAddress.getHostAddress()
                            if (host.isNotEmpty()) {
                                ip = host
                                break;
                            }
                        }
                    }

                }
            } catch (e: java.lang.Exception) {

            }
        }

        if (ip.isEmpty())
            ip = "127.0.0.1"
        return ip
    }


    fun getMacAddress_Util(inter: String = "wlan0"): String = Utilss.getMACAddress(inter);
    fun getIPAddress_Util(isTrueip4: Boolean = true): String = Utilss.getIPAddress(isTrueip4);

    fun getIPAddress_Wif_3(isTrueip4: Boolean = true): String {
        val wm = contexts.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager?
        val ipAddress: String = BigInteger.valueOf(wm!!.dhcpInfo.netmask.toLong()).toString()
        return ipAddress
    }
    //


    fun getLocalIpAddress(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (ex: java.lang.Exception) {
            Log.e("IP Address", ex.toString())
        }
        return null
    }

    fun getDeviceIpAddress(): String {
        var actualConnectedToNetwork: String? = null
        val connManager = contexts.getApplicationContext()
            .getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (connManager != null) {
            val mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            if (mWifi!!.isConnected) {
                actualConnectedToNetwork = getWifiIp()
            }
        }
        if (TextUtils.isEmpty(actualConnectedToNetwork)) {
            actualConnectedToNetwork = getNetworkInterfaceIpAddress()
        }
        if (TextUtils.isEmpty(actualConnectedToNetwork)) {
            actualConnectedToNetwork = "127.0.0.1"
        }
        return actualConnectedToNetwork!!
    }


    fun getWifiIp(): String? {
        val mWifiManager = contexts.getApplicationContext().getSystemService(
            WIFI_SERVICE
        ) as WifiManager
        if (mWifiManager.isWifiEnabled) {
            val ip = mWifiManager.connectionInfo.ipAddress
            return ((ip and 0xFF).toString() + "." + (ip shr 8 and 0xFF) + "." + (ip shr 16 and 0xFF) + "."
                    + (ip shr 24 and 0xFF))
        }
        return null
    }


    fun getNetworkInterfaceIpAddress(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val networkInterface = en.nextElement()
                val enumIpAddr = networkInterface.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        val host = inetAddress.getHostAddress()
                        if (!TextUtils.isEmpty(host)) {
                            return host
                        }
                    }
                }
            }
        } catch (ex: java.lang.Exception) {
            Log.e("IP Address", "getLocalIpAddress", ex)
        }
        return null
    }

    fun getLocalIpAddress_2(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return null
    }

    @Throws(IOException::class)
    fun getLocalAddress(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        //return inetAddress.getHostAddress().toString();
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (ex: SocketException) {
            Log.e("SALMAN", ex.toString())
        }
        return null
    }


    fun getLocalIpAddress_4(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
//                        val ip = Formatter.formatIpAddress(inetAddress.hostAddress())
                        Log.i("TAG", "***** IP=")
                        return inetAddress.canonicalHostName + inetAddress.address;
                    }
                }
            }
        } catch (ex: SocketException) {
            Log.e("TAG", ex.toString())
        }
        return null
    }

    fun getIpInterface(key: String): String {
        var keys = key;
        if (key.contains("bt_pan"))
            keys =
                "bt-pan"
        val networkIp4LoopbackIps = getNetworkIp4LoopbackIps()
        try {
            if (key.contains("rmnet_data")) {
                networkIp4LoopbackIps.forEach {
                    if (it.key.contains("rmnet_data")) return it.value
                }
            } else if (key.contains("wlan1")) {
                networkIp4LoopbackIps.forEach {
                    if (it.key.contains("wlan1")) return it.value
                    if (getdeviceIpAddress_Wifi()?.contains("null") == true)
                        if (it.key.contains("wlan0")) return it.value
                }
            }
            return networkIp4LoopbackIps.getValue(keys)
        } catch (e: Exception) {
            return "Null";
        }
    }


}

enum class network_interfac {
    bt_pan, wlan1, wlan0, wlan2, rmnet_data1, rmnet_data2, rmnet_data0, rndis0, rndis1, rndis2
}
