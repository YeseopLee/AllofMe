package com.example.allofme.widget.adapter.listener.board

import com.example.allofme.model.board.ArticleListModel
import com.example.allofme.widget.adapter.listener.AdapterListener

interface BoardListListener: AdapterListener {

    fun onClickItem(model: ArticleListModel)

}