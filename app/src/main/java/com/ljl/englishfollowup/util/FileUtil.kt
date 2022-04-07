package com.ljl.englishfollowup.util

import com.ljl.englishfollowup.MyApplication
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.RandomAccessFile
import java.lang.Exception

fun getTitleFromName(fileName: String): String {
    return fileName.substring(0, fileName.indexOf("_"))
}

fun getRecordDir(articleTitle: String): String {
    val recordDir = File(MyApplication.context?.cacheDir, articleTitle)
    if (!recordDir.exists()) {
        recordDir.mkdirs()
    }
    return recordDir.absolutePath
}

fun getSynthesisDir(): String {
    val synthesisDir = File(MyApplication.context?.filesDir, "record")
    if (!synthesisDir.exists()) {
        synthesisDir.mkdirs()
    }
    return synthesisDir.absolutePath
}

fun appendFile(fromFile: File, toFile: File): Boolean {
    var randomAccessFile: RandomAccessFile? = null
    var inputStream: FileInputStream? = null
    try {
        randomAccessFile = RandomAccessFile(toFile, "rw")
        randomAccessFile.seek(randomAccessFile.length())

        inputStream = fromFile.inputStream()
        val buff = ByteArray(1024)
        var len = 0
        while (inputStream.read(buff).also { len = it } != -1) {
            randomAccessFile.write(buff)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    } finally {
        inputStream?.close()
        randomAccessFile?.close()
    }
    return true
}


