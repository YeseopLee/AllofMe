package com.example.allofme.screen.board.articlelist.detail

import androidx.annotation.StringRes
import com.example.allofme.data.entity.ArticleEntity
import com.example.allofme.model.board.article.detail.ArticleModel

sealed class DetailState {

    object Uninitialized: DetailState()

    object Loading: DetailState()

    data class Success(
        val article: ArticleEntity
    ): DetailState()

    data class Error(
        @StringRes val messageId: Int
    ): DetailState()

}