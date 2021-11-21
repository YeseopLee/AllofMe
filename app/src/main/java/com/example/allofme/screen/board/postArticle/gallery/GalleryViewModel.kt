package com.example.allofme.screen.board.postArticle.gallery

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.allofme.MyApp.Companion.appContext
import com.example.allofme.data.repository.board.postArticle.gallery.GalleryPhotoRepository
import com.example.allofme.model.board.postArticle.gallery.GalleryPhotoModel
import com.example.allofme.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GalleryViewModel(
    private val galleryPhotoRepository: GalleryPhotoRepository
): BaseViewModel() {

    private lateinit var photoList: MutableList<GalleryPhotoModel>

    val galleryStateLiveData = MutableLiveData<GalleryState>()

    override fun fetchData(): Job = viewModelScope.launch {
        galleryStateLiveData.value = GalleryState.Loading

        photoList = galleryPhotoRepository.getAllPhotos()

        galleryStateLiveData.value = GalleryState.Success(
            photoList = photoList
        )
    }

    fun selectPhoto(galleryPhoto: GalleryPhotoModel) {
        galleryStateLiveData.value = GalleryState.Loading
        val findGalleryPhoto = photoList.find { it.id == galleryPhoto.id }
        findGalleryPhoto?.let { photo ->
            Log.e("findGalleery", findGalleryPhoto.toString())
            if(photo.size > 5) {
                photoList[photoList.indexOf(photo)] =
                    photo.copy(
                        isSelected = photo.isSelected.not()
                    )
                galleryStateLiveData.value = GalleryState.Success(
                    photoList = photoList
                )
            } else {


            }
        }

    }



    fun confirmCheckedPhotos() {
        galleryStateLiveData.value = GalleryState.Loading

        galleryStateLiveData.value = GalleryState.Confirm(
            photoList = photoList.filter {
                it.isSelected
            }
        )
    }



}