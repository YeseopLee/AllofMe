package com.example.allofme.screen.my

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.allofme.data.preference.MyPreferenceManager
import com.example.allofme.data.repository.user.UserRepository
import com.example.allofme.screen.base.BaseViewModel
import com.example.allofme.screen.board.articlelist.FieldCategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyViewModel(
    private val myPreferenceManager: MyPreferenceManager,
    private val userRepository: UserRepository
) : BaseViewModel() {

    val myStateLiveData = MutableLiveData<MyState>(MyState.Uninitialized)

    override fun fetchData(): Job = viewModelScope.launch {
        myStateLiveData.value = MyState.Loading
        myPreferenceManager.getIdToken()?.let {
            myStateLiveData.value = MyState.Login(it) // 저장된 토큰이 있으므로 login state로 넘어간다.
        } ?: kotlin.run {
            myStateLiveData.value =
                MyState.Success.NotRegistered // 저장된 토큰이 없으므로 not registered state로 넘어간다.
        }
    }

    // sharedPref idToken 저장
    fun saveToken(idToken: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            myPreferenceManager.putIdToken(idToken)
            fetchData()
        }
    }

    fun setInitUserInfo(firebaseUser: FirebaseUser?) = viewModelScope.launch {

        firebaseUser?.let { user ->
            myStateLiveData.value = MyState.Success.Registered(
                userName = user.displayName ?: "익명",
                profileImageUri = user.photoUrl,
                field = userRepository.getUserInfo(user.uid).field ?: "ANDROID",
                year = userRepository.getUserInfo(user.uid).year ?: "NEW"
            )
            setUserInfo(
                user.uid,
                user.displayName ?: "익명",
                userRepository.getUserInfo(user.uid).field ?: "ANDROID",
                userRepository.getUserInfo(user.uid).year ?: "NEW"
            )
        } ?: kotlin.run {
            myStateLiveData.value = MyState.Success.NotRegistered
        }
    }

    fun setUserInfo(uid: String, name: String, field: String, year: String) = viewModelScope.launch {
        userRepository.setUserInfo(uid, name, field, year)
    }

    fun setField(uid: String, name: String, field: String) = viewModelScope.launch {
        userRepository.setField(uid, name, field)
    }

    fun setYear(uid: String, name: String, year: String) = viewModelScope.launch {
        userRepository.setYear(uid, name, year)
    }

    fun logOut() = viewModelScope.launch {
        withContext(Dispatchers.IO){
            myPreferenceManager.removeIdToken()
            FirebaseAuth.getInstance().signOut()
        }
        fetchData()
    }

}