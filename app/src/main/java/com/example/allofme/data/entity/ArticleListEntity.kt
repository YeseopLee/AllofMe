package com.example.allofme.data.entity

import android.net.Uri
import android.os.Parcelable
import com.example.allofme.screen.board.articlelist.FieldCategory
import com.example.allofme.screen.board.articlelist.YearCategory
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleListEntity(
    override val id: Long,
    val articleId: String,
    val name: String,
    val title: String,
    val year: YearCategory,
    val field: FieldCategory,
    val profileImageUrl: Uri? = null,
    val createdAt: Long? = null
): Entity, Parcelable {

}