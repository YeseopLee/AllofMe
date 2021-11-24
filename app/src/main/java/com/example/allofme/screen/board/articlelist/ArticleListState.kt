package com.example.allofme.screen.board.articlelist

import androidx.annotation.StringRes
import com.example.allofme.model.board.BoardListModel
import com.example.allofme.model.board.postArticle.gallery.GalleryPhotoModel
import com.example.allofme.screen.board.BoardState

sealed class ArticleListState {

    object Uninitialized: ArticleListState()

    object Loading: ArticleListState()

    data class Success(
        val articleList: List<BoardListModel>
    ): ArticleListState()

    data class Error(
        @StringRes val messageId: Int
    ): ArticleListState()

}