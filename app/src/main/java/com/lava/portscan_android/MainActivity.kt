package com.lava.portscan_android

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.shersoft.portscan.NoticeDialogFragment

class MainActivity : AppCompatActivity() {
    lateinit var textviewStatus: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textviewStatus = findViewById<TextView>(R.id.textviewStatus)

    }

    fun confirmFireMissiles() {
        val timeout_port = 1500
        val timeout_ip = 1500
        var type = 1;
        var ip = "";
        var port = "1433";
        val newFragment: NoticeDialogFragment =
            NoticeDialogFragment(
                timeout_ip,
                timeout_port,
                type,
                ip,
                port,
                object : NoticeDialogFragment.NoticeDialogListener {
                    override fun onDialogPositiveClick(dialog: DialogFragment?, ip: String) {
                        textviewStatus.text = ip;
                        dialog?.dismiss()

                    }

                    override fun onDialogNegativeClick(dialog: DialogFragment?) {
                        dialog?.dismiss()

                    }

                    override fun onIpClicked(ip: String, dialog: DialogFragment?) {
                        textviewStatus.text = ip;
                        dialog?.dismiss()
                    }

                })
        newFragment.isCancelable = false
        val fr = supportFragmentManager
        newFragment.show(fr, "missiles")


    }

    fun clikeme(view: android.view.View) {

        confirmFireMissiles()
    }
}