package com.example.allofme.screen.board.articlelist

import androidx.annotation.StringRes
import com.example.allofme.R

enum class YearCategory (

    @StringRes val categoryNameId: Int

) {

    ALL(R.string.category_all),
    NEW(R.string.category_new),
    TWO_THREE(R.string.category_2to3),
    FOUR_FIVE(R.string.category_4to5),
    FIVE(R.string.category_5)

}