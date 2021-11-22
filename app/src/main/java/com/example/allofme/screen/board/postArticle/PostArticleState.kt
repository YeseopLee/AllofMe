package com.example.allofme.screen.board.postArticle

import com.example.allofme.model.board.postArticle.PostArticleModel

sealed class PostArticleState {

    object Uninitialized: PostArticleState()

    object Loading: PostArticleState()

    data class Success(
        val articleDescList: List<PostArticleModel>
    ): PostArticleState()

}