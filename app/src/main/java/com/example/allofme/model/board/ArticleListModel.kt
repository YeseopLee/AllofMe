package com.example.allofme.model.board

import android.net.Uri
import com.example.allofme.data.entity.ArticleListEntity
import com.example.allofme.model.CellType
import com.example.allofme.model.Model
import com.example.allofme.screen.board.articlelist.FieldCategory
import com.example.allofme.screen.board.articlelist.YearCategory

data class ArticleListModel (
    override val id: Long,
    override val type: CellType = CellType.ARTICLE_CELL,
    val articleId: String,
    val name: String,
    val title: String,
    val year: YearCategory,
    val field: FieldCategory,
    val profileImageUrl: String? = null
) : Model(id, type) {

}