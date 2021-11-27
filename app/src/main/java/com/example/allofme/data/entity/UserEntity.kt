package com.example.allofme.data.entity

import android.os.Parcelable
import com.example.allofme.screen.board.articlelist.FieldCategory
import com.example.allofme.screen.board.articlelist.YearCategory
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserEntity (
    val displayName: String?,
    val year: String?,
    val field: String?
): Parcelable