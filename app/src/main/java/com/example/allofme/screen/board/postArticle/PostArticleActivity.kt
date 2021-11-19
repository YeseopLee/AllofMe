package com.example.allofme.screen.board.postArticle

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.allofme.databinding.ActivityPostArticleBinding
import com.example.allofme.model.CellType
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
            viewModel._postArticleLiveData.add(
                PostArticleModel(
                    id = viewModel.viewHolderCount.toLong(),
                    type = CellType.ARTICLE_EDIT_CELL
                )
            )
            viewModel._postArticleLiveData.add(
                PostArticleModel(
                    id = viewModel.viewHolderCount.toLong() + 1,
                    type = CellType.ARTICLE_IMAGE_CELL,
                )
            )
            viewModel.viewHolderCount += 2
            recyclerView.setItemViewCacheSize(viewModel.viewHolderCount) // 이미 생성됏지만 스크롤하여 사라진 viewholder를, 다시 나타날 때 다시 만드는것이 아니라 cache에 저장후 사용하게 해준다.
            viewModel.fetchData()
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


    override fun observeData() = viewModel.postArticleLiveData.observe(this) {
        testAdapter.submitList(it)
    }

    companion object {

        fun newIntent(context: Context) =
            Intent(context, PostArticleActivity::class.java).apply {

            }
    }

}