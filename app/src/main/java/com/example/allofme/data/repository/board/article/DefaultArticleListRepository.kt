package com.example.allofme.data.repository.board.article

import android.util.Log
import com.example.allofme.data.entity.ArticleListEntity
import com.example.allofme.screen.board.articlelist.FieldCategory
import com.example.allofme.screen.board.articlelist.YearCategory
import com.example.allofme.screen.provider.ResourcesProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DefaultArticleListRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val resourcesProvider: ResourcesProvider,
    private val fireStore: FirebaseFirestore
) : ArticleListRepository {

    override suspend fun getList(
        fieldCategory: FieldCategory
    ): List<ArticleListEntity> = withContext(ioDispatcher) {

        var articleList = ArrayList<ArticleListEntity>()

        var tempData: ArticleListEntity

        fireStore
            .collection("article")
            .get()
            .addOnSuccessListener { snapshot ->

                for (document in snapshot) {
                    if("2~3년차"==document["year"].toString()) Log.e("True","TrueSign")
                    Log.e("documnetId",document.id.toString())
                    val item = ArticleListEntity(
                        id = document.hashCode().toLong(),
                        name = document["name"] as String,
                        title = document["title"] as String,
                        year = YearCategory.valueOf(document["year"].toString()),
                        field = FieldCategory.valueOf(document["field"].toString())
                    )
                    articleList.add(item)
                }
                Log.e("articleList",articleList.toString())
            }
            .await()

        Log.e("finalArticleList",articleList.toString())
        when (fieldCategory) {
            FieldCategory.ALL -> {
                articleList
            }
            else -> {
                articleList.filter {
                    it.field == fieldCategory
                }
            }
        }



    }
}
