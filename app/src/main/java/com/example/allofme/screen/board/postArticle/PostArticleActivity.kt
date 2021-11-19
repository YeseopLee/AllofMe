package com.example.allofme.screen.board.postArticle

import android.content.Context
import android.content.Intent
import com.example.allofme.databinding.ActivityPostArticleBinding
import com.example.allofme.model.board.post.PostArticleModel
import com.example.allofme.screen.base.BaseActivity
import com.example.allofme.screen.provider.ResourcesProvider
import com.example.allofme.widget.adapter.ModelRecyclerAdapter
import com.example.allofme.widget.adapter.listener.board.articlelist.PostArticleListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class PostArticleActivity : BaseActivity<PostArticleViewModel, ActivityPostArticleBinding>() {
    override val viewModel by viewModel<PostArticleViewModel>()

    override fun getViewBinding(): ActivityPostArticleBinding = ActivityPostArticleBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    override fun initViews() = with(binding) {
        viewModel.fetchData()
        recyclerView.adapter = testAdapter

        floatbtn1.setOnClickListener {
            viewModel.editTextCount += 1
        }

        floatbtn2.setOnClickListener {
            viewModel.imageViewCount += 1
        }
    }

    private val testAdapter by lazy {
        ModelRecyclerAdapter<PostArticleModel, PostArticleViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : PostArticleListener {
                override fun onClickItem(model: PostArticleModel) {
                    //
                }
            }
        )

    }


    override fun observeData() = viewModel.editTextLiveData.observe(this) {
        testAdapter.submitList(it)
    }

    companion object {

        fun newIntent(context: Context) =
            Intent(context, PostArticleActivity::class.java).apply {

            }
    }

}