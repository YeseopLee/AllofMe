package com.example.allofme.widget.adapter.viewholder

import android.text.Editable
import android.text.TextWatcher
import com.example.allofme.databinding.ViewholderDetailArticleBinding
import com.example.allofme.databinding.ViewholderDetailArticleImageBinding
import com.example.allofme.databinding.ViewholderPostArticleBinding
import com.example.allofme.model.board.article.detail.ArticleModel
import com.example.allofme.model.board.postArticle.PostArticleModel
import com.example.allofme.screen.base.BaseViewModel
import com.example.allofme.screen.provider.ResourcesProvider
import com.example.allofme.util.load
import com.example.allofme.widget.adapter.listener.AdapterListener


class DetailArticleImageViewHolder(
    private val binding: ViewholderDetailArticleImageBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider
): ModelViewHolder<PostArticleModel>(binding, viewModel, resourcesProvider) {

    override fun reset() {

    }

    override fun bindViews(model: PostArticleModel, adapterListener: AdapterListener) = with(binding) {

    }

    override fun bindData(model: PostArticleModel) = with(binding) {
        imageView.load(model.url.toString())
        super.bindData(model)

    }

}
