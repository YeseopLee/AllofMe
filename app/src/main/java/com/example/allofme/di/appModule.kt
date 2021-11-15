package com.example.allofme.di

import com.example.allofme.data.repository.board.article.ArticleListRepository
import com.example.allofme.data.repository.board.article.DefaultArticleListRepository
import com.example.allofme.screen.board.BoardViewModel
import com.example.allofme.screen.board.article.ArticleListViewModel
import com.example.allofme.screen.board.article.FieldCategory
import com.example.allofme.screen.main.MainViewModel
import com.example.allofme.screen.provider.DefaultResourcesProvider
import com.example.allofme.screen.provider.ResourcesProvider
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel

import org.koin.dsl.module

val appModule = module {

    viewModel { MainViewModel() }
    viewModel { BoardViewModel() }
    viewModel { (fieldCategory: FieldCategory) -> ArticleListViewModel(get(), fieldCategory) }

    //Repositories
    single<ArticleListRepository> { DefaultArticleListRepository(get(), get()) }

    single { Dispatchers.IO }
    single { Dispatchers.Main }

    //ResourcesProvider
    single<ResourcesProvider> { DefaultResourcesProvider(androidApplication()) }
}