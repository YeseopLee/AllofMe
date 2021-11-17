package com.example.allofme.util.mapper

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.allofme.databinding.ViewholderBoardBinding
import com.example.allofme.model.CellType
import com.example.allofme.model.Model
import com.example.allofme.screen.base.BaseViewModel
import com.example.allofme.screen.provider.ResourcesProvider
import com.example.allofme.widget.adapter.viewholder.BoardViewHolder
import com.example.allofme.widget.adapter.viewholder.ModelViewHolder

object ModelViewHolderMapper {

    @Suppress("UNCHECKED_CAST")
    fun <M: Model> map(
        parent: ViewGroup,
        type: CellType,
        viewModel: BaseViewModel,
        resourcesProvider: ResourcesProvider
    ): ModelViewHolder<M> {
        var inflater = LayoutInflater.from(parent.context)
        val viewHolder = when (type) {
            CellType.ARTICLE_CELL -> BoardViewHolder(
                ViewholderBoardBinding.inflate(inflater, parent, false),
                viewModel,
                resourcesProvider
            )
        }
        return viewHolder as ModelViewHolder<M>
    }
}