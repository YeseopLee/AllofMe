package com.example.allofme.widget.adapter.listener.board.articlelist

import com.example.allofme.model.board.post.PostArticleModel
import com.example.allofme.widget.adapter.listener.AdapterListener

interface PostArticleListener: AdapterListener {

    fun onClickItem(model: PostArticleModel)

}