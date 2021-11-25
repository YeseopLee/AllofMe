package com.example.allofme.data.repository.board.article.detail

import android.util.Log
import com.example.allofme.data.entity.ArticleEntity
import com.example.allofme.model.CellType
import com.example.allofme.model.board.postArticle.PostArticleModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DefaultDetailArticleRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val fireStore: FirebaseFirestore
): DetailArticleRepository {

    override suspend fun getArticle(articleId: String): ArticleEntity = withContext(ioDispatcher) {

        lateinit var item: ArticleEntity
        lateinit var item2: ArticleEntity
        fireStore
            .collection("article")
            .document(articleId)
            .get()
            .addOnSuccessListener { snapshot ->

                item = ArticleEntity(
                    userId = snapshot.get("userId") as String,
                    name = snapshot.get("name") as String,
                    title = snapshot.get("title") as String,
                    createdAt = snapshot.get("createdAt") as Long,
                    content = (snapshot.get("content") as ArrayList<Map<String, Any>>).map { article ->
                        PostArticleModel(
                            id = article.hashCode().toLong(),
                            type = CellType.valueOf(article["type"].toString()),
                            text = article["text"] as String?,
                            url = article["url"] as String?
                        )
                    },
                    year = snapshot.get("year") as String,
                    field = snapshot.get("field") as String
                )

                item2 = ArticleEntity(
                    userId = snapshot.get("userId") as String,
                    name = snapshot.get("name") as String,
                    title = snapshot.get("title") as String,
                    createdAt = snapshot.get("createdAt") as Long,
                    content = snapshot.get("content") as List<PostArticleModel>,
                    year = snapshot.get("year") as String,
                    field = snapshot.get("field") as String
                )
            }
            .await()

        Log.e("whatIsRealItem?",item.toString())
        Log.e("whatIsRealItem?2",item2.toString())

        return@withContext item
    }
}

//val userId: String,
//val title: String,
//val createdAt: Long,
//val content: List<PostArticleModel>,
//val year: String?,
//val field: String?