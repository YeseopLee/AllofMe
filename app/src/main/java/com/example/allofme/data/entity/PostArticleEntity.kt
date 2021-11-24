package com.example.allofme.data.entity

import android.net.Uri
import android.os.Parcelable
import com.example.allofme.model.CellType
import com.example.allofme.model.Model
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostArticleEntity (
    val id: Long,
    val type: CellType = CellType.ARTICLE_EDIT_CELL,
    var text: String? = null,
    val url: String? = null
):Parcelable



