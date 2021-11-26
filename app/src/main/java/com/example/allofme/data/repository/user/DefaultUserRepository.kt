package com.example.allofme.data.repository.user

import android.content.Context
import android.util.Log
import com.example.allofme.data.entity.UserEntity
import com.example.allofme.screen.board.articlelist.FieldCategory
import com.example.allofme.screen.board.articlelist.YearCategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class DefaultUserRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val fireStore: FirebaseFirestore
): UserRepository {

    override suspend fun getUserInfo(userId: String): UserEntity = withContext(ioDispatcher) {

        lateinit var entity: UserEntity

        fireStore
            .collection("user")
            .document(userId)
            .get()
            .addOnSuccessListener { documents ->
                entity = UserEntity(
                    displayName = documents.get("displayName") as String?,
                    field = documents.get("field") as String?,
                    year = documents.get("year") as String?
                )
            }
            .addOnFailureListener{
                entity = UserEntity(
                    displayName = "Error",
                    field = "Error",
                    year = "Error"
                )
            }
            .await()

        return@withContext entity

    }

    override suspend fun setField(uid: String, name: String, field: String) {
        val userInfo = hashMapOf(
            "displayName" to name,
            "field" to field
        )

        fireStore
            .collection("user")
            .document(uid)
            .set(userInfo, SetOptions.merge())
    }

    override suspend fun setYear(uid: String, name: String, year: String?) {
        val userInfo = hashMapOf(
            "displayName" to name,
            "year" to year
        )

        fireStore
            .collection("user")
            .document(uid)
            .set(userInfo, SetOptions.merge())
    }

    override suspend fun setUserInfo(uid: String, name: String, field: String, year: String): Unit = withContext(ioDispatcher) {
        val userInfo = hashMapOf(
            "displayName" to name,
            "field" to field,
            "year" to year
        )

        fireStore
            .collection("user")
            .document(uid)
            .set(userInfo, SetOptions.merge())
    }


}
