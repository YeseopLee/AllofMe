package com.example.allofme.screen.board.article

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.allofme.data.repository.board.article.ArticleListRepository
import com.example.allofme.model.board.BoardListModel
import com.example.allofme.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ArticleListViewModel(
    private val articleListRepository: ArticleListRepository,
    private val fieldCategory: FieldCategory,
    private var yearCategory: YearCategory = YearCategory.ALL
): BaseViewModel() {

    val articleListLiveData = MutableLiveData<List<BoardListModel>>()

    override fun fetchData(): Job = viewModelScope.launch {
        var articleList = articleListRepository.getList(fieldCategory)

        if(yearCategory != YearCategory.ALL) {
            articleList = articleList.filter {
                it.year == yearCategory
            }
        } else {
            articleList = articleList
        }


        articleListLiveData.value = articleList.map {
            BoardListModel(
                id = it.id,
                name = it.name,
                title = it.title,
                year = it.year,
                field = it.field,
                profileImageUrl = it.profileImageUrl
            )
        }

    }

    fun setYear(year: YearCategory) {
        this.yearCategory = year
        fetchData()
    }

}
