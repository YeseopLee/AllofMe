package com.example.allofme.data.repository.board.article.detail

import com.example.allofme.data.entity.ArticleEntity

interface DetailArticleRepository {

    suspend fun getArticle(articleId: String): ArticleEntity
}