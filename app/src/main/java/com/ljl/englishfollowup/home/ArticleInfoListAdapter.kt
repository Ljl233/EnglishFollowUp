package com.ljl.englishfollowup.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ljl.englishfollowup.R
import com.ljl.englishfollowup.base.OnItemClickListener
import com.ljl.englishfollowup.model.ArticleInfo

class ArticleInfoListAdapter : RecyclerView.Adapter<ArticleInfoListAdapter.VH>() {

    private val data: MutableList<ArticleInfo> = mutableListOf()
    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_article_info, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bindData(data[position])
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateList(articleInfos: List<ArticleInfo>) {
        data.clear()
        data.addAll(articleInfos)
        notifyDataSetChanged()
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(info: ArticleInfo) {
            val titleView = itemView.findViewById<TextView>(R.id.info_title)
            val abstractView = itemView.findViewById<TextView>(R.id.info_abstract)
            val coverView = itemView.findViewById<ImageView>(R.id.info_cover)

            titleView.text = info.title
            abstractView.text = info.abstract
            if (info.picture != null) {
                coverView.setImageBitmap(info.picture)
            } else {
                coverView.setImageResource(R.drawable.youth)
            }
        }
    }
}