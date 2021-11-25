package com.example.allofme.widget.adapter.viewholder

import com.example.allofme.databinding.ViewholderBoardBinding
import com.example.allofme.model.board.ArticleListModel
import com.example.allofme.screen.base.BaseViewModel
import com.example.allofme.screen.provider.ResourcesProvider
import com.example.allofme.widget.adapter.listener.AdapterListener
import com.example.allofme.widget.adapter.listener.board.BoardListListener

class BoardViewHolder(
    private val binding:ViewholderBoardBinding,
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
            //profileImageView.
            nameTextView.text = model.name
            titleTextView.text = model.title
            fieldChip.text = resourcesProvider.getString(model.field.categoryNameId)
            yearChip.text = resourcesProvider.getString(model.year.categoryNameId)
        }
    }

    override fun reset() {

    }

}