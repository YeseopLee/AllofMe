package com.example.allofme.model.board.postArticle

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.example.allofme.model.CellType
import com.example.allofme.model.Model
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostArticleModel (
    override val id: Long,
    override val type: CellType = CellType.ARTICLE_EDIT_CELL,
    var order: Int = 0,
    var text: String? = null,
    val url: Uri? = null
): Model(id, type), Parcelable {

    companion object {

        val DIFF_CALLBACK: DiffUtil.ItemCallback<PostArticleModel> = object : DiffUtil.ItemCallback<PostArticleModel>() {
            override fun areItemsTheSame(oldItem: PostArticleModel, newItem: PostArticleModel): Boolean {
                return oldItem.id == newItem.id && oldItem.type == newItem.type
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: PostArticleModel, newItem: PostArticleModel): Boolean {
                return oldItem === newItem
            }
        }

    }

}