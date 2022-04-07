package com.ljl.englishfollowup.base

interface OnItemClickListener {
    fun onItemClick(position: Int)
}

interface OnSentenceClickListener : OnItemClickListener {
    fun onRecordClick(position: Int, record: Boolean)

    fun onPlayClick(position: Int, play: Boolean)
}