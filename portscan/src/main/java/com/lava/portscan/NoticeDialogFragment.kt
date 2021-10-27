package com.shersoft.portscan

import android.app.Dialog
import android.content.Context
import android.content.res.AssetManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.lava.portscan.R
import com.lava.portscan.adapter.CustomAdapter
import com.shersoft.android_ip.util.ConnectedDevice
import com.shersoft.android_ip.util.ConnectedDevice.IDeviceConnected
import com.shersoft.android_ip.util.MyIp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class NoticeDialogFragment(
    var timeout_ip: Int = 1000,
    var timeout_port: Int = 1000,
    var type: Int,
    var ip: String,
    var port: String,
    val listener: NoticeDialogListener
) :
    DialogFragment() {

    private var mRecyclerView: RecyclerView? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var mCurrentLayoutManagerType: LayoutManagerType? = null
    private var mAdapter: CustomAdapter? = null
    private var myIp: MyIp? = null
    private var connectedDevice: ConnectedDevice? = null
    private var fragmentActivity: FragmentActivity? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    lateinit var noticeDialogFragment: NoticeDialogFragment
    lateinit var coroutineScope: CoroutineScope
    override fun onAttach(context: Context) {
        super.onAttach(context)
        myIp = MyIp(context)
        connectedDevice = ConnectedDevice(context)
        noticeDialogFragment = this
        coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    }

    override fun onDetach() {
        super.onDetach()
        myIp = null
        connectedDevice = null
    }

    var mrootView: View? = null;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mrootView = inflater.inflate(R.layout.content_portscan, container, false)
        if (type == 2)
            mrootView?.findViewById<TextView>(R.id.ports)?.setText(port)
        mrootView?.findViewById<Button>(R.id.button_ok)?.setOnClickListener {
            if (mDataset.size >= 1)
                listener.onDialogPositiveClick(this, mDataset.get(0).toString())
            else
                listener.onDialogPositiveClick(this, "")
        }
        mrootView?.findViewById<Button>(R.id.btn_cancel)?.setOnClickListener {
            listener.onDialogNegativeClick(this)
        }
        mrootView?.findViewById<Button>(R.id.btn_refresh)?.setOnClickListener {
            mDataset.clear()
            coroutineScope.launch(Dispatchers.Main) {
                mAdapter =
                    CustomAdapter(mDataset, listener, noticeDialogFragment)
                mRecyclerView!!.adapter = mAdapter


            }
            if (type == 1)
                listIp
            else
                listPort
        }

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = mrootView?.findViewById<View>(R.id.recyclerview) as RecyclerView
        //        initDataset();

        mLayoutManager = LinearLayoutManager(activity)
        mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER
        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = savedInstanceState
                .getSerializable(KEY_LAYOUT_MANAGER) as LayoutManagerType?
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType)
        mAdapter = CustomAdapter(mDataset, listener, this)

        mRecyclerView!!.adapter = mAdapter

        if (type == 1) {

            mrootView?.findViewById<TextView>(R.id.head)?.setText("Scanning Device")
            listIp
        } else {
            mrootView?.findViewById<TextView>(R.id.head)?.setText("Scanning Server")
            listPort
        }




        fragmentActivity = requireActivity()
        return mrootView
    }

    private val s1: String?
        get() {
            val ipAddress_util = if (ip.length < 3) myIp?.getIPAddress_Util(true) else ip
            return ipAddress_util
        }

    val listIp: Unit
        get() {
            mDataset.clear()

            val ipAddress_util = if (ip.length < 3) myIp?.getIPAddress_Util(true) else ip
            //        s = mDataset.length;
            if (ipAddress_util != null && ipAddress_util != "Null") {
                val findViewById = mrootView?.findViewById<LottieAnimationView>(R.id.animation_view)
                findViewById
                    ?.setAnimation("wifii.json")
                findViewById?.playAnimation()
//                coroutineScope.launch(Dispatchers.IO) {
                ConnectedDevice(requireContext()).gethostData(
                    ipAddress_util,
                    object : IDeviceConnected {
                        override fun DeviceConnected(ip: String) {
                            mDataset.add(ip)
                            s++
                            coroutineScope.launch(Dispatchers.Main) {
                                mAdapter =
                                    CustomAdapter(mDataset, listener, noticeDialogFragment)
                                mRecyclerView!!.adapter = mAdapter


                            }
                        }
                    }
                )
//                }
            } else {

                val findViewById = mrootView?.findViewById<LottieAnimationView>(R.id.animation_view)
                findViewById
                    ?.setAnimation("nowifi.json")
                findViewById?.playAnimation()
            }
        }//public void run() {

    //        s = mDataset.length;
    val listPort: Unit
        get() {
            mDataset.clear()
            val ipAddress_util = if (ip.length < 3) myIp?.getIPAddress_Util(true) else ip
            //        s = mDataset.length;
            if (ipAddress_util != null && ipAddress_util != "Null") {
                connectedDevice?.gethostData(ipAddress_util, object : IDeviceConnected {
                    override fun DeviceConnected(ip: String) {
                        val stringStringMap = connectedDevice!!.portScan(
                            ip,
                            if (port.length < 1) 1433 else Integer.parseInt(port),
                            timeout_port
                        )
                        if ((stringStringMap["success"] == "true")) {
                            mDataset.add(ip)
                            s++
                            coroutineScope.launch(Dispatchers.Main) {
                                mAdapter = CustomAdapter(mDataset, listener, noticeDialogFragment)
                                mRecyclerView!!.adapter = mAdapter
                            } //public void run() {

                        }
                    }
                })
            } else {

                val findViewById = mrootView?.findViewById<LottieAnimationView>(R.id.animation_view)
                findViewById
                    ?.setAnimation("nowifi.json")
                findViewById?.playAnimation()
            }
        }
    var mDataset = ArrayList<String>()
    private fun initDataset() {
//        mDataset = arrayOfNulls(DATASET_COUNT)
        for (i in 0 until DATASET_COUNT) {
            mDataset[i] = "This is element #$i"
        }
    }

    fun setRecyclerViewLayoutManager(layoutManagerType: LayoutManagerType?) {
        var scrollPosition = 0

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView!!.layoutManager != null) {
            scrollPosition =
                (mRecyclerView!!.layoutManager as LinearLayoutManager?)?.findFirstCompletelyVisibleItemPosition()
                    ?: 10
        }
        when (layoutManagerType) {
            LayoutManagerType.GRID_LAYOUT_MANAGER -> {
                mLayoutManager = GridLayoutManager(activity, SPAN_COUNT)
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER
            }
            LayoutManagerType.LINEAR_LAYOUT_MANAGER -> {
                mLayoutManager = LinearLayoutManager(activity)
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER
            }
            else -> {
                mLayoutManager = LinearLayoutManager(activity)
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER
            }
        }
        mRecyclerView!!.layoutManager = mLayoutManager
        mRecyclerView!!.scrollToPosition(scrollPosition)
    }

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment?, ip: String)
        fun onDialogNegativeClick(dialog: DialogFragment?)
        fun onIpClicked(ip: String, dialog: DialogFragment?)
    }

    companion object {
        var s = 0
        private val DATASET_COUNT = 60
        private val KEY_LAYOUT_MANAGER = "layoutManager"
        private val SPAN_COUNT = 2
    }

//    fun loadJSONFromAsset(context: Context): String? {
//        var json: String? = null
//        json = try {
//            val `is`: InputStream = context.assets.open("file_name.json")
//            val size: Int = `is`.available()
//            val buffer = ByteArray(size)
//            `is`.read(buffer)
//            `is`.close()
//            String(buffer, "UTF-8")
//        } catch (ex: IOException) {
//            ex.printStackTrace()
//            return null
//        }
//        return json
//    }

}

enum class LayoutManagerType {
    GRID_LAYOUT_MANAGER, LINEAR_LAYOUT_MANAGER
}

fun AssetManager.readAssetsFile(fileName: String): String =
    open(fileName).bufferedReader().use { it.readText() }