package com.example.allofme.screen.board.postArticle

import android.util.Log
import androidx.databinding.ObservableChar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.allofme.model.CellType
import com.example.allofme.model.board.postArticle.PostArticleModel
import com.example.allofme.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PostArticleViewModel : BaseViewModel() {

    val postArticleLiveData = MutableLiveData<List<PostArticleModel>>()
    var _postArticleLiveData = ArrayList<PostArticleModel>()

    var viewHolderCount = 1


    override fun fetchData(): Job = viewModelScope.launch {

        if(_postArticleLiveData.isEmpty()) _postArticleLiveData.add(PostArticleModel(id=0, type=CellType.ARTICLE_EDIT_CELL))

        postArticleLiveData.value = _postArticleLiveData.toList().sortedBy { it.id }

    }

}