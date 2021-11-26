package com.example.allofme.screen.board

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.allofme.data.repository.user.UserRepository
import com.example.allofme.screen.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BoardViewModel(
    private val userRepository: UserRepository
): BaseViewModel() {

    val boardStateLiveData = MutableLiveData<BoardState>(BoardState.Uninitialized)

    override fun fetchData() = viewModelScope.launch {

    }

    fun userInfoCheck(userId: String) = viewModelScope.launch {
        boardStateLiveData.value = BoardState.Success(
            userRepository.getUserInfo(userId)
        )
    }
}