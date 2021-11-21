package com.example.allofme.model.board.postArticle.gallery

import android.net.Uri
import com.example.allofme.model.CellType
import com.example.allofme.model.Model

data class GalleryPhotoModel(
    override val id: Long,
    override val type: CellType = CellType.GALLERY_PHOTO,
    val uri: Uri,
    val name: String,
    val date: String,
    val size: Int,
    var isSelected: Boolean = false
): Model(id, type)
