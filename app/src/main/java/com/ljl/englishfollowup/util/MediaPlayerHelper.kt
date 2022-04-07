package com.ljl.englishfollowup.util

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import java.io.File
import java.io.FileInputStream

class MediaPlayerHelper(private val context: Context?) {
    private val mediaPlayer = MediaPlayer()
    var isPlaying = false
        get() = mediaPlayer.isPlaying

    fun play(path: String) {
        if (mediaPlayer.isPlaying) {
            stop()
        }
        val file = File(path)
        if (!file.exists()) {
            Toast.makeText(context, "file(${file.absolutePath}) not exists", Toast.LENGTH_SHORT).show()
            return
        }
        val fis = FileInputStream(file)
        mediaPlayer.setDataSource(fis.fd)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    fun stop() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.reset()
    }

    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        mediaPlayer.setOnCompletionListener(listener)
    }
}