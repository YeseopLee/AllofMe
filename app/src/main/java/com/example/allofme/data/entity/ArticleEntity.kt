package com.example.allofme.data.entity

import android.os.Parcelable
import com.example.allofme.model.board.postArticle.PostArticleModel
import com.example.allofme.screen.board.articlelist.FieldCategory
import com.example.allofme.screen.board.articlelist.YearCategory
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleEntity(
    val userId: String,
    val title: String,
    val name: String,
    val createdAt: Long,
    val content: List<PostArticleModel>,
    val year: String?,
    val field: String?
) : Parcelable