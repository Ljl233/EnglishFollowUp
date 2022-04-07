package com.ljl.englishfollowup.record

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ljl.englishfollowup.R
import java.io.File

class RecordListFragment : Fragment() {

    private lateinit var toolbar: Toolbar
    private lateinit var listView: RecyclerView

    private lateinit var listAdapter: RecordAdapter

    private val recordPaths: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_record_list, container, false)

        toolbar = root.findViewById<Toolbar?>(R.id.record_fragment_toolbar).apply {
            setOnClickListener { findNavController().popBackStack() }
        }
        listView = root.findViewById<RecyclerView?>(R.id.list).apply {
            layoutManager = LinearLayoutManager(context)
            listAdapter = RecordAdapter()
            adapter = listAdapter
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshData()
        listAdapter.updateData(recordPaths)
    }

    private fun refreshData() {
        val recordDir = File(context?.filesDir, "record")
        recordDir.listFiles()?.forEach {
            recordPaths.add(it.absolutePath)
        }
    }
}