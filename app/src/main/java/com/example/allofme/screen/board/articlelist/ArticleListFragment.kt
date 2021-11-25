package com.example.allofme.screen.board.articlelist

import android.util.Log
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.allofme.data.entity.ArticleEntity
import com.example.allofme.databinding.FragmentArticleListBinding
import com.example.allofme.model.board.ArticleListModel
import com.example.allofme.screen.base.BaseFragment
import com.example.allofme.screen.board.articlelist.detail.DetailActivity
import com.example.allofme.screen.provider.ResourcesProvider
import com.example.allofme.widget.adapter.ModelRecyclerAdapter
import com.example.allofme.widget.adapter.listener.board.BoardListListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ArticleListFragment: BaseFragment<ArticleListViewModel, FragmentArticleListBinding>() {
    val fieldCategory by lazy { arguments?.getSerializable(FIELD_CATEGORY_KEY) as FieldCategory}

    override val viewModel by viewModel<ArticleListViewModel> {
        parametersOf(
            fieldCategory
        )
    }

    override fun getViewBinding(): FragmentArticleListBinding = FragmentArticleListBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val adapter by lazy {
        ModelRecyclerAdapter<ArticleListModel, ArticleListViewModel> (
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : BoardListListener {
                override fun onClickItem(model: ArticleListModel) {
                    startActivity(
                        DetailActivity.newIntent(
                            requireContext(),
                            model.articleId
                        )
                    )
                }
            }
        )
    }

    override fun initViews() = with(binding) {
        recyclerView.adapter = adapter

    }

    override fun onResume() = with(Dispatchers.IO) {
        viewModel.fetchData()
        super.onResume()
    }


    override fun observeData() = viewModel.articleListStateLiveData.observe(viewLifecycleOwner) {
        when(it) {
            is ArticleListState.Loading -> handleStateLoading()
            is ArticleListState.Success -> handleStateSuccess(it)
            is ArticleListState.Error -> handleStateError(it)
            else -> Unit
        }
    }

    private fun handleStateLoading() {
        binding.progressBar.isVisible = true
    }

    private fun handleStateSuccess(state: ArticleListState.Success) {
        binding.progressBar.isGone = true
        adapter.submitList(state.articleList)
    }

    private fun handleStateError(state: ArticleListState.Error) {
        binding.progressBar.isGone = true
    }


    companion object {
        const val FIELD_CATEGORY_KEY = "fieldCategory"
        const val ARTICLE_KEY = "Article"

        fun newInstance(
            fieldCategory: FieldCategory
        ): ArticleListFragment {
            return ArticleListFragment().apply {
                arguments = bundleOf(
                    FIELD_CATEGORY_KEY to fieldCategory
                )
            }
        }

    }


}