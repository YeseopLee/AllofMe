package com.example.allofme.widget.adapter.viewholder

import com.example.allofme.databinding.ViewholderArticleListBinding
import com.example.allofme.model.board.ArticleListModel
import com.example.allofme.screen.base.BaseViewModel
import com.example.allofme.screen.provider.ResourcesProvider
import com.example.allofme.util.load
import com.example.allofme.widget.adapter.listener.AdapterListener
import com.example.allofme.widget.adapter.listener.board.BoardListListener

class ArticleListViewHolder(
    private val binding:ViewholderArticleListBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider
): ModelViewHolder<ArticleListModel>(binding, viewModel, resourcesProvider) {


    override fun bindViews(model: ArticleListModel, adapterListener: AdapterListener) = with(binding) {
        if(adapterListener is BoardListListener) {
            root.setOnClickListener {
                adapterListener.onClickItem(model)
            }
        }
    }

    override fun bindData(model: ArticleListModel)  {
        super.bindData(model)
        with(binding) {
            profileImageView.load(model.profileImageUrl.toString())
            nameTextView.text = model.name
            titleTextView.text = model.title
            fieldChip.text = resourcesProvider.getString(model.field.categoryNameId)
            yearChip.text = resourcesProvider.getString(model.year.categoryNameId)
        }
    }

    override fun reset() {

    }

}