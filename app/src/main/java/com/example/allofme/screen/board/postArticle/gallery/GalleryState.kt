package com.example.allofme.screen.board.postArticle.gallery

import androidx.annotation.IdRes
import com.example.allofme.model.board.postArticle.gallery.GalleryPhotoModel

sealed class GalleryState {

    object Uninitialized: GalleryState()

    object Loading: GalleryState()

    data class Success(
        val photoList: List<GalleryPhotoModel>
    ): GalleryState()

    data class Confirm(
        val photoList: List<GalleryPhotoModel>
    ): GalleryState()

}
