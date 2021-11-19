package com.example.allofme.widget.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.allofme.screen.board.articlelist.ArticleListFragment

class ArticleListFragmentPagerAdapter(
    fragment: Fragment,
    val fragmentList: List<ArticleListFragment>,
): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position]


}