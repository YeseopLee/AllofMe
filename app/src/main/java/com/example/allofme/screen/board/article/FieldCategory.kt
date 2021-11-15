package com.example.allofme.screen.board.article

import androidx.annotation.StringRes
import com.example.allofme.R

enum class FieldCategory (

    @StringRes val categoryNameId: Int,
    @StringRes val categoryTypeId: Int

    ) {

    ALL(R.string.category_all,R.string.category_all_type),
    ANDROID(R.string.category_android, R.string.category_android_type),
    IOS(R.string.category_ios, R.string.category_ios_type),
    WEB(R.string.category_web, R.string.category_web_type),
    BACKEND(R.string.category_back, R.string.category_back_type)


}