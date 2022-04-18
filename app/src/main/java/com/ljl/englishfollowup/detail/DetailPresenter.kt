package com.ljl.englishfollowup.detail

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.ljl.englishfollowup.base.OnSentenceClickListener
import com.ljl.englishfollowup.model.LocalRepo
import com.ljl.englishfollowup.model.SentenceDetail
import com.ljl.englishfollowup.util.LyricUtil

class  DetailPresenter(val view: IDetailContract.IDetailView) : IDetailContract.IDetailPresenter {

    private var sentenceList = mutableListOf<SentenceDetail>()
    private var firstPlaying: Boolean = true
    private val repo = LocalRepo()

    private lateinit var sentenceHelper: SentencePlayAndRecordHelper

    private var curRecordPos = -1
    private var curPlayRecordPos = -1


    override fun initSentenceHelper(context: Context, articleId: Int, articleTitle: String) {
        sentenceHelper = SentencePlayAndRecordHelper.getInstance(context, repo, articleId, articleTitle)
    }

    override fun isMusicPlaying(): Boolean {
        if (!this::sentenceHelper.isInitialized) {
            return false
        }
        return sentenceHelper.isPlaying
    }

    override fun onPlayBtClick() {
        if (firstPlaying) {
            startMonitorSentence()
            notifySentenceColor(0)
            firstPlaying = false
        }
        playOrPauseMusic()
    }

    override fun fetchData(): List<SentenceDetail> {
        repo.getLyricByArticleId()?.let {
            sentenceList = LyricUtil.linesToSentences(it).toMutableList()
        }
        return sentenceList
    }

    override fun setItemClickListenerForAdapter(adapter: SentenceAdapter) {

        adapter.itemClickListener = object : OnSentenceClickListener {
            override fun onItemClick(position: Int) {
                playSentence(position)
            }

            override fun onRecordClick(position: Int, record: Boolean) {
                if (record) {
                    view.updateRecordBtStateByPosition(position, record)

                    sentenceHelper.startRecord(position)
                    curRecordPos = position
                } else {
                    sentenceHelper.stopRecord(position)
                    curPlayRecordPos = -1
                }
            }

            override fun onPlayClick(position: Int, play: Boolean) {
                if (play) {
                    view.updateRecordPlayBtState(position, play)
                    sentenceHelper.playRecord(position)
                    curPlayRecordPos = position
                    sentenceHelper.setOnPlayFinishCallback {
                        view.updateRecordPlayBtState(position = position, isPlaying = false)
                        curPlayRecordPos = -1
                        view.finishPlayRecordSentence()
                    }
                } else {
                    view.updateRecordPlayBtState(position, play)
                    sentenceHelper.stopPlay(position)
                    curPlayRecordPos = -1
                }
            }
        }
    }

    override fun playSentence(position: Int) {
        if (firstPlaying) {
            startMonitorSentence()
            firstPlaying = false
        }
        notifySentenceColor(position)
        sentenceHelper.playSentence(position)
        view.updatePlayIconState(isMusicPlaying())
    }

    override fun setOnPlayFinishCallback(listener: MediaPlayer.OnCompletionListener) {
        sentenceHelper.setOnPlayFinishCallback(listener)
    }

    private fun notifySentenceColor(position: Int) {
        lastPlaySentence = curPlaySentence
        curPlaySentence = position
        view.notifySentenceColor(curPlaySentence, lastPlaySentence)

        view.smoothListIfNeed(curPlaySentence, sentenceList.size)
    }

    private fun playOrPauseMusic() {
        if (!isMusicPlaying()) {
            sentenceHelper.play()
        } else {
            sentenceHelper.pause()
        }
        view.updatePlayIconState(isMusicPlaying())
    }

    private val handler = Handler(Looper.getMainLooper()) {
        if (isMusicPlaying()) {
            if (it.what == curPlaySentence + 1) {
                notifySentenceColor(it.what)
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    @Volatile
    private var curPlaySentence = 0
    private var lastPlaySentence = -1
    private fun startMonitorSentence() {
        Thread {
            var flag = true
            while (flag) {
                val nextSentence = curPlaySentence + 1
                if (nextSentence < sentenceList.size - 1) {
                    val nextMill = sentenceList[nextSentence].startTime.toMillSec()
                    val curMill = sentenceHelper.currentPosition
                    val sleepTime = nextMill - curMill
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime.toLong())
                        val msg = Message()
                        msg.what = nextSentence
                        handler.sendMessage(msg)
                    }
                } else {
                    break
                }
            }
        }.start()
    }
}