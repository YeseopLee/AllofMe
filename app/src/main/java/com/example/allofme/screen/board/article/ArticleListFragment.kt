package com.example.allofme.screen.board.article

import android.util.Log
import android.widget.Toast
import androidx.core.os.bundleOf
import com.example.allofme.databinding.FragmentArticleListBinding
import com.example.allofme.model.board.BoardListModel
import com.example.allofme.screen.base.BaseFragment
import com.example.allofme.screen.provider.ResourcesProvider
import com.example.allofme.widget.adapter.ModelRecyclerAdapter
import com.example.allofme.widget.adapter.listener.board.BoardListListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ArticleListFragment: BaseFragment<ArticleListViewModel, FragmentArticleListBinding>() {
    val fieldCategory by lazy { arguments?.getSerializable(FIELD_CATEGORY_KEY) as FieldCategory}

    override val viewModel by viewModel<ArticleListViewModel> {
        parametersOf(
            fieldCategory
        )
    }

    override fun getViewBinding(): FragmentArticleListBinding = FragmentArticleListBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val adapter by lazy {
        ModelRecyclerAdapter<BoardListModel, ArticleListViewModel> (
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : BoardListListener {
                override fun onClickItem(model: BoardListModel) {
                    //
                }
            }
        )
    }

    override fun initViews() = with(binding) {
        recyclerView.adapter = adapter

    }

    override fun observeData() = viewModel.articleListLiveData.observe(viewLifecycleOwner) {
        adapter.submitList(it)
    }


    companion object {
        const val FIELD_CATEGORY_KEY = "fieldCategory"

        fun newInstance(
            fieldCategory: FieldCategory
        ): ArticleListFragment {
            return ArticleListFragment().apply {
                arguments = bundleOf(
                    FIELD_CATEGORY_KEY to fieldCategory
                )
            }
        }

    }


}