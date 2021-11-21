package com.example.allofme.data.repository.board.postArticle.gallery

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.example.allofme.model.board.postArticle.gallery.GalleryPhotoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface GalleryPhotoRepository {

    suspend fun getAllPhotos(): MutableList<GalleryPhotoModel>

}
