package com.example.allofme.screen.board.postArticle

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.databinding.ObservableChar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.allofme.data.entity.ArticleEntity
import com.example.allofme.data.entity.PostArticleEntity
import com.example.allofme.data.repository.board.article.detail.DefaultDetailArticleRepository
import com.example.allofme.data.repository.board.article.detail.DetailArticleRepository
import com.example.allofme.data.repository.board.postArticle.PostArticleRepository
import com.example.allofme.data.repository.user.UserRepository
import com.example.allofme.model.CellType
import com.example.allofme.model.board.postArticle.PostArticleModel
import com.example.allofme.screen.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.koin.android.ext.android.inject

class PostArticleViewModel(
    private val userRepository: UserRepository,
    private val postArticleRepository: PostArticleRepository,
    private val firebaseAuth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val fireStore: FirebaseFirestore
) : BaseViewModel() {

    val postArticleStateLiveData = MutableLiveData<PostArticleState>(PostArticleState.Uninitialized)
    val imageList = MutableLiveData<List<Any>>()

    var articleDescList: MutableList<PostArticleModel> = arrayListOf()
    var stringList: ArrayList<String> = arrayListOf()
    var title: String? = null

    var field: String = "분야"
    var year: String = "경력"

    var viewHolderCount = 1


    init {
        getUser()
    }

    private fun getUser() = viewModelScope.launch {
        field = firebaseAuth.currentUser?.uid?.let { userRepository.getUserInfo(it).field }.toString()
        year = firebaseAuth.currentUser?.uid?.let { userRepository.getUserInfo(it).year }.toString()
    }

    override fun fetchData(): Job = viewModelScope.launch {
        postArticleStateLiveData.value = PostArticleState.Loading

        if(articleDescList.isEmpty()) articleDescList.add(PostArticleModel(id=0, type = CellType.ARTICLE_EDIT_CELL))

        postArticleStateLiveData.value = PostArticleState.Success(
            articleDescList.map {
                PostArticleModel(
                    id = it.id,
                    type = it.type,
                    text = it.text,
                    url = it.url
                )
            }
        )

    }



    fun updateDescription(model: PostArticleModel) = viewModelScope.launch {
        postArticleStateLiveData.value = PostArticleState.Loading

        /*
            데이터가 변경 될 때 view의 editText의 마지막 text가  model에 바로 담아지지 않는현상이 존재함.
            그래서 데이터를 넘기기전에 미리 view에서 editText들의 string 배열을 가져와서 수동으로 직접 삽입하였다.
         */

        var e = 0
        for (i in 0 until articleDescList.size) {
            if(articleDescList[i].type == CellType.ARTICLE_EDIT_CELL) {
                articleDescList[i].text = stringList[e]
                e += 1
            }
        }

        articleDescList.add(model)
        articleDescList.add(PostArticleModel(id = model.id, type=CellType.ARTICLE_EDIT_CELL))
        viewHolderCount += 2

        fetchData()
    }

    fun uploadPhotoOnStorage(title:String, name:String, imageList: ArrayList<PostArticleModel>, model: List<PostArticleModel>, userId:String, profileImage: Uri) = viewModelScope.launch {

        val result = postArticleRepository.postStorage(imageList)

        afterUploadPhoto(result, title, name, model, userId, profileImage)

    }

    private fun afterUploadPhoto(results: List<Any>, title: String, name: String, model: List<PostArticleModel>, userId: String, profileImage: Uri) {
        val errorResults = results.filterIsInstance<Pair<Uri, Exception>>()
        val successResults = results.filterIsInstance<String>()

        // URL을 firestore에 적합한 타입으로 변경한다.
        var e = 0
        model.forEach {
            if(it.type == CellType.ARTICLE_IMAGE_CELL) {
                it.url = successResults[e]
                e += 1
            }
        }

        when {
            errorResults.isNotEmpty() && successResults.isNotEmpty() -> {
                //photoUploadErrorButContinurDialog(errorResults, successResults, title, model, userId)
            }
            errorResults.isNotEmpty() && successResults.isEmpty() -> {
                //uploadError()
            }
            else -> {
                uploadArticle(userId, title, name, model, profileImage)
            }
        }
    }

    fun uploadArticle(userId: String, title: String, name: String, model: List<PostArticleModel>, profileImage: Uri) = viewModelScope.launch {

        postArticleRepository.postArticle(userId, title, name, model, year, field, profileImage)

    }

    fun removeImage(model: PostArticleModel) = viewModelScope.launch {

        var e = 0
        for (i in 0 until articleDescList.size) {
            if(articleDescList[i].type == CellType.ARTICLE_EDIT_CELL) {
                articleDescList[i].text = stringList[e]
                e += 1
            }
        }

        val index = articleDescList.indexOf(model)

        async {
            articleDescList.removeAt(index)
        }.await()


        if(articleDescList[index].text == null) {
            articleDescList.removeAt(index)
        }
        else {
            articleDescList[index-1].text = articleDescList[index-1].text + "\n" + articleDescList[index].text
            articleDescList.removeAt(index)
        }

        fetchData()
    }

}


