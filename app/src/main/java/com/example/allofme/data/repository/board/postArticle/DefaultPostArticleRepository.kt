package com.example.allofme.data.repository.board.postArticle

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.example.allofme.data.entity.ArticleEntity
import com.example.allofme.model.board.postArticle.PostArticleModel
import com.example.allofme.screen.provider.ResourcesProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class DefaultPostArticleRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val storage: FirebaseStorage,
    private val fireStore: FirebaseFirestore
): PostArticleRepository {

    override suspend fun postArticle(userId: String, title: String, name: String, model: List<PostArticleModel>, year:String, field:String, profileImage: Uri) {

        val article = ArticleEntity(userId, title, name, System.currentTimeMillis(), model, year, field, profileImage.toString())

        fireStore
            .collection("article")
            .add(article)
    }

    override suspend fun postStorage(modelList: ArrayList<PostArticleModel>):List<Any> = withContext(ioDispatcher) {
        val tempUriList : ArrayList<String> = arrayListOf()

        modelList.forEach {
            it.url?.let { url -> tempUriList.add(url) }
        }
        val uriList = tempUriList.toList()

        val uploadedDeferred: List<Deferred<Any>> = uriList.mapIndexed { index, uri ->
            async {
                try {
                    val fileName = "image${index}.png"
                    storage.reference.child("article/photo").child(fileName)
                        .putFile(uri.toUri())
                        .await()
                        .storage
                        .downloadUrl
                        .await()
                        .toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Pair(uri, e)
                }
            }

        }
        uploadedDeferred.awaitAll()
    }
}
