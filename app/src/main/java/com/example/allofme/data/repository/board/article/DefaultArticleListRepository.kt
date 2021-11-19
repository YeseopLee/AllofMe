package com.example.allofme.data.repository.board.article

import com.example.allofme.data.entity.ArticleListEntity
import com.example.allofme.screen.board.articlelist.FieldCategory
import com.example.allofme.screen.board.articlelist.YearCategory
import com.example.allofme.screen.provider.ResourcesProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultArticleListRepository(
    private val resourcesProvider: ResourcesProvider,
    private val ioDispatcher: CoroutineDispatcher
) : ArticleListRepository {

    private val mockArticleList = listOf<ArticleListEntity>(
        ArticleListEntity(
            id = 0,
            name = "이예섭0",
            title = "자기소개서0",
            year = YearCategory.NEW,
            field = FieldCategory.ANDROID,
            profileImageUrl = null
        ),
        ArticleListEntity(
            id = 1,
            name = "이예섭1",
            title = "자기소개서1",
            year = YearCategory.NEW,
            field = FieldCategory.IOS,
            profileImageUrl = null
        ),        ArticleListEntity(
            id = 2,
            name = "이예섭2",
            title = "자기소개서2",
            year = YearCategory.NEW,
            field = FieldCategory.BACKEND,
            profileImageUrl = null
        ),        ArticleListEntity(
            id = 3,
            name = "이예섭3",
            title = "자기소개서3",
            year = YearCategory.TWO_THREE,
            field = FieldCategory.ANDROID,
            profileImageUrl = null
        ),        ArticleListEntity(
            id = 4,
            name = "이예섭4",
            title = "자기소개서4",
            year = YearCategory.FIVE,
            field = FieldCategory.ANDROID,
            profileImageUrl = null
        ),        ArticleListEntity(
            id = 5,
            name = "이예섭5",
            title = "자기소개서5",
            year = YearCategory.FOUR_FIVE,
            field = FieldCategory.IOS,
            profileImageUrl = null
        ),        ArticleListEntity(
            id = 6,
            name = "이예섭6",
            title = "자기소개서6",
            year = YearCategory.NEW,
            field = FieldCategory.WEB,
            profileImageUrl = null
        ),        ArticleListEntity(
            id = 7,
            name = "이예섭7",
            title = "자기소개서7",
            year = YearCategory.FOUR_FIVE,
            field = FieldCategory.WEB,
            profileImageUrl = null
        ),        ArticleListEntity(
            id = 8,
            name = "이예섭8",
            title = "자기소개서8",
            year = YearCategory.NEW,
            field = FieldCategory.ANDROID,
            profileImageUrl = null
        ),        ArticleListEntity(
            id = 9,
            name = "이예섭9",
            title = "자기소개서9",
            year = YearCategory.NEW,
            field = FieldCategory.IOS,
            profileImageUrl = null
        ),        ArticleListEntity(
            id = 10,
            name = "이예섭10",
            title = "자기소개서10",
            year = YearCategory.NEW,
            field = FieldCategory.WEB,
            profileImageUrl = null
        ),        ArticleListEntity(
            id = 11,
            name = "이예섭11",
            title = "자기소개서11",
            year = YearCategory.NEW,
            field = FieldCategory.BACKEND,
            profileImageUrl = null
        )
    )

    override suspend fun getList(
        fieldCategory: FieldCategory
    ): List<ArticleListEntity> = withContext(ioDispatcher) {

        when (fieldCategory) {
            FieldCategory.ALL -> {
                mockArticleList
            }
            else -> {
                mockArticleList.filter {
                    it.field == fieldCategory
                }
            }
        }

    }
}
