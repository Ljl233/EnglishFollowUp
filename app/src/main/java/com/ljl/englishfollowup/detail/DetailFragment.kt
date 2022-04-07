package com.ljl.englishfollowup.detail

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ljl.englishfollowup.R
import com.ljl.englishfollowup.base.BaseListFragment
import com.ljl.englishfollowup.base.OnSentenceClickListener
import com.ljl.englishfollowup.home.ArticleListFragment
import com.ljl.englishfollowup.model.LocalRepo
import com.ljl.englishfollowup.model.SentenceDetail
import com.ljl.englishfollowup.synthesis.BUNDLE_SYNTHESIS_DIR
import com.ljl.englishfollowup.synthesis.BUNDLE_TITLE
import com.ljl.englishfollowup.util.LyricUtil
import com.ljl.englishfollowup.util.getRecordDir

class DetailFragment : BaseListFragment() {

    private lateinit var toolbar: Toolbar
    private lateinit var playBt: ImageView
    private lateinit var shareBt: ImageView

    private var sentenceList = mutableListOf<SentenceDetail>()
    private lateinit var listAdapter: SentenceAdapter

    private val repo = LocalRepo()
    private lateinit var sentenceHelper: SentencePlayAndRecordHelper

    private val articleId by lazy { arguments?.getInt(ArticleListFragment.BUNDLE_ARTICLE_ID) ?: -1 }
    private val articleTitle by lazy { arguments?.getString(ArticleListFragment.BUNDLE_ARTICLE_TITLE) ?: "" }

    private val isMusicPlaying: Boolean
        get() {
            if (!this::sentenceHelper.isInitialized) {
                return false
            }
            return sentenceHelper.isPlaying
        }
    private var firstPlaying: Boolean = true

    override fun initView() {
        super.initView()
        toolbar = rootView.findViewById(R.id.sentence_fragment_toolbar)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        playBt = rootView.findViewById(R.id.action_play)
        playBt.setImageResource(
            if (isMusicPlaying) R.drawable.ic_baseline_pause_24 else R.drawable.ic_baseline_play_arrow_24
        )
        playBt.setOnClickListener {
            if (firstPlaying) {
                startMonitorSentence()
                notifySentenceColor(0)
                firstPlaying = false
            }
            playOrPauseMusic()
        }

        shareBt = rootView.findViewById(R.id.sentence_fragment_share)
        shareBt.setOnClickListener {
            showShareDialog()
        }

        sentenceHelper.setOnPlayFinishCallback {
            playBt.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        }
    }

    private var curRecordPos = -1
    private var curPlayRecordPos = -1

    override fun initList() {
        listAdapter = SentenceAdapter(articleTitle)

        listView.layoutManager = LinearLayoutManager(context)
        listView.adapter = listAdapter
        listAdapter.itemClickListener = object : OnSentenceClickListener {
            override fun onItemClick(position: Int) {
                playSentence(position)
            }

            override fun onRecordClick(position: Int, record: Boolean) {
                if (record) {
                    (listView.findViewHolderForAdapterPosition(position) as SentenceAdapter.SentenceViewHolder)
                        .recordBt.setImageResource(R.drawable.ic_baseline_mic_none_24)
                    sentenceHelper.startRecord(position)
                    curRecordPos = position
                } else {
                    sentenceHelper.stopRecord(position)
                    curPlayRecordPos = -1
                }
            }

            override fun onPlayClick(position: Int, play: Boolean) {
                if (play) {
                    (listView.findViewHolderForAdapterPosition(position) as SentenceAdapter.SentenceViewHolder)
                        .playIcon.setImageResource(R.drawable.ic_baseline_stop_24)
                    sentenceHelper.playRecord(position)
                    curPlayRecordPos = position
                    sentenceHelper.setOnPlayFinishCallback {
                        (listView.findViewHolderForAdapterPosition(position) as SentenceAdapter.SentenceViewHolder)
                            .playIcon.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                        curPlayRecordPos = -1
                        listAdapter.finishPlayRecordSentence()
                    }
                } else {
                    (listView.findViewHolderForAdapterPosition(position) as SentenceAdapter.SentenceViewHolder)
                        .playIcon.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    sentenceHelper.stopPlay(position)
                    curPlayRecordPos = -1
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshData()
        listAdapter.updateData(sentenceList)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sentenceHelper = SentencePlayAndRecordHelper.getInstance(context, repo, articleId, articleTitle)
    }

    // region 播放进度管理
    private val handler = Handler(Looper.getMainLooper()) {
        if (isMusicPlaying) {
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


    // endregion

    // region 原文播放
    @Volatile
    private var curPlaySentence = 0

    private fun playOrPauseMusic() {
        if (!isMusicPlaying) {
            sentenceHelper.play()
        } else {
            sentenceHelper.pause()
        }
        updatePlayIconState()
    }

    private fun updatePlayIconState() {
        if (isMusicPlaying) {
            playBt.setImageResource(R.drawable.ic_baseline_pause_24)
        } else {
            playBt.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        }
    }

    private fun playSentence(position: Int) {
        if (firstPlaying) {
            startMonitorSentence()
            firstPlaying = false
        }
        notifySentenceColor(position)
        sentenceHelper.playSentence(position)
        updatePlayIconState()
    }

    private fun notifySentenceColor(position: Int) {
        curPlaySentence = position
        listAdapter.playSentence(position)

        val lastVisiblePosition = (listView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        if (curPlaySentence >= lastVisiblePosition && lastVisiblePosition < sentenceList.size - 1) {
            listView.smoothScrollToPosition(lastVisiblePosition + 1)
        }
    }
    // endregion

    private fun refreshData() {
        repo.getLyricByArticleId()?.let {
            sentenceList = LyricUtil.linesToSentences(it).toMutableList()
        }
    }

    private fun showShareDialog() {
        val bundle = Bundle()
        bundle.putString(BUNDLE_SYNTHESIS_DIR, getRecordDir(articleTitle))
        bundle.putString(BUNDLE_TITLE, articleTitle)
        val shareDialog = AlertDialog.Builder(context)
            .setTitle("合成跟读并分享")
            .setNegativeButton("取消") { dialog, _ -> dialog?.dismiss() }
            .setPositiveButton("确定") { _, _ ->
                findNavController().navigate(R.id.synthesisFragment, bundle)
            }
        shareDialog.show()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_sentence
    }
}