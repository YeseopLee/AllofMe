package com.example.allofme.screen.board

import androidx.lifecycle.MutableLiveData
import com.example.allofme.screen.base.BaseViewModel

class BoardViewModel: BaseViewModel() {

    val boardStateLiveData = MutableLiveData<BoardState>(BoardState.Uninitialized)

    fun initViewModel() {
        boardStateLiveData.value = BoardState.Success
    }
}