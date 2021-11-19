package com.example.allofme.data.repository.board.article

import com.example.allofme.data.entity.ArticleListEntity
import com.example.allofme.screen.board.articlelist.FieldCategory

interface ArticleListRepository {

    suspend fun getList(
        fieldCategory: FieldCategory
    ): List<ArticleListEntity>

}