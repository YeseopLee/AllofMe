package com.example.allofme.screen.board.articlelist.detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.allofme.data.repository.board.article.detail.DetailArticleRepository
import com.example.allofme.model.CellType
import com.example.allofme.screen.base.BaseViewModel
import kotlinx.coroutines.*

class DetailViewModel(
    private val detailArticleRepository: DetailArticleRepository,
): BaseViewModel() {

    val detailArticleStateLiveData = MutableLiveData<DetailState>(DetailState.Uninitialized)
    val isMeLiveData : MutableLiveData<Boolean> = MutableLiveData(false)

    override fun fetchData(): Job = viewModelScope.launch {

        detailArticleStateLiveData.value = DetailState.Loading

    }

    fun getArticleDetail(articleId: String) = viewModelScope.launch {


        var article = detailArticleRepository.getArticle(articleId)

        if(article == null) detailArticleStateLiveData.value = DetailState.Deleted

        // 글 읽기에 적합한 CellType으로 변경하기
        article?.content?.forEach {
            if (it.type == CellType.ARTICLE_EDIT_CELL) {
                it.type = CellType.ARTICLE_DETAIL
            }
            if (it.type == CellType.ARTICLE_IMAGE_CELL) {
                it.type = CellType.ARTICLE_DETAIL_IMAGE
            }
        }

        detailArticleStateLiveData.value = article?.let {
            DetailState.Success(
                it
            )
        }
    }

    fun checkIsMe(articleId: String, userId: String) = viewModelScope.launch {

        Log.e("whyExist", detailArticleRepository.getArticle(articleId)?.userId.toString())
        isMeLiveData.value = detailArticleRepository.getArticle(articleId)?.userId == userId
        Log.e("value",isMeLiveData.value.toString())
    }

    fun deleteArticle(articleId: String, userId: String) = viewModelScope.launch {

        detailArticleStateLiveData.value = DetailState.Loading

        detailArticleRepository.deleteArticle(articleId, userId)

        detailArticleStateLiveData.value = DetailState.Finish

    }
}