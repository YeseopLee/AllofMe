package com.example.allofme.widget.adapter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.allofme.model.CellType
import com.example.allofme.model.Model
import com.example.allofme.model.board.postArticle.PostArticleModel
import com.example.allofme.screen.base.BaseViewModel
import com.example.allofme.screen.board.postArticle.PostArticleViewModel
import com.example.allofme.screen.provider.ResourcesProvider
import com.example.allofme.util.mapper.ModelViewHolderMapper
import com.example.allofme.widget.adapter.listener.AdapterListener
import com.example.allofme.widget.adapter.viewholder.ModelViewHolder

class PostArticleRecyclerAdapter<M: PostArticleModel, VM: BaseViewModel>(
    private var modelList: List<PostArticleModel>,
    private val viewModel: VM,
    private val resourcesProvider: ResourcesProvider,
    private val adapterListener: AdapterListener

): ListAdapter<PostArticleModel, ModelViewHolder<M>>(PostArticleModel.DIFF_CALLBACK) {

    override fun getItemCount(): Int = modelList.size

    override fun getItemViewType(position: Int): Int = modelList[position].type.ordinal


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder<M> {
        return ModelViewHolderMapper.map(parent, CellType.values()[viewType], viewModel, resourcesProvider)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: ModelViewHolder<M>, position: Int) {
        with(holder) {
            bindData(modelList[position] as M)
            bindViews(modelList[position] as M, adapterListener)
        }
    }

    override fun submitList(list: List<PostArticleModel>?) {
        list?.let { modelList = it }
        super.submitList(list)
    }


}