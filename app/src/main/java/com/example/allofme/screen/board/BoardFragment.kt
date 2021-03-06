package com.example.allofme.screen.board

import android.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.allofme.R
import com.example.allofme.data.preference.MyPreferenceManager
import com.example.allofme.databinding.FragmentBoardBinding
import com.example.allofme.screen.base.BaseFragment
import com.example.allofme.screen.board.articlelist.ArticleListFragment
import com.example.allofme.screen.board.articlelist.FieldCategory
import com.example.allofme.screen.board.articlelist.YearCategory
import com.example.allofme.screen.board.postArticle.PostArticleActivity
import com.example.allofme.screen.main.MainActivity
import com.example.allofme.screen.main.MainTabMenu
import com.example.allofme.widget.adapter.ArticleListFragmentPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class BoardFragment: BaseFragment<BoardViewModel,FragmentBoardBinding>() {

    override val viewModel by viewModel<BoardViewModel>()

    override fun getViewBinding(): FragmentBoardBinding = FragmentBoardBinding.inflate(layoutInflater)

    private lateinit var viewPagerAdapter: ArticleListFragmentPagerAdapter

    private val myPreferenceManager by inject<MyPreferenceManager>()

    private val firebaseAuth by inject<FirebaseAuth>()

    override fun initViews() = with(binding) {

        initViewPager()

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

        postArticleButton.setOnClickListener {
            userSessionCheck()
        }

    }

    private fun userSessionCheck() {
        if (myPreferenceManager.getIdToken() == null || firebaseAuth.currentUser == null) { //????????? ????????? ????????? ????????? ???????????? ????????????.
            alertLogin {
                lifecycleScope.launch {
                    (requireActivity() as MainActivity).goToTab(MainTabMenu.MY)
                }
            }
        } else {
            startActivity(PostArticleActivity.newIntent(requireContext(),null))
        }
    }


    private fun alertLogin(function: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("???????????? ???????????????.")
            .setMessage("?????? ??????????????? ???????????? ???????????????. ??????????????? ???????????????????")
            .setPositiveButton("??????") { dialog, _ ->
                function()
                dialog.dismiss()
            }
            .setNegativeButton("??????") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun chipChange(year: YearCategory) {
        viewPagerAdapter.fragmentList.forEach {
            it.viewModel.setYear(year)
        }

    }

    override fun observeData() = viewModel.boardStateLiveData.observe(viewLifecycleOwner) {
        when (it) {
            is BoardState.Success -> {
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

        }


    }


    companion object {

        fun newInstance() = BoardFragment()

        const val TAG = "BoardFragment"
    }
}