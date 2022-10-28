package com.shersoft.android_ip.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import android.util.Log
import android.widget.Toast



class W2peer(contexts: Context) : ConnectedDevice(contexts) {
//    interface Ihotspot {
//        fun onEnableHotspot(hospot: Pigeon.Hotspot);
//    }

    private val intentFilter = IntentFilter()
    open lateinit var mManager: WifiP2pManager
    lateinit var channel: WifiP2pManager.Channel

    init {


        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        mManager = contexts.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = mManager.initialize(contexts, Looper.getMainLooper(), null)
        val w2pBroadCast = W2pBroadCast()
        contexts.registerReceiver(w2pBroadCast, intentFilter)
    }

    private val peers = mutableListOf<WifiP2pDevice>()
    lateinit var device: WifiP2pDevice
    val config = WifiP2pConfig()
//    val peerListListener = WifiP2pManager.PeerListListener { peerList ->
//        val refreshedPeers = peerList.deviceList
//        print(peerList)
//        if (refreshedPeers != peers) {
//            if (refreshedPeers.size > 0) {
//                device = peerList.deviceList.first()
//                config.deviceAddress = device.deviceAddress
//                if (device != null)
//                    channel?.also { channel ->
//                        mManager?.connect(channel, config, object : WifiP2pManager.ActionListener {
//
//                            override fun onSuccess() {
//                                //success logic
//                                print("connected")
//                            }
//
//                            override fun onFailure(reason: Int) {
//                                print("fail" +reason)
//                            }
//                        }
//                        )
//                    }
//            }
//            peers.clear()
//            peers.addAll(refreshedPeers)
//
//            // If an AdapterView is backed by this data, notify it
//            // of the change. For instance, if you have a ListView of
//            // available peers, trigger an update.
//
//
//            // Perform any other updates needed based on the new list of
//            // peers connected to the Wi-Fi P2P network.
//        }
//
//        if (peers.isEmpty()) {
//            Log.d("TAG", "No devices found")
//            return@PeerListListener
//        }
//    }
//
//    fun discoverPeers() {
//        mManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
//
//            override fun onSuccess() {
//                print("============p2p>success")
//                // Code for when the discovery initiation is successful goes here.
//                // No services have actually been discovered yet, so this method
//                // can often be left blank. Code for peer discovery goes in the
//                // onReceive method, detailed below.
//            }
//
//            override fun onFailure(reasonCode: Int) {
//                print("failure")
//                // Code for when the discovery initiation fails goes here.
//                // Alert the user that something went wrong.
//            }
//        })
//    }

//    fun connect() {
//        // Picking the first device found on the network.
//        val device = peers[0]
//
//        val config = WifiP2pConfig().apply {
//            deviceAddress = device.deviceAddress
//            wps.setup = WpsInfo.PBC
//        }
//
//        mManager.connect(channel, config, object : WifiP2pManager.ActionListener {
//
//            override fun onSuccess() {
//                // WiFiDirectBroadcastReceiver notifies us. Ignore for now.
//            }
//
//            override fun onFailure(reason: Int) {
//                Toast.makeText(
//                    contexts,
//                    "Connect failed. Retry.",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        })
//    }

    inner class W2pBroadCast : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                    // Determine if Wifi P2P mode is enabled or not, alert
                    // the Activity.
                    val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)

                    when (state) {
                        WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                            // Wifi P2P is enabled
                        }
                        else -> {

                        }
                    }
                    print(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
                }
//                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
//                    mManager?.requestPeers(channel, peerListListener)
//                    mManager.requestGroupInfo(channel, DeviceListListener);
//                    Log.d("TAG", "P2P peers changed")
//                    // The peer list has changed! We should probably do something about
//                    // that.
//                    print(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
//                }
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    print(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
                    // Connection state changed! We should probably do something about
                    // that.

                }
                WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                    print(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
                    intent.getParcelableExtra<WifiP2pDevice>(
                        WifiP2pManager.EXTRA_WIFI_P2P_DEVICE
                    )

                }
            }
        }
    }

    object DeviceListListener :
        WifiP2pManager.GroupInfoListener {


        override fun onGroupInfoAvailable(group: WifiP2pGroup?) {
            print(group)
        }

    }

}
