package com.example.allofme.model.board.post

import android.net.Uri
import com.example.allofme.model.CellType
import com.example.allofme.model.Model

data class PostArticleModel (
    override val id: Long,
    override val type: CellType = CellType.ARTICLE_EDIT_CELL,
    var text: String? = null,
    val url: Uri? = null
): Model(id, type)