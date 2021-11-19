package com.example.allofme.screen.board.postArticle

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.allofme.model.CellType
import com.example.allofme.model.board.post.PostArticleModel
import com.example.allofme.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PostArticleViewModel: BaseViewModel() {

    val postArticleLiveData = MutableLiveData<List<PostArticleModel>>()
    var _postArticleLiveData = ArrayList<PostArticleModel>()

    var viewHolderCount = 0

    override fun fetchData(): Job = viewModelScope.launch {

        postArticleLiveData.value = _postArticleLiveData.toList().sortedBy { it.id }
        // 항상 고정으로 sort해야 하기 떄문에 별도의 count value를 사용하여 정렬하였음.

    }

}