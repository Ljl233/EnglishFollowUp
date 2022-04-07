package com.ljl.englishfollowup.record

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ljl.englishfollowup.R
import com.ljl.englishfollowup.util.MediaPlayerHelper
import com.ljl.englishfollowup.util.getTitleFromName
import com.ljl.englishfollowup.util.shareWechatFriend
import java.io.File
import java.util.*

class RecordAdapter : RecyclerView.Adapter<RecordAdapter.VH>() {

    private val data: MutableList<String> = mutableListOf()
    private var context: Context? = null
    private val mediaPlayerHelper by lazy { MediaPlayerHelper(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        if (context == null) {
            context = parent.context
        }

        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bindView(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateData(recordPaths: MutableList<String>) {
        data.clear()
        data.addAll(recordPaths)
        notifyDataSetChanged()
    }

    private var curPlayPath = ""

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(path: String) {
            val file = File(path)
            if (!file.isFile) {
                itemView.visibility = View.GONE
                return
            }
            val title = getTitleFromName(file.name)
            itemView.findViewById<TextView>(R.id.article_title).text = title

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = file.lastModified()
            val time = String.format(
                "%d:%d  %d/%d",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH)
            )
            itemView.findViewById<TextView>(R.id.record_time).text = time

            val playBt = itemView.findViewById<ImageView>(R.id.record_play)
            playBt.setImageResource(if (curPlayPath == path) R.drawable.ic_baseline_stop_24 else R.drawable.ic_baseline_play_arrow_24)
            playBt.setOnClickListener {
                if (curPlayPath == path) {
                    mediaPlayerHelper.stop()
                    playBt.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    curPlayPath = ""
                } else {
                    if (mediaPlayerHelper.isPlaying) {
                        mediaPlayerHelper.stop()
                    }
                    mediaPlayerHelper.setOnCompletionListener {
                        it.reset()
                        playBt.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                        curPlayPath = ""
                    }
                    mediaPlayerHelper.play(path)
                    playBt.setImageResource(R.drawable.ic_baseline_stop_24)
                    curPlayPath = path
                }
            }

            val shareBt = itemView.findViewById<ImageView>(R.id.record_share)
            shareBt.setOnClickListener {
                context?.let { c -> shareWechatFriend(c, File(path)) }
            }

        }
    }

}