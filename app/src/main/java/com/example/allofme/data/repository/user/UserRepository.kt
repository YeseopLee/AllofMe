package com.example.allofme.data.repository.user

import com.example.allofme.data.entity.UserEntity

interface UserRepository {

    suspend fun getUserInfo(userId: String): UserEntity
}