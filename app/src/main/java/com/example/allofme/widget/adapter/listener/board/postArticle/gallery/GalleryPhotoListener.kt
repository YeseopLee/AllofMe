package com.example.allofme.widget.adapter.listener.board.postArticle.gallery

import com.example.allofme.model.board.postArticle.gallery.GalleryPhotoModel
import com.example.allofme.widget.adapter.listener.AdapterListener

interface GalleryPhotoListener: AdapterListener {

    fun onClickItem(model: GalleryPhotoModel)

}