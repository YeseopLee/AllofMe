package com.example.allofme.di

import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val appModule = module {

    single { Dispatchers.IO }
    single { Dispatchers.Main }

}