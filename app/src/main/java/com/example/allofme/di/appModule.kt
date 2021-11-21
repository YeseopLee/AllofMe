package com.example.allofme.di

import com.example.allofme.data.preference.MyPreferenceManager
import com.example.allofme.data.repository.board.article.ArticleListRepository
import com.example.allofme.data.repository.board.article.DefaultArticleListRepository
import com.example.allofme.data.repository.board.postArticle.gallery.DefaultGalleryPhotoRepository
import com.example.allofme.data.repository.board.postArticle.gallery.GalleryPhotoRepository
import com.example.allofme.screen.board.BoardViewModel
import com.example.allofme.screen.board.articlelist.ArticleListViewModel
import com.example.allofme.screen.board.articlelist.FieldCategory
import com.example.allofme.screen.board.postArticle.PostArticleViewModel
import com.example.allofme.screen.board.postArticle.gallery.GalleryViewModel
import com.example.allofme.screen.main.MainViewModel
import com.example.allofme.screen.my.MyViewModel
import com.example.allofme.screen.provider.DefaultResourcesProvider
import com.example.allofme.screen.provider.ResourcesProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel

import org.koin.dsl.module

val appModule = module {

    viewModel { MainViewModel() }
    viewModel { BoardViewModel() }
    viewModel { (fieldCategory: FieldCategory) -> ArticleListViewModel(get(), fieldCategory) }
    viewModel { MyViewModel(get())}
    viewModel { PostArticleViewModel() }
    viewModel { GalleryViewModel(get()) }

    //Repositories
    single<ArticleListRepository> { DefaultArticleListRepository(get()) }
    single<GalleryPhotoRepository> { DefaultGalleryPhotoRepository(get(), get())}

    single { Dispatchers.IO }
    single { Dispatchers.Main }

    //ResourcesProvider
    single<ResourcesProvider> { DefaultResourcesProvider(androidApplication()) }

    //SharedPreference
    single { MyPreferenceManager(androidApplication()) }

    //Firebase
    single { FirebaseAuth.getInstance() }
}