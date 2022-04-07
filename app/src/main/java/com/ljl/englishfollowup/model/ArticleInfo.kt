package com.ljl.englishfollowup.model

import android.graphics.Bitmap

data class ArticleInfo(
    val id: Int,
    val title: String,
    val abstract: String,
    val picture: Bitmap?
)
