package com.ixzus.tts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ixzus.tts.core.TTSHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TTSHelper.getInstance().init(this)
        TTSHelper.getInstance().speak("开机")
    }
}