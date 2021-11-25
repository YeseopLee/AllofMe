package com.example.allofme.model.board.article.detail

import com.example.allofme.data.entity.ArticleEntity
import com.example.allofme.model.CellType
import com.example.allofme.model.Model
import com.example.allofme.model.board.postArticle.PostArticleModel

data class ArticleModel (
    override val id: Long,
    override val type: CellType = CellType.ARTICLE_DETAIL,
    val userId: String,
    val title: String,
    val createdAt: Long,
    val content: List<PostArticleModel>,
    val year: String?,
    val field: String?
): Model(id, type) {


}