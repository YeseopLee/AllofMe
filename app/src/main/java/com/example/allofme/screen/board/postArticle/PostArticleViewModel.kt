package com.example.allofme.screen.board.postArticle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.allofme.model.CellType
import com.example.allofme.model.board.post.PostArticleModel
import com.example.allofme.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PostArticleViewModel: BaseViewModel() {

    val editTextLiveData = MutableLiveData<List<PostArticleModel>>()

    var editTextCount = 0
    var imageViewCount = 0

    override fun fetchData(): Job = viewModelScope.launch {

        editTextLiveData.value = listOf(
            PostArticleModel(
                id = 0,
                type = CellType.ARTICLE_EDIT_CELL,
                text = "??"
            ),
            PostArticleModel(
                id = 1,
                type = CellType.ARTICLE_IMAGE_CELL,
                text = "???"
            )
        )

    }

}