package com.example.allofme.screen.board.articlelist

import androidx.annotation.StringRes
import com.example.allofme.R

enum class YearCategory (

    @StringRes val categoryNameId: Int,
    @StringRes val categoryTypeId: Int

) {

    ALL(R.string.category_all, R.string.category_all_type),
    NEW(R.string.category_new, R.string.category_new_type),
    TWO_THREE(R.string.category_2to3,R.string.category_2to3_type),
    FOUR_FIVE(R.string.category_4to5,R.string.category_4to5_type),
    FIVE(R.string.category_5,R.string.category_5_type)

}

//enum class YearCategory {
//
//    ALL, NEW, TWO_THREE, FOUR_FIVE, OLDER
//
//}