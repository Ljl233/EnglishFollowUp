package com.ljl.englishfollowup.detail

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ljl.englishfollowup.R
import com.ljl.englishfollowup.base.BaseListFragment
import com.ljl.englishfollowup.home.ArticleListFragment
import com.ljl.englishfollowup.synthesis.BUNDLE_SYNTHESIS_DIR
import com.ljl.englishfollowup.synthesis.BUNDLE_TITLE
import com.ljl.englishfollowup.util.getRecordDir

class DetailFragment : BaseListFragment(), IDetailContract.IDetailView {

    private lateinit var toolbar: Toolbar
    private lateinit var playBt: ImageView
    private lateinit var shareBt: ImageView

    private val articleId by lazy { arguments?.getInt(ArticleListFragment.BUNDLE_ARTICLE_ID) ?: -1 }
    private val articleTitle by lazy { arguments?.getString(ArticleListFragment.BUNDLE_ARTICLE_TITLE) ?: "" }

    private lateinit var listAdapter: SentenceAdapter
    private val presenter: IDetailContract.IDetailPresenter = DetailPresenter(this)

    override fun initView() {
        super.initView()
        toolbar = rootView.findViewById(R.id.sentence_fragment_toolbar)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        playBt = rootView.findViewById(R.id.action_play)
        playBt.setImageResource(
            if (presenter.isMusicPlaying()) R.drawable.ic_baseline_pause_24 else R.drawable.ic_baseline_play_arrow_24
        )

        playBt.setOnClickListener {
            presenter.onPlayBtClick()
        }

        shareBt = rootView.findViewById(R.id.sentence_fragment_share)
        shareBt.setOnClickListener {
            showShareDialog()
        }

        presenter.setOnPlayFinishCallback {
            playBt.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        }
    }

    override fun initList() {
        listAdapter = SentenceAdapter(articleTitle)

        listView.layoutManager = LinearLayoutManager(context)
        listView.adapter = listAdapter
        presenter.setItemClickListenerForAdapter(listAdapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listAdapter.updateData(presenter.fetchData())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.initSentenceHelper(context, articleId, articleTitle)
    }

    override fun updatePlayIconState(isPlaying: Boolean) {
        if (isPlaying) {
            playBt.setImageResource(R.drawable.ic_baseline_pause_24)
        } else {
            playBt.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        }
    }

    override fun notifySentenceColor(position: Int, oldPosition: Int) {
        listAdapter.playSentence(position, oldPosition)
    }

    override fun updateRecordPlayBtState(position: Int, isPlaying: Boolean) {
        if (isPlaying) {
            (listView.findViewHolderForAdapterPosition(position) as SentenceAdapter.SentenceViewHolder)
                .playIcon.setImageResource(R.drawable.ic_baseline_stop_24)
        } else {
            (listView.findViewHolderForAdapterPosition(position) as SentenceAdapter.SentenceViewHolder)
                .playIcon.setImageResource(R.drawable.ic_baseline_play_arrow_24)

        }
    }

    override fun updateRecordBtStateByPosition(position: Int, record: Boolean) {
        if (record)
            (listView.findViewHolderForAdapterPosition(position) as SentenceAdapter.SentenceViewHolder)
                .recordBt.setImageResource(R.drawable.ic_baseline_mic_none_24)
    }

    override fun smoothListIfNeed(curPlaySentence: Int, listSize: Int) {
        val lastVisiblePosition = (listView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        if (curPlaySentence >= lastVisiblePosition && lastVisiblePosition < listSize - 1) {
            listView.smoothScrollToPosition(lastVisiblePosition + 1)
        }
    }

    override fun finishPlayRecordSentence() {
        listAdapter.finishPlayRecordSentence()
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