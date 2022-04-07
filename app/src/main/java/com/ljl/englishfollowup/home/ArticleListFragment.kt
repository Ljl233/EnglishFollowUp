package com.ljl.englishfollowup.home

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ljl.englishfollowup.R
import com.ljl.englishfollowup.base.BaseListFragment
import com.ljl.englishfollowup.base.OnItemClickListener
import com.ljl.englishfollowup.detail.DetailFragment
import com.ljl.englishfollowup.model.ArticleInfo
import com.ljl.englishfollowup.model.LocalRepo

class ArticleListFragment : BaseListFragment() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerIcon: ImageView
    private lateinit var wechatBt: TextView
    private lateinit var myRecordBt: TextView

    private val listAdapter by lazy { ArticleInfoListAdapter() }
    private val localRepo by lazy { LocalRepo() }
    private val listData: MutableList<ArticleInfo> = mutableListOf()

    companion object {
        const val BUNDLE_ARTICLE_ID = "bundle_article_id"
        const val BUNDLE_ARTICLE_TITLE = "bundle_article_title"
    }

    override fun initList() {
        drawerLayout = rootView as DrawerLayout
        drawerIcon = rootView.findViewById(R.id.drawer_icon)
        drawerIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        wechatBt = rootView.findViewById(R.id.drawer_wechat)
        wechatBt.setOnClickListener { }
        myRecordBt = rootView.findViewById(R.id.drawer_my_record)
        myRecordBt.setOnClickListener { findNavController().navigate(R.id.recordListFragment) }

        listView.layoutManager = LinearLayoutManager(context)
        listView.adapter = listAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshData()
        listAdapter.itemClickListener = object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                val bundle = Bundle()
                bundle.putInt(BUNDLE_ARTICLE_ID, listData[position].id)
                bundle.putString(BUNDLE_ARTICLE_TITLE, listData[position].title)
                findNavController().navigate(R.id.detailFragment, bundle)
            }
        }
        listAdapter.updateList(listData)
    }

    private fun refreshData() {
        listData.clear()
        listData.addAll(localRepo.getArticleInfos())
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

}