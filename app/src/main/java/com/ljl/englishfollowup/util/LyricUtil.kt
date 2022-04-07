package com.ljl.englishfollowup.util

import androidx.annotation.RawRes
import com.ljl.englishfollowup.MyApplication
import com.ljl.englishfollowup.R
import com.ljl.englishfollowup.model.SentenceDetail
import java.io.*

class LyricUtil {
    companion object {
        private const val REGEX_LYRIC_INFO = "\\[\\w{2}:\\w+\\]"
        private const val REGEX_LYRIC_TIME = "\\[[0-9]{2}:[0-9]{2}.[0-9]{3}\\].+"

        fun parseLrcFromRes(@RawRes resId: Int) =
            MyApplication.context?.resources?.let {
                parseLrcFromInputStream(it.openRawResource(resId))
            }

        fun getYouthLrcResId(): Int = R.raw.youth_lyric

        fun parseLrcFromFile(file: File) = parseLrcFromInputStream(file.inputStream())

        private fun parseLrcFromInputStream(inputStream: InputStream): List<Line> {
            val reader = BufferedReader(InputStreamReader(inputStream))
            val lines = mutableListOf<Line>()
            var readLines: List<String>? = null
            try {
                readLines = reader.readLines()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            readLines?.forEach {
                parseLine(it)?.let { line ->
                    lines.add(line)
                }
            }
            return lines
        }

        private fun parseLine(line: String): Line? {
            val infoRegex = Regex(REGEX_LYRIC_INFO)
            val timeRegex = Regex(REGEX_LYRIC_TIME)
            if (line.matches(infoRegex)) {
                return InfoLine(
                    line.substring(1, line.indexOf(":")),
                    line.substring(line.indexOf(":") + 1, line.length - 1)
                )
            } else if (line.matches(timeRegex)) {
                val min = line.substring(1, line.indexOf(":")).toInt()
                val sec = line.substring(line.indexOf(":") + 1, line.indexOf(".")).toInt()
                val mill = line.substring(line.indexOf(".") + 1, line.indexOf("]") - 1).toInt()
                val lyric = line.substring(line.indexOf("]") + 1)
                return TimeLine(LyricTime(min, sec, mill), lyric)
            }

            return null
        }

        fun linesToSentences(lines: List<Line>): List<SentenceDetail> {
            val sentences = mutableListOf<SentenceDetail>()
            lines.forEach {
                if (it is TimeLine) {
                    sentences.add(SentenceDetail(it.lyric, it.time, null))
                }
            }
            return sentences
        }

        fun parseTimeFromLine(lines: List<Line>): List<LyricTime> {
            val times = mutableListOf<LyricTime>()
            lines.forEach {
                if (it is TimeLine) {
                    times.add(it.time)
                }
            }
            return times
        }
    }

    abstract class Line

    /**
     * 标识标签
     * 格式：[tag:des]
     * e.g. [ti:青春Youth]
     */
    data class InfoLine(
        val tag: String,
        val des: String
    ) : Line() {
        override fun toString(): String {
            return "[${tag}:$des]"
        }
    }

    /**
     * 时间标签
     * 格式 [mm:ss.fff] lyric
     * e.g. [00:07.000]Youth is not a time of life,
     */
    data class TimeLine(
        val time: LyricTime,
        val lyric: String
    ) : Line() {
        override fun toString(): String {
            return "[${time.min}:${time.sec}.${time.mill}]$lyric"
        }
    }

    data class LyricTime(
        val min: Int,
        val sec: Int,
        val mill: Int
    ) {
        fun toMillSec(): Int {
            return ((min * 60) + sec) * 1000 + mill
        }
    }
}

fun test() {
    LyricUtil.parseLrcFromRes(LyricUtil.getYouthLrcResId())?.forEach {
        println(it.toString())
    }
}