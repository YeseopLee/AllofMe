package com.example.allofme.model.board

import android.net.Uri
import com.example.allofme.data.entity.ArticleListEntity
import com.example.allofme.model.CellType
import com.example.allofme.model.Model
import com.example.allofme.screen.board.article.FieldCategory
import com.example.allofme.screen.board.article.YearCategory

data class BoardListModel (
    override val id: Long,
    override val type: CellType = CellType.ARTICLE_CELL,
    val name: String,
    val title: String,
    val year: YearCategory,
    val field: FieldCategory,
    val profileImageUrl: Uri? = null
) : Model(id, type) {

    fun toEntity() = ArticleListEntity(
        id, name, title, year, field, profileImageUrl
    )

}