package com.ljl.englishfollowup.util

import android.content.Context
import android.media.AudioFormat
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class SentenceRecorder(private val context: Context, private val articleTitle: String) {

    private val TAG = "MediaRecorderHelper"
    private val mediaRecorder: MediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    } else {
        MediaRecorder()
    }


    private val sampleRateInHz = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_STEREO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private var outputFilePath: String? = null
    private val testOutputFilePath by lazy {
        context.filesDir.absolutePath + "/test.aac"
    }
    private val outputDir by lazy { getRecordDir(articleTitle) }

    /**
     * Sets the path of the output file to be produced. Call this after
     * setOutputFormat() but before prepare().
     *
     * @param path The pathname to use.
     * @throws IllegalStateException if it is called before
     * setOutputFormat() or after prepare()
     */
    fun setOutputPath(path: String) {
        outputFilePath = path
        mediaRecorder.setOutputFile(path)
    }

    private var curId = -1
    private var isRecording = false

    private fun initMediaRecorder() {
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
        mediaRecorder.setAudioSamplingRate(sampleRateInHz)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder.setAudioEncodingBitRate(96000)
    }

    fun startRecord(sentenceId: Int) {
        if (isRecording) {
            mediaRecorder.stop()
            mediaRecorder.reset()
        }
        initMediaRecorder()

        val outputFile = File(outputDir, generateOutputFileName(articleTitle, sentenceId))
        if (outputFile.exists()) {
            outputFile.delete()
        }
        mediaRecorder.setOutputFile(outputFile.absolutePath)

        try {
            mediaRecorder.prepare()
            mediaRecorder.start()
            curId = sentenceId
            isRecording = true
        } catch (e: IllegalStateException) {
            Log.e(TAG, e.message ?: "")
        } catch (e: IOException) {
            Log.e(TAG, e.message ?: "")
        }

    }

    fun stopRecord(sentenceId: Int) {
        if (!isRecording) {
            return
        }

        isRecording = false
        if (curId != sentenceId) {
            Log.e(TAG, "curId($curId) != sentenceId($sentenceId)")
        }
        mediaRecorder.stop()
        mediaRecorder.reset()
    }

    // region 播放录音
    private val mediaPlayer = MediaPlayer()

    fun playRecord(sentenceId: Int) {
        val file = File(outputDir, generateOutputFileName(articleTitle, sentenceId))
        if (!file.exists()) {
            Toast.makeText(context, "file(${file.absolutePath}) not exists", Toast.LENGTH_SHORT).show()
            return
        }
        val fis = FileInputStream(file)
        mediaPlayer.setDataSource(fis.fd)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        mediaPlayer.setOnCompletionListener {
            listener.onCompletion(it)
            it.reset()
        }
    }

    fun stopPlay() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
        }
    }

    fun releaseMediaPlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
        }
        mediaPlayer.release()
        Log.e(TAG, "mediaPlayer.release()")
    }
    // endregion

}

fun generateOutputFileName(title: String, sentenceId: Int, suffix: String = ".aac"): String {
    return "${title}_${sentenceId}${suffix}"
}

fun getIdFromFileName(fileName: String): Int {
    val startIndex = fileName.indexOf("_")
    val endIndex = fileName.indexOf(".")
    return fileName.substring(startIndex, endIndex).toInt()
}