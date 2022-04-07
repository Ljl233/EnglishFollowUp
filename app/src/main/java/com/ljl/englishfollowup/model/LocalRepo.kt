package com.ljl.englishfollowup.model

import com.ljl.englishfollowup.util.LyricUtil

class LocalRepo {
    fun getArticleInfos(): List<ArticleInfo> {
        val infos = ArrayList<ArticleInfo>()
        infos.add(
            ArticleInfo(
                -1,
                "青春",
                "Youth is not a time of life, it is a state of mind; it is not a matter of rosy cheeks, red lips and supple knees;",
                null
            )
        )
        return infos
    }

    fun getLyricByArticleId(id: Int = -1): List<LyricUtil.Line>? {
        if (id == -1) {
            return LyricUtil.parseLrcFromRes(LyricUtil.getYouthLrcResId())
        }
        // todo: article id 和 文件路径 的映射
        return null
    }

    fun getLyricTimeByArticleId(id: Int): List<LyricUtil.LyricTime>? {
        return getLyricByArticleId(id)?.let { LyricUtil.parseTimeFromLine(it) }
    }

    fun getMusicPathById(id: Int): String? {
        return null
    }
}