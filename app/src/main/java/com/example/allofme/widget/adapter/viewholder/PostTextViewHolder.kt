package com.example.allofme.widget.adapter.viewholder

import com.example.allofme.databinding.ViewholderPostArticleBinding
import com.example.allofme.model.board.post.PostArticleModel
import com.example.allofme.screen.base.BaseViewModel
import com.example.allofme.screen.provider.ResourcesProvider
import com.example.allofme.widget.adapter.listener.AdapterListener

class PostTextViewHolder(
    private val binding: ViewholderPostArticleBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider
): ModelViewHolder<PostArticleModel>(binding, viewModel, resourcesProvider) {

    override fun reset() {

    }

    override fun bindViews(model: PostArticleModel, adapterListener: AdapterListener) = with(binding) {

    }

    override fun bindData(model: PostArticleModel) = with(binding) {
        super.bindData(model)

    }

}
