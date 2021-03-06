package com.ljl.englishfollowup.synthesis

import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ljl.englishfollowup.R
import com.ljl.englishfollowup.util.MediaPlayerHelper
import com.ljl.englishfollowup.util.getIdFromFileName
import com.ljl.englishfollowup.util.getSynthesisDir
import com.ljl.englishfollowup.util.shareWechatFriend
import java.io.File

const val BUNDLE_SYNTHESIS_DIR = "BUNDLE_SYNTHESIS_DIR"
const val BUNDLE_TITLE = "BUNDLE_TITLE"

private const val SYNTHESIS_STATE_UNSTART = 0
private const val SYNTHESIS_STATE_COMPOSE = 1
private const val SYNTHESIS_STATE_FINISH = 2
private const val SYNTHESIS_STATE_ERROR = 3


class SynthesisFragment : Fragment() {
    private var synthesisDir: String? = null
    private var title: String? = null

    private lateinit var toolbar: Toolbar
    private lateinit var titleTv: TextView
    private lateinit var synthesisBt: Button
    private lateinit var synthesisStateTv: TextView
    private lateinit var playBt: ImageView
    private lateinit var shareWechatBt: ImageView
    private lateinit var downloadBt: ImageView
    private lateinit var downloadTv: TextView


    private var state = SYNTHESIS_STATE_UNSTART
    private var resultFilePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            synthesisDir = it.getString(BUNDLE_SYNTHESIS_DIR)
            title = it.getString(BUNDLE_TITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_synthesis, container, false)

        toolbar = root.findViewById<Toolbar?>(R.id.synthesis_fragment_toolbar).apply {
            setNavigationOnClickListener { findNavController().popBackStack() }
        }
        titleTv = root.findViewById<TextView?>(R.id.synthesis_fragment_title).apply {
            text = title
        }

        synthesisBt = root.findViewById<Button?>(R.id.start_synthesis).apply {
            setOnClickListener { startSynthesis() }
        }
        synthesisStateTv = root.findViewById(R.id.synthesis_state)
        playBt = root.findViewById<ImageView?>(R.id.record_play).apply {
            setOnClickListener { playRecord() }
        }
        shareWechatBt = root.findViewById<ImageView?>(R.id.share_wechat_bt).apply {
            setOnClickListener {
                shareWechatFriend(context, getResultFile())
            }
        }
        downloadBt = root.findViewById<ImageView?>(R.id.share_download).apply {
            setOnClickListener {
                downloadToLocal()
            }
        }
        downloadTv = root.findViewById(R.id.share_download_text)
        return root
    }

    private fun startSynthesis() {
        updateState(SYNTHESIS_STATE_COMPOSE)

        if (TextUtils.isEmpty(synthesisDir) || !File(synthesisDir!!).isDirectory) {
            updateState(SYNTHESIS_STATE_ERROR)
            return
        }

        val recordFiles = mutableListOf<String>()
        File(synthesisDir!!).listFiles()?.forEach {
            recordFiles.add(it.absolutePath)
        }

        recordFiles.sortWith { file1: String, file2: String ->
            val file1Id = getIdFromFileName(file1)
            val file2Id = getIdFromFileName(file2)
            file1Id - file2Id
        }
        resultFilePath = joinAudio(recordFiles)

        if (TextUtils.isEmpty(resultFilePath)) {
            updateState(SYNTHESIS_STATE_ERROR)
        } else {
            updateState(SYNTHESIS_STATE_FINISH)
        }
    }

    private fun updateState(state: Int) {
        this.state = state
        synthesisStateTv.text = when (state) {
            SYNTHESIS_STATE_UNSTART -> "??????????????????"
            SYNTHESIS_STATE_ERROR -> "????????????"
            SYNTHESIS_STATE_COMPOSE -> "?????????..."
            SYNTHESIS_STATE_FINISH -> {
                playBt.visibility = View.VISIBLE
                "????????????"
            }
            else -> "??????????????????"
        }
    }

    private fun getResultFile(): File? {
        if (!TextUtils.isEmpty(resultFilePath)) {
            return File(resultFilePath)
        }
        return null
    }


    private fun joinAudio(fileList: MutableList<String>): String {
        val synthesisDir = getSynthesisDir()
        val outputFile = File(synthesisDir, "${title}_${System.currentTimeMillis()}.aac")
        val resultStream = outputFile.outputStream()
        fileList.forEach {
            val fis = File(it).inputStream()
            val buff = ByteArray(1024)

            while (fis.read(buff) > -1) {
                resultStream.write(buff)
            }
            fis.close()
            File(it).delete()
        }
        resultStream.close()

        return outputFile.absolutePath
    }

    val mediaPlayer: MediaPlayerHelper = MediaPlayerHelper(context).apply {
        setOnCompletionListener {
            playBt.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        }
    }

    private fun playRecord() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            playBt.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        } else {
            mediaPlayer.play(resultFilePath)
            playBt.setImageResource(R.drawable.ic_baseline_stop_24)
        }
    }


    private fun downloadToLocal() {
        if (TextUtils.isEmpty(resultFilePath)) {
            return
        }
        val musicDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Music";
        val resultFile = File(resultFilePath)
        val targetFile = File(musicDir, resultFile.name)

        val fos = targetFile.outputStream()
        val fis = resultFile.inputStream()
        val buff = ByteArray(1024)

        while (fis.read(buff) > -1) {
            fos.write(buff)
        }
        fis.close()
        fos.close()

        downloadTv.text = "?????????" + targetFile.absolutePath
        Toast.makeText(context, "?????????${targetFile}", Toast.LENGTH_LONG).show()
    }
}