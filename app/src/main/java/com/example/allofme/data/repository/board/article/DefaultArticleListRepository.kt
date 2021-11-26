package com.example.allofme.data.repository.board.article

import android.net.Uri
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
    private val fireStore: FirebaseFirestore
) : ArticleListRepository {

    override suspend fun getList(
        fieldCategory: FieldCategory
    ): List<ArticleListEntity> = withContext(ioDispatcher) {

        var articleList = ArrayList<ArticleListEntity>()

        fireStore
            .collection("article")
            .get()
            .addOnSuccessListener { snapshot ->

                for (document in snapshot) {
                    val item = ArticleListEntity(
                        id = document.hashCode().toLong(),
                        articleId = document.id,
                        name = document["name"] as String,
                        title = document["title"] as String,
                        year = YearCategory.valueOf(document["year"].toString()),
                        field = FieldCategory.valueOf(document["field"].toString()),
                        createdAt = document["createdAt"] as Long,
                        profileImageUrl = document["profileImageUri"] as String?
                    )
                    articleList.add(item)
                }
            }
            .await()

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
