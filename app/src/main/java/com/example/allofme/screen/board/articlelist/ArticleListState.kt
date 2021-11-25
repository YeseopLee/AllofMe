package com.example.allofme.screen.board.articlelist

import androidx.annotation.StringRes
import com.example.allofme.model.board.ArticleListModel

sealed class ArticleListState {

    object Uninitialized: ArticleListState()

    object Loading: ArticleListState()

    data class Success(
        val articleList: List<ArticleListModel>
    ): ArticleListState()

    data class Error(
        @StringRes val messageId: Int
    ): ArticleListState()

}