package com.example.allofme.screen.my

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.allofme.data.preference.MyPreferenceManager
import com.example.allofme.screen.base.BaseViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyViewModel(
    private val myPreferenceManager: MyPreferenceManager
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

    fun setUserInfo(firebaseUser: FirebaseUser?) = viewModelScope.launch {
        firebaseUser?.let { user ->
            myStateLiveData.value = MyState.Success.Registered(
                userName = user.displayName ?: "???",
                profileImageUri = user.photoUrl
            )
        } ?: kotlin.run {
            myStateLiveData.value = MyState.Success.NotRegistered
        }
    }

    fun logOut() = viewModelScope.launch {
        withContext(Dispatchers.IO){
            myPreferenceManager.removeIdToken()
        }
        fetchData()
    }

}