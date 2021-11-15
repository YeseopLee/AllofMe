package com.example.allofme.widget.adapter.viewholder

import com.example.allofme.R
import com.example.allofme.databinding.ViewholderBoardBinding
import com.example.allofme.model.board.BoardListModel
import com.example.allofme.screen.base.BaseViewModel
import com.example.allofme.screen.provider.ResourcesProvider
import com.example.allofme.widget.adapter.listener.AdapterListener
import com.example.allofme.widget.adapter.listener.board.BoardListListener

class BoardViewHolder(
    private val binding:ViewholderBoardBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider
): ModelViewHolder<BoardListModel>(binding, viewModel, resourcesProvider) {

    override fun reset() = with(binding) {

    }

    override fun bindViews(model: BoardListModel, adapterListener: AdapterListener) = with(binding) {
        if(adapterListener is BoardListListener) {
            root.setOnClickListener {

            }
        }
    }

    override fun bindData(model: BoardListModel)  {
        super.bindData(model)
        with(binding) {
            //profileImageView.
            nameTextView.text = model.name
            titleTextView.text = model.title
            fieldChip.text = resourcesProvider.getString(model.field.categoryNameId)
            yearChip.text = resourcesProvider.getString(model.year.categoryNameId)
        }
    }

}