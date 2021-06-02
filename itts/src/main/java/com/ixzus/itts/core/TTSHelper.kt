package com.ixzus.itts.core

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.*

class TTSHelper {
    companion object {
        @Volatile
        private var instance: TTSHelper? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: TTSHelper()
        }
    }


    private lateinit var context: Context
    private lateinit var textToSpeech: TextToSpeech
    private val speakQueue by lazy { LinkedList<String>() }

    private var initState = TextToSpeech.ERROR

    private var currentSpeak: String? = null

    private var utteranceProgressListener = object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
        }

        override fun onDone(utteranceId: String?) {
            currentSpeak = null
            if (speakQueue.count() > 0) {
                textToSpeech?.run {
                    currentSpeak = speakQueue.removeFirst()
                    textToSpeech.speak(currentSpeak, TextToSpeech.QUEUE_ADD, null, "")
                }
            }
        }

        override fun onError(utteranceId: String?) {
        }
    }

    fun init(context: Context) {
        this.context = context
        textToSpeech = TextToSpeech(context) { status ->
            initState = status
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setPitch(1.0f)
                textToSpeech.setSpeechRate(0.5f)
                if (speakQueue.size > 0)
                    textToSpeech.speak(speakQueue.removeFirst(), TextToSpeech.QUEUE_ADD, null, "")
                textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener)
            } else {
            }
        }
    }

    fun speak(text: String) {
        if (context == null) {
            return
        }
        if (speakQueue.contains(text) || currentSpeak == text) return
        speakQueue.add(text)
        if (initState == TextToSpeech.SUCCESS) {
            textToSpeech?.run {
                currentSpeak = speakQueue.removeFirst()
                speak(currentSpeak, TextToSpeech.QUEUE_ADD, null, "")
            }
        }
    }

    fun isSpeaking() = if (textToSpeech == null) false else textToSpeech.isSpeaking

    fun stop() {
        textToSpeech?.run {
            if (isSpeaking)
                stop()
        }
    }

    fun release() {
        textToSpeech?.run {
            stop()
            shutdown()
        }
        instance = null
    }

}