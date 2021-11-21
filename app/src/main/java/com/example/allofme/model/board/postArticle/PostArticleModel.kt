package com.example.allofme.model.board.postArticle

import android.net.Uri
import android.os.Parcelable
import com.example.allofme.model.CellType
import com.example.allofme.model.Model
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostArticleModel (
    override val id: Long,
    override val type: CellType = CellType.ARTICLE_EDIT_CELL,
    var text: String? = null,
    val url: Uri? = null
): Model(id, type), Parcelable