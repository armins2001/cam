package com.gapgpt.camera

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pedro.library.rtmp.RtmpCamera1
import com.pedro.library.view.OpenGlView
import com.pedro.encoder.input.video.CameraOpenException
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var rtmpCamera1: RtmpCamera1
    private lateinit var btnStart: Button
    private lateinit var etServer: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val openGlView = findViewById<OpenGlView>(R.id.surfaceView)
        btnStart = findViewById(R.id.btnStart)
        etServer = findViewById(R.id.etServer)
        
        etServer.setText("rtmp://YOUR_VPS_IP/live/cam1")

        rtmpCamera1 = RtmpCamera1(openGlView, object : com.pedro.rtmp.utils.ConnectCheckerRtmp {
            override fun onConnectionSuccessRtmp() {
                runOnUiThread { Toast.makeText(this@MainActivity, "Connected", Toast.LENGTH_SHORT).show() }
            }
            override fun onConnectionFailedRtmp(reason: String) {
                runOnUiThread { Toast.makeText(this@MainActivity, "Failed: $reason", Toast.LENGTH_SHORT).show() }
                rtmpCamera1.stopStream()
            }
            override fun onNewBitrateRtmp(bitrate: Long) {}
            override fun onDisconnectRtmp() {
                runOnUiThread { Toast.makeText(this@MainActivity, "Disconnected", Toast.LENGTH_SHORT).show() }
            }
            override fun onAuthErrorRtmp() {}
            override fun onAuthSuccessRtmp() {}
        })

        btnStart.setOnClickListener {
            if (!rtmpCamera1.isStreaming) {
                if (rtmpCamera1.prepareAudio() && rtmpCamera1.prepareVideo()) {
                    rtmpCamera1.startStream(etServer.text.toString())
                    btnStart.text = "STOP"
                }
            } else {
                rtmpCamera1.stopStream()
                btnStart.text = "START"
            }
        }

        // PTZ Listeners
        findViewById<Button>(R.id.btnUp).setOnClickListener { sendPTZ("up") }
        findViewById<Button>(R.id.btnDown).setOnClickListener { sendPTZ("down") }
        findViewById<Button>(R.id.btnLeft).setOnClickListener { sendPTZ("left") }
        findViewById<Button>(R.id.btnRight).setOnClickListener { sendPTZ("right") }
    }

    private fun sendPTZ(direction: String) {
        // Here you would send a command to your VPS API 
        // which then forwards to the camera via ONVIF
        Toast.makeText(this, "Moving $direction...", Toast.LENGTH_SHORT).show()
    }
}
