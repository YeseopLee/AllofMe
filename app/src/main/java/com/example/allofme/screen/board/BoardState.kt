package com.example.allofme.screen.board

import androidx.annotation.StringRes
import com.example.allofme.data.entity.UserEntity

sealed class BoardState {

    object Uninitialized: BoardState()

    object Loading: BoardState()

    data class Success(
        val UserInfo: UserEntity
    ): BoardState()

    data class Error(
        @StringRes val messageId: Int
    ): BoardState()

}