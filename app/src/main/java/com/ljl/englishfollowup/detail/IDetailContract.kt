package com.ljl.englishfollowup.detail

import android.content.Context
import android.media.MediaPlayer
import com.ljl.englishfollowup.model.SentenceDetail

interface IDetailContract {
    interface IDetailView {
        fun updatePlayIconState(isPlaying: Boolean)
        fun notifySentenceColor(position: Int, oldPosition: Int)
        fun updateRecordPlayBtState(position: Int, isPlaying: Boolean)
        fun updateRecordBtStateByPosition(position: Int, record: Boolean)
        fun smoothListIfNeed(curPlaySentence: Int, listSize: Int)
        fun finishPlayRecordSentence()
    }

    interface IDetailPresenter {

        fun initSentenceHelper(context: Context, articleId: Int, articleTitle: String)

        fun isMusicPlaying(): Boolean
        fun onPlayBtClick()
        fun fetchData(): List<SentenceDetail>

        fun setItemClickListenerForAdapter(adapter: SentenceAdapter)
        fun playSentence(position: Int)
        fun setOnPlayFinishCallback(listener: MediaPlayer.OnCompletionListener)
    }
}