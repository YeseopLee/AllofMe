package com.example.allofme.screen.board.articlelist.detail

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.allofme.R
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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
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

    private val firebaseAuth by inject<FirebaseAuth>()

    private var clicked: Boolean = false

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

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.deleteButton.setOnClickListener {
            viewModel.deleteArticle(articleId, firebaseAuth.currentUser?.uid ?: "null")
        }

        binding.updateButton.setOnClickListener {
            startActivity(
                PostArticleActivity.newIntent(
                    this,
                    articleId
                )
            )
        }

        viewModel.checkIsMe(articleId, firebaseAuth.currentUser?.uid.toString()) // 본인 글인지 확인
        viewModel.getArticleDetail(articleId)
        binding.recyclerView.adapter = adapter
    }

    override fun observeData()  {
        viewModel.detailArticleStateLiveData.observe(this) {
            when(it) {
                is DetailState.Uninitialized -> Unit
                is DetailState.Loading -> handleStateLoading()
                is DetailState.Success -> handleStateSuccess(it)
                is DetailState.Error -> handleStateError(it)
                is DetailState.Finish -> handleFinishState()
            }
        }
        viewModel.isMeLiveData.observe(this) {
            when (it) {
                true -> handleDialButton()
            }
        }
    }

    override fun onResume() {
        viewModel.getArticleDetail(articleId)
        super.onResume()
    }

    private fun handleDialButton() {
        binding.dialButton.isVisible = true
        binding.dialButton.setOnClickListener {
            if(!clicked) {
                binding.updateButton.animate().translationY(-resources.getDimension(R.dimen.update))
                binding.deleteButton.animate().translationY(-resources.getDimension(R.dimen.delete))
                clicked = true
            } else {
                binding.updateButton.animate().translationY(0f)
                binding.deleteButton.animate().translationY(0f)
                clicked = false
            }
        }
    }

    private fun handleStateLoading() {
        binding.progressBar.isVisible = true
    }

    private fun handleFinishState() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.progressBar.isGone = true
            finish() },500)
    }

    private fun handleStateSuccess(state: DetailState.Success) = with(binding) {
        binding.progressBar.isGone = true
        titleTextView.text = state.article.title

        adapter.submitList(state.article.content)
    }

    private fun handleStateError(state: DetailState.Error) {}

    companion object {
        fun newIntent(context: Context, articleId: String) =
            Intent(context, DetailActivity::class.java).apply {
                putExtra(ARTICLE_KEY, articleId)
            }
        const val ARTICLE_KEY = "Article"

    }


}