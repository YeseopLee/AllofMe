package com.example.allofme.screen.board

import android.app.Activity
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.example.allofme.R
import com.example.allofme.databinding.FragmentBoardBinding
import com.example.allofme.screen.base.BaseFragment
import com.example.allofme.screen.board.article.ArticleListFragment
import com.example.allofme.screen.board.article.FieldCategory
import com.example.allofme.screen.board.article.YearCategory
import com.example.allofme.widget.adapter.ArticleListFragmentPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.viewmodel.ext.android.viewModel

class BoardFragment: BaseFragment<BoardViewModel,FragmentBoardBinding>() {

    override val viewModel by viewModel<BoardViewModel>()

    override fun getViewBinding(): FragmentBoardBinding = FragmentBoardBinding.inflate(layoutInflater)

    private lateinit var viewPagerAdapter: ArticleListFragmentPagerAdapter

    override fun initViews() = with(binding) {

        viewModel.initViewModel()

        orderChipGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.chipAll -> {
                    chipChange(YearCategory.ALL)
                }
                R.id.chipNew -> {
                    chipChange(YearCategory.NEW)
                }
                R.id.chipTwoThree -> {
                    chipChange(YearCategory.TWO_THREE)
                }
                R.id.chipFourFive -> {
                    chipChange(YearCategory.FOUR_FIVE)
                }
                R.id.chipUpFive -> {
                    chipChange(YearCategory.FIVE)
                }
            }
        }

    }

    private fun chipChange(year: YearCategory) {
        viewPagerAdapter.fragmentList.forEach {
            it.viewModel.setYear(year)
        }

    }

    override fun observeData() = viewModel.boardStateLiveData.observe(viewLifecycleOwner) {
        when (it) {
            is BoardState.Success -> {
                initViewPager()
            }
            else -> Unit
        }

    }

    private fun initViewPager() = with(binding) {
        val fieldCategories = FieldCategory.values()

        if(::viewPagerAdapter.isInitialized.not()) {

            val articleListFragment = fieldCategories.map {
                ArticleListFragment.newInstance(it)
            }

            viewPagerAdapter = ArticleListFragmentPagerAdapter(
                this@BoardFragment,
                articleListFragment
            )

            viewPager.adapter = viewPagerAdapter

            viewPager.offscreenPageLimit = fieldCategories.size
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.setText(fieldCategories[position].categoryNameId)
            }.attach()

            Log.e("initViews,", viewPagerAdapter.fragmentList.first().fieldCategory.toString())
            Log.e("initViews,", viewPagerAdapter.fragmentList[1].fieldCategory.toString())

        }


    }


    companion object {

        fun newInstance() = BoardFragment()

        const val TAG = "BoardFragment"
    }
}