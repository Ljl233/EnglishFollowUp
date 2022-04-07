package com.ljl.englishfollowup.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.ljl.englishfollowup.R

abstract class BaseListFragment : Fragment() {

    lateinit var rootView: View
    lateinit var listView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(tag, "-onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(getLayoutId(), container, false)
        initView()
        Log.e(tag, "-onCreateView")
        return rootView
    }

    override fun onStart() {
        super.onStart()
        Log.e(tag, "-onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.e(tag, "-onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.e(tag, "-onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.e(tag, "-onStop")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e(tag, "-onAttach")
    }

    override fun onDetach() {
        super.onDetach()
        Log.e(tag, "-onDetach")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(tag, "-onDestroy")
    }

    open fun initView() {
        listView = rootView.findViewById(R.id.list)
        initList()
    }

    abstract fun initList()

    @LayoutRes
    open fun getLayoutId(): Int {
        return R.layout.fragment_list_base
    }


}