package com.example.allofme.widget.adapter.listener.board.postArticle

import com.example.allofme.model.board.postArticle.PostArticleModel
import com.example.allofme.widget.adapter.listener.AdapterListener

interface TempImageListener: AdapterListener {

    fun onClickItem(model: PostArticleModel)

    fun removeItem(model: PostArticleModel)

}