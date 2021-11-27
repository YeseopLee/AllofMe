package com.example.allofme.data.repository.board.postArticle

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.example.allofme.data.entity.ArticleEntity
import com.example.allofme.model.board.postArticle.PostArticleModel
import com.example.allofme.screen.provider.ResourcesProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firestore.v1.WriteResult
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.reflect.Field

class DefaultPostArticleRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val storage: FirebaseStorage,
    private val fireStore: FirebaseFirestore
) : PostArticleRepository {

    override suspend fun postArticle(
        userId: String,
        title: String,
        name: String,
        model: List<PostArticleModel>,
        year: String,
        field: String,
        profileImage: Uri
    ) {

        val article = ArticleEntity(
            userId,
            title,
            name,
            System.currentTimeMillis(),
            model,
            year,
            field,
            profileImage.toString()
        )

        fireStore
            .collection("article")
            .add(article)
    }

    override suspend fun postStorage(modelList: ArrayList<PostArticleModel>): List<Any> =
        withContext(ioDispatcher) {
            val tempUriList: ArrayList<String> = arrayListOf()

            modelList.forEach {
                it.url?.let { url -> tempUriList.add(url) }
            }

            val uploadedDeferred: List<Deferred<Any>> = tempUriList.mapIndexed { index, uri ->
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
            return@withContext uploadedDeferred.awaitAll()
        }

    override suspend fun updateArticle(
        articleId: String,
        userId: String,
        title: String,
        model: List<PostArticleModel>
    ): Unit = withContext(ioDispatcher) {

        val info = hashMapOf(
            "content" to model
        )

        async {
            fireStore
                .collection("article")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.id == articleId) {
                            fireStore
                                .collection("article")
                                .document(articleId)
                                .update("content", FieldValue.delete())
                        }
                    }
                }
        }.await()

        async {
            fireStore
                .collection("article")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.id == articleId) {
                            fireStore
                                .collection("article")
                                .document(articleId)
                                .set(info, SetOptions.merge())
                        }
                    }
                }
        }.await()

    }
}
