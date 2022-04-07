package com.ljl.englishfollowup.model

import com.ljl.englishfollowup.util.LyricUtil

data class SentenceDetail(
    val english: String,
    val startTime: LyricUtil.LyricTime,
    var recordFilePath: String?
)
