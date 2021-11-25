package com.example.allofme.screen.board.articlelist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.allofme.data.repository.board.article.ArticleListRepository
import com.example.allofme.model.board.ArticleListModel
import com.example.allofme.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ArticleListViewModel(
    private val articleListRepository: ArticleListRepository,
    private val fieldCategory: FieldCategory,
    private var yearCategory: YearCategory = YearCategory.ALL
): BaseViewModel() {

    var articleListStateLiveData = MutableLiveData<ArticleListState>(ArticleListState.Uninitialized)

    override fun fetchData(): Job = viewModelScope.launch {

        articleListStateLiveData.value = ArticleListState.Loading

        var articleList = articleListRepository.getList(fieldCategory)

        if(yearCategory != YearCategory.ALL) {
            articleList = articleList.filter {
                it.year == yearCategory
            }
        }

        Log.e("???sort", articleList.toString())
        articleList = articleList.sortedByDescending { it.createdAt }
        Log.e("???sort22", articleList.toString())

        articleListStateLiveData.value = ArticleListState.Success(
            articleList.map {
                ArticleListModel(
                    articleId = it.articleId,
                    id = it.id,
                    name = it.name,
                    title = it.title,
                    year = it.year,
                    field = it.field,
                    profileImageUrl = it.profileImageUrl.toString()
                )
            }
        )
    }

    fun setYear(year: YearCategory) {
        this.yearCategory = year
        fetchData()
    }

}
