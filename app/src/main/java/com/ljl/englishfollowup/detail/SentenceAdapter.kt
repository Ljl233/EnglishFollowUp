package com.ljl.englishfollowup.detail

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ljl.englishfollowup.R
import com.ljl.englishfollowup.base.OnSentenceClickListener
import com.ljl.englishfollowup.model.SentenceDetail
import com.ljl.englishfollowup.util.MediaPlayerHelper
import com.ljl.englishfollowup.util.generateOutputFileName
import com.ljl.englishfollowup.util.getRecordDir
import java.io.File

class SentenceAdapter(private val articleTitle: String) : RecyclerView.Adapter<SentenceAdapter.SentenceViewHolder>() {

    val data = mutableListOf<SentenceDetail>()
    var itemClickListener: OnSentenceClickListener? = null

    var curSentencePlay = -1
    var lastSentencePlay = -1

    private var curRecordPos = -1
    private var curPlayRecordPos = -1

    private lateinit var context: Context
    private val recordDir by lazy { getRecordDir(articleTitle) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SentenceViewHolder {
        if (!this::context.isInitialized) {
            context = parent.context
        }
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_detail_sentence, parent, false)
        return SentenceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SentenceViewHolder, position: Int) {
        holder.bindView(
            data[position], position == curSentencePlay,
            curRecordPos == position, curPlayRecordPos == position
        )

        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(holder.adapterPosition)
        }

        holder.recordBt.setOnClickListener {
            onRecordBtClick(holder)
        }

        holder.playIcon.setOnClickListener {
            onPlayRecordBtClick(holder)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateData(list: List<SentenceDetail>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    private fun onRecordBtClick(holder: SentenceViewHolder) {
        val position = holder.adapterPosition
        if (curRecordPos == position) {
            itemClickListener?.onRecordClick(curRecordPos, record = false)
            data[position].recordFilePath = File(recordDir, generateOutputFileName(articleTitle, position)).toString()
            curRecordPos = -1
            holder.recordBt.setImageResource(R.drawable.ic_baseline_mic_none_24)
            holder.playIcon.apply {
                visibility = View.VISIBLE
                setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }

        } else {
            if (curRecordPos != -1) {
                itemClickListener?.onRecordClick(curRecordPos, record = false)
                data[position].recordFilePath = File(recordDir, generateOutputFileName(articleTitle, position)).toString()
            }
            curRecordPos = position
            itemClickListener?.onRecordClick(curRecordPos, record = true)
            holder.recordBt.setImageResource(R.drawable.ic_baseline_mic_24)
        }
    }

    private fun onPlayRecordBtClick(holder: SentenceViewHolder) {
        val position = holder.adapterPosition
        if (TextUtils.isEmpty(data[position].recordFilePath)) {
            return
        }
        if (curPlayRecordPos == position) {
            itemClickListener?.onPlayClick(curPlayRecordPos, play = false)
            curPlayRecordPos = -1
        } else {
            if (curPlayRecordPos != -1) {
                itemClickListener?.onPlayClick(curPlayRecordPos, play = false)
            }
            curPlayRecordPos = position
            itemClickListener?.onPlayClick(curPlayRecordPos, play = true)
        }
    }

    fun finishPlayRecordSentence() {
        curPlayRecordPos = -1
    }

    fun playSentence(position: Int) {
        lastSentencePlay = curSentencePlay
        curSentencePlay = position
        notifyItemChanged(lastSentencePlay)
        notifyItemChanged(curSentencePlay)
    }

    inner class SentenceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var sentence: TextView
        lateinit var playIcon: ImageView
        lateinit var recordBt: ImageView
        private var path: String? = ""

        fun bindView(sentenceDetail: SentenceDetail, isSentencePlaying: Boolean, isRecording: Boolean, isPlayRecoding: Boolean) {
            sentence = itemView.findViewById(R.id.sentence_english)
            sentence.text = sentenceDetail.english
            sentence.setTextColor(
                if (isSentencePlaying) context.resources.getColor(R.color.purple_500)
                else context.resources.getColor(R.color.black)
            )

            recordBt = itemView.findViewById(R.id.sentence_record)
            recordBt.setImageResource(
                if (isRecording) R.drawable.ic_baseline_mic_24
                else R.drawable.ic_baseline_mic_none_24
            )

            path = sentenceDetail.recordFilePath
            playIcon = itemView.findViewById(R.id.sentence_play_record)
            playIcon.visibility = if (TextUtils.isEmpty(path)) {
                View.INVISIBLE
            } else {
                playIcon.setImageResource(
                    if (isPlayRecoding) R.drawable.ic_baseline_pause_24
                    else R.drawable.ic_baseline_play_arrow_24
                )
                View.VISIBLE
            }
        }

    }
}