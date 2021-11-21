package com.example.allofme.widget.adapter.viewholder

import com.example.allofme.databinding.ViewholderPostArticleImageBinding
import com.example.allofme.databinding.ViewholderPostArticleImagelistBinding
import com.example.allofme.model.board.postArticle.PostArticleModel
import com.example.allofme.screen.base.BaseViewModel
import com.example.allofme.screen.provider.ResourcesProvider
import com.example.allofme.util.load
import com.example.allofme.widget.adapter.listener.AdapterListener

class TempImageViewHolder(
    private val binding: ViewholderPostArticleImagelistBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider
): ModelViewHolder<PostArticleModel>(binding, viewModel, resourcesProvider) {

    override fun reset() {
//
    }

    override fun bindViews(model: PostArticleModel, adapterListener: AdapterListener) = with(binding) {
//
    }

    override fun bindData(model: PostArticleModel) = with(binding) {
        photoImageView.load(model.url.toString())
        super.bindData(model)
    }

}