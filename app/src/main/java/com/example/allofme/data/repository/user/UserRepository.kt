package com.example.allofme.data.repository.user

import android.net.Uri
import com.example.allofme.data.entity.UserEntity
import com.example.allofme.model.board.postArticle.PostArticleModel

interface UserRepository {

    suspend fun getUserInfo(userId: String): UserEntity

    suspend fun setUserInfo(uid: String, name: String, field: String, year: String)

    suspend fun setField(uid: String, name: String, field: String)

    suspend fun setYear(uid: String, name: String, year: String?)

}