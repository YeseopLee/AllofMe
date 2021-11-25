package com.example.allofme.screen.board.articlelist.detail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.allofme.data.entity.ArticleEntity
import com.example.allofme.databinding.ActivityDetailBinding
import com.example.allofme.model.CellType
import com.example.allofme.model.board.ArticleListModel
import com.example.allofme.model.board.article.detail.ArticleModel
import com.example.allofme.model.board.postArticle.PostArticleModel
import com.example.allofme.screen.base.BaseActivity
import com.example.allofme.screen.board.articlelist.ArticleListFragment
import com.example.allofme.screen.board.articlelist.ArticleListFragment.Companion.ARTICLE_KEY
import com.example.allofme.screen.board.articlelist.ArticleListViewModel
import com.example.allofme.screen.board.postArticle.PostArticleActivity
import com.example.allofme.screen.provider.ResourcesProvider
import com.example.allofme.widget.adapter.ModelRecyclerAdapter
import com.example.allofme.widget.adapter.listener.AdapterListener
import com.example.allofme.widget.adapter.listener.board.BoardListListener
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailActivity : BaseActivity<DetailViewModel, ActivityDetailBinding>() {
    override val viewModel by viewModel<DetailViewModel> {
        parametersOf(
            intent.getParcelableExtra<ArticleEntity>(ARTICLE_KEY)
        )
    }

    private val articleId by lazy { intent.getStringExtra(ARTICLE_KEY)!! }

    private val resourcesProvider by inject<ResourcesProvider>()

    override fun getViewBinding(): ActivityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)

    private val adapter by lazy {
        ModelRecyclerAdapter<ArticleModel, DetailViewModel> (
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : AdapterListener {
            }
        )
    }

    override fun initViews() {
        viewModel.getArticleDetail(articleId)
        binding.recyclerView.adapter = adapter
    }

    override fun observeData() = viewModel.detailArticleStateLiveData.observe(this) {
        when(it) {
            is DetailState.Uninitialized -> Unit
            is DetailState.Loading -> handleStateLoading()
            is DetailState.Success -> handleStateSuccess(it)
            is DetailState.Error -> handleStateError(it)
        }
    }

    private fun handleStateLoading() {}

    private fun handleStateSuccess(state: DetailState.Success) = with(binding) {
        titleTextView.text = state.article.title

        adapter.submitList(state.article.content)
    }

    private fun handleStateError(state: DetailState.Error) {}

    companion object {
        fun newIntent(context: Context, articleId: String) =
            Intent(context, DetailActivity::class.java).apply {
                putExtra(ARTICLE_KEY, articleId)
            }
    }


}