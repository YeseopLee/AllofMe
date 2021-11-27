package com.example.allofme.data.repository.board.postArticle

import android.net.Uri
import com.example.allofme.model.board.postArticle.PostArticleModel

interface PostArticleRepository {


    suspend fun postArticle(userId: String, title: String, name: String, model: List<PostArticleModel>,year:String, field:String, profileImage: Uri)

    suspend fun postStorage(modelList: ArrayList<PostArticleModel>): List<Any>

    suspend fun updateArticle(articleId: String, userId: String, title: String, model: List<PostArticleModel>)

}