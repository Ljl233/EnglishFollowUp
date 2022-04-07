package com.ljl.englishfollowup.detail

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.ljl.englishfollowup.R
import com.ljl.englishfollowup.model.LocalRepo
import com.ljl.englishfollowup.util.LyricUtil
import com.ljl.englishfollowup.util.SentenceRecorder
import java.io.File
import java.io.FileDescriptor

class SentencePlayAndRecordHelper(private val context: Context, private val repo: LocalRepo) {

    companion object {
        // todo: 模仿线程池，设置回收机制
        private val sentenceHelperPool by lazy { mutableMapOf<Int, SentencePlayAndRecordHelper>() }

        fun getInstance(context: Context, repo: LocalRepo, articleId: Int, articleTitle: String): SentencePlayAndRecordHelper {
            var instance = sentenceHelperPool[articleId]
            if (instance == null) {
                instance = SentencePlayAndRecordHelper(context, repo).also {
                    it.articleId = articleId
                    it.articleTitle = articleTitle
                }
                sentenceHelperPool[articleId] = instance
            }
            return instance
        }

        fun destroyInstance(articleId: Int) {
            sentenceHelperPool.remove(articleId)
        }
    }


    private val tag = "SentenceHelper"

    private var mediaPlayer = MediaPlayer()
    var articleId: Int = -1
    var articleTitle: String = ""

    private val lyricTimes: List<LyricUtil.LyricTime>?
        get() {
            return repo.getLyricTimeByArticleId(articleId)
        }

    private var isPlayerPrepared = false
    val isPlaying: Boolean
        get() {
            return mediaPlayer.isPlaying
        }

    // region MediaPlayer
    val currentPosition get() = mediaPlayer.currentPosition

    private fun checkPlayerPrepare() {
        if (!isPlayerPrepared) {
            val fd = getMusicFD()
            if (fd == null) {
                Log.e(tag, "file is null")
                mediaPlayer = MediaPlayer.create(context, R.raw.youth)
            } else {
                mediaPlayer.setDataSource(fd)
                mediaPlayer.prepare()
            }
            isPlayerPrepared = true
        }
    }

    fun play() {
        checkPlayerPrepare()
        mediaPlayer.start()
    }

    fun playSentence(position: Int) {
        checkPlayerPrepare()
        val sentenceStart = lyricTimes?.get(position) ?: return
        mediaPlayer.seekTo(sentenceStart.toMillSec())
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    private fun getMusicFD(): FileDescriptor? {
        val path = repo.getMusicPathById(articleId)
        if (path == null) {
            return null
        } else {
            return File(path).inputStream().fd
        }
    }
    // endregion

    //region Recorder

    private val sentenceRecorder by lazy { SentenceRecorder(context, articleTitle) }
    fun startRecord(sentencePosition: Int) {
        sentenceRecorder.startRecord(sentencePosition)
    }

    fun stopRecord(sentencePosition: Int) {
        sentenceRecorder.stopRecord(sentencePosition)
    }

    fun playRecord(sentencePosition: Int) {
        sentenceRecorder.playRecord(sentencePosition)
    }

    fun stopPlay(sentencePosition: Int) {
        sentenceRecorder.stopPlay()
    }

    fun setOnPlayFinishCallback(listener: MediaPlayer.OnCompletionListener) {
        sentenceRecorder.setOnCompletionListener(listener)
    }

    //endregion


}