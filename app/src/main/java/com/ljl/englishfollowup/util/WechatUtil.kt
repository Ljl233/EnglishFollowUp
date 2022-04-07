package com.ljl.englishfollowup.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

/**
 * 直接文件到微信好友
 *
 * @param picFile 文件路径
 */
fun shareWechatFriend(mContext: Context, picFile: File?) {
    //首先判断是否安装微信
//        if (isInstallApp(mContext, ShareUtils.PACKAGE_WECHAT)) {
    val intent = Intent()
    intent.setPackage("com.tencent.mm")
    intent.action = Intent.ACTION_SEND
    var type = "*/*"
    for (i in MATCH_ARRAY.indices) {
        //判断文件的格式
        if (picFile?.getAbsolutePath().toString().contains(MATCH_ARRAY[i][0])) {
            type = MATCH_ARRAY[i][1]
            break
        }
    }
    intent.type = type
    var uri: Uri? = null
    if (picFile != null) {
        //这部分代码主要功能是判断了下文件是否存在，在android版本高过7.0（包括7.0版本）
        //当前APP是不能直接向外部应用提供file开头的的文件路径，
        //需要通过FileProvider转换一下。否则在7.0及以上版本手机将直接crash。
        try {
            val applicationInfo = mContext.applicationInfo
            val targetSDK = applicationInfo.targetSdkVersion
            uri = if (targetSDK >= Build.VERSION_CODES.N &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            ) {
                FileProvider.getUriForFile(
                    mContext,
                    mContext.applicationContext.packageName + ".fileprovider", picFile
                )
            } else {
                Uri.fromFile(picFile)
            }
            intent.putExtra(Intent.EXTRA_STREAM, uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//    if (getVersionCode(mContext, PACKAGE_WECHAT) > VERSION_CODE_FOR_WEI_XIN_VER7) {
        // 微信7.0及以上版本
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_STREAM, uri)
//    }
    mContext.startActivity(Intent.createChooser(intent, "分享文件"))
//} else {
//    Toast.makeText(mContext, "您需要安装微信客户端", Toast.LENGTH_LONG).show()
//}
}

// 建立一个文件类型与文件后缀名的匹配表
private val MATCH_ARRAY = arrayOf(
    arrayOf(".3gp", "video/3gpp"),
    arrayOf(".apk", "application/vnd.android.package-archive"),
    arrayOf(".asf", "video/x-ms-asf"),
    arrayOf(".avi", "video/x-msvideo"),
    arrayOf(".bin", "application/octet-stream"),
    arrayOf(".bmp", "image/bmp"),
    arrayOf(".c", "text/plain"),
    arrayOf(".class", "application/octet-stream"),
    arrayOf(".conf", "text/plain"),
    arrayOf(".cpp", "text/plain"),
    arrayOf(".doc", "application/msword"),
    arrayOf(".exe", "application/octet-stream"),
    arrayOf(".gif", "image/gif"),
    arrayOf(".gtar", "application/x-gtar"),
    arrayOf(".gz", "application/x-gzip"),
    arrayOf(".h", "text/plain"),
    arrayOf(".htm", "text/html"),
    arrayOf(".html", "text/html"),
    arrayOf(".jar", "application/java-archive"),
    arrayOf(".java", "text/plain"),
    arrayOf(".jpeg", "image/jpeg"),
    arrayOf(".jpg", "image/jpeg"),
    arrayOf(".js", "application/x-javascript"),
    arrayOf(".log", "text/plain"),
    arrayOf(".m3u", "audio/x-mpegurl"),
    arrayOf(".m4a", "audio/mp4a-latm"),
    arrayOf(".m4b", "audio/mp4a-latm"),
    arrayOf(".m4p", "audio/mp4a-latm"),
    arrayOf(".m4u", "video/vnd.mpegurl"),
    arrayOf(".m4v", "video/x-m4v"),
    arrayOf(".mov", "video/quicktime"),
    arrayOf(".mp2", "audio/x-mpeg"),
    arrayOf(".mp3", "audio/x-mpeg"),
    arrayOf(".mp4", "video/mp4"),
    arrayOf(".mpc", "application/vnd.mpohun.certificate"),
    arrayOf(".mpe", "video/mpeg"),
    arrayOf(".mpeg", "video/mpeg"),
    arrayOf(".mpg", "video/mpeg"),
    arrayOf(".mpg4", "video/mp4"),
    arrayOf(".mpga", "audio/mpeg"),
    arrayOf(".msg", "application/vnd.ms-outlook"),
    arrayOf(".ogg", "audio/ogg"),
    arrayOf(".pdf", "application/pdf"),
    arrayOf(".png", "image/png"),
    arrayOf(".pps", "application/vnd.ms-powerpoint"),
    arrayOf(".ppt", "application/vnd.ms-powerpoint"),
    arrayOf(".prop", "text/plain"),
    arrayOf(".rar", "application/x-rar-compressed"),
    arrayOf(".rc", "text/plain"),
    arrayOf(".rmvb", "audio/x-pn-realaudio"),
    arrayOf(".rtf", "application/rtf"),
    arrayOf(".sh", "text/plain"),
    arrayOf(".tar", "application/x-tar"),
    arrayOf(".tgz", "application/x-compressed"),
    arrayOf(".txt", "text/plain"),
    arrayOf(".wav", "audio/x-wav"),
    arrayOf(".wma", "audio/x-ms-wma"),
    arrayOf(".wmv", "audio/x-ms-wmv"),
    arrayOf(".wps", "application/vnd.ms-works"),
    arrayOf(".xml", "text/plain"),
    arrayOf(".z", "application/x-compress"),
    arrayOf(".zip", "application/zip"),
    arrayOf("", "*/*")
)
