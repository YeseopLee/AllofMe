package com.example.allofme.screen.board.postArticle

import android.util.Log
import androidx.databinding.ObservableChar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.allofme.data.entity.PostArticleEntity
import com.example.allofme.model.CellType
import com.example.allofme.model.board.postArticle.PostArticleModel
import com.example.allofme.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PostArticleViewModel() : BaseViewModel() {

    val postArticleStateLiveData = MutableLiveData<PostArticleState>(PostArticleState.Uninitialized)

    var articleDescList: MutableList<PostArticleModel> = arrayListOf()

    var stringList: ArrayList<String> = arrayListOf()

    var viewHolderCount = 1

    override fun fetchData(): Job = viewModelScope.launch {
        postArticleStateLiveData.value = PostArticleState.Loading

        if(articleDescList.isEmpty()) articleDescList.add(PostArticleModel(id=0,type = CellType.ARTICLE_EDIT_CELL))

        postArticleStateLiveData.value = PostArticleState.Success(
            articleDescList.map {
                PostArticleModel(
                    id = it.hashCode().toLong(),
                    type = it.type,
                    order = it.order,
                    text = it.text,
                    url = it.url
                )
            }
        )
    }

    fun updateDescription(model: PostArticleModel) = viewModelScope.launch {
        postArticleStateLiveData.value = PostArticleState.Loading

        var e = 0
        for (i in 0 until articleDescList.size) {
            if(articleDescList[i].type == CellType.ARTICLE_EDIT_CELL) {
                articleDescList[i].text = stringList[e]
                e += 1
            }
        }

        articleDescList.add(model)
        articleDescList.add(PostArticleModel(id = model.id+1, type=CellType.ARTICLE_EDIT_CELL))
        viewHolderCount += 2
        fetchData()
    }


}


