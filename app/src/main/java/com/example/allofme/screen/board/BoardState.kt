package com.example.allofme.screen.board

import androidx.annotation.StringRes

sealed class BoardState {

    object Uninitialized: BoardState()

    object Loading: BoardState()

    object Success: BoardState()

    data class Error(
        @StringRes val messageId: Int
    ): BoardState()

}