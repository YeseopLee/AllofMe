package com.example.allofme.screen.board.articlelist.detail

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
    val isMeLiveData : MutableLiveData<Boolean> = MutableLiveData()

    override fun fetchData(): Job = viewModelScope.launch {

        detailArticleStateLiveData.value = DetailState.Loading

    }

    fun getArticleDetail(articleId: String) = viewModelScope.launch {


        var article = detailArticleRepository.getArticle(articleId)

        // 글 읽기에 적합한 CellType으로 변경하기
        article.content.forEach {
            if (it.type == CellType.ARTICLE_EDIT_CELL) {
                it.type = CellType.ARTICLE_DETAIL
            }
            if (it.type == CellType.ARTICLE_IMAGE_CELL) {
                it.type = CellType.ARTICLE_DETAIL_IMAGE
            }
        }

        detailArticleStateLiveData.value = DetailState.Success(
            article
        )
    }

    fun checkIsMe(articleId: String, userId: String) = viewModelScope.launch {

        isMeLiveData.value = detailArticleRepository.getArticle(articleId).userId == userId
    }

    fun deleteArticle(articleId: String, userId: String) = viewModelScope.launch {

        detailArticleStateLiveData.value = DetailState.Loading

        detailArticleRepository.deleteArticle(articleId, userId)

        detailArticleStateLiveData.value = DetailState.Finish

    }
}