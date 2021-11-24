package com.example.allofme.data.entity

import com.example.allofme.model.board.postArticle.PostArticleModel
import com.example.allofme.screen.board.articlelist.FieldCategory
import com.example.allofme.screen.board.articlelist.YearCategory

data class ArticleEntity(
    val userId: String,
    val title: String,
    val createdAt: Long,
    val content: List<PostArticleModel>,
    val year: String?,
    val field: String?
)