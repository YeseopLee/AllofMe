package com.example.allofme.data.entity

import com.example.allofme.model.board.postArticle.PostArticleModel

data class ArticleEntity(
    val userId: String,
    val title: String,
    val createdAt: Long = 0,
    val content: List<PostArticleModel>
)