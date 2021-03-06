package com.example.allofme.widget.adapter.viewholder

import com.example.allofme.databinding.ViewholderPostArticleImageBinding
import com.example.allofme.model.board.postArticle.PostArticleModel
import com.example.allofme.screen.base.BaseViewModel
import com.example.allofme.screen.provider.ResourcesProvider
import com.example.allofme.util.load
import com.example.allofme.widget.adapter.listener.AdapterListener
import com.example.allofme.widget.adapter.listener.board.postArticle.PostArticleListener

class PostImageViewHolder(
    private val binding: ViewholderPostArticleImageBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider
): ModelViewHolder<PostArticleModel>(binding, viewModel, resourcesProvider) {

    override fun reset() {
//
    }

    override fun bindViews(model: PostArticleModel, adapterListener: AdapterListener) = with(binding) {
        if(adapterListener is PostArticleListener) {
            closeButton.setOnClickListener {
                adapterListener.onRemoveItem(model)
            }
            imageView.setOnClickListener {
                adapterListener.onClickItem(model)
            }
        }
    }

    override fun bindData(model: PostArticleModel) = with(binding) {
        imageView.load(model.url.toString())
        super.bindData(model)
    }

}