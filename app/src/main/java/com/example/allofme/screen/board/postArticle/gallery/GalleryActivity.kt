package com.example.allofme.screen.board.postArticle.gallery

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.allofme.databinding.ActivityGalleryBinding
import com.example.allofme.model.CellType
import com.example.allofme.model.board.postArticle.PostArticleModel
import com.example.allofme.model.board.postArticle.gallery.GalleryPhotoModel
import com.example.allofme.screen.base.BaseActivity
import com.example.allofme.screen.provider.ResourcesProvider
import com.example.allofme.widget.adapter.ModelRecyclerAdapter
import com.example.allofme.widget.adapter.listener.board.postArticle.gallery.GalleryPhotoListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class GalleryActivity : BaseActivity<GalleryViewModel, ActivityGalleryBinding>() {

    override val viewModel by viewModel<GalleryViewModel>()

    override fun getViewBinding(): ActivityGalleryBinding = ActivityGalleryBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    override fun initViews() = with(binding) {
        recyclerView.adapter = adapter

        confirmButton.setOnClickListener {
            viewModel.confirmCheckedPhotos()
        }
    }

    private val adapter by lazy {
        ModelRecyclerAdapter<GalleryPhotoModel, GalleryViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : GalleryPhotoListener {
                override fun onClickItem(model: GalleryPhotoModel) {
                    viewModel.selectPhoto(model)
                }

            }
        )
    }

    override fun observeData() = viewModel.galleryStateLiveData.observe(this) {
        when (it) {
            is GalleryState.Uninitialized -> Unit
            is GalleryState.Loading -> handleLoading()
            is GalleryState.Success -> handleSuccess(it)
            is GalleryState.Confirm -> handleConfirm(it)
        }
    }

    private fun handleLoading() = with(binding) {
        progressBar.isVisible = true
        recyclerView.isGone = true
    }

    private fun handleSuccess(state: GalleryState.Success) = with(binding) {
        progressBar.isGone = true
        recyclerView.isVisible = true
        adapter.submitList(state.photoList)
        adapter.notifyDataSetChanged()
    }


    // URI_LIST_KEY에 갤러리에서 선택한 사진들을 저장하여 전송
    private fun handleConfirm(state: GalleryState.Confirm) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(URI_LIST_KEY, ArrayList(state.photoList.map {
                PostArticleModel(
                    id=it.hashCode().toLong(),
                    type=CellType.ARTICLE_TEMP_IMAGE_CELL,
                    url = it.uri.toString()) }
                )
            )
        })
        finish()
    }


    companion object {
        fun newIntent(activity: Activity) = Intent(activity, GalleryActivity::class.java)

        const val URI_LIST_KEY = "uriList"
    }
}