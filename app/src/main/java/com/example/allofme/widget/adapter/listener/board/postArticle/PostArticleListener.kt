package com.example.allofme.widget.adapter.listener.board.postArticle

import com.example.allofme.model.board.postArticle.PostArticleModel
import com.example.allofme.widget.adapter.listener.AdapterListener

interface PostArticleListener: AdapterListener {

    fun onClickItem(model: PostArticleModel)

    fun onSaveItem(mode: PostArticleModel)

}