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

    private var limitOver: Boolean = false

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
                    if(limitOver) { // 체크가5개가 이미 된 경우, 다음 클릭한 model을 isSelected로 분기
                        if(model.isSelected) viewModel.selectPhoto(model)
                        else Toast.makeText(this@GalleryActivity, "사진은 5개까지만 추가할 수 있습니다.",Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.selectPhoto(model)
                    }
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
        checkLimit(state)
        adapter.submitList(state.photoList)
        adapter.notifyDataSetChanged()
    }

    // 5개까지만 체크가능
    private fun checkLimit(state: GalleryState.Success) {
        limitOver = state.photoList.filter { it.isSelected }.size == 5
    }

    private fun handleConfirm(state: GalleryState.Confirm) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(URI_LIST_KEY, ArrayList(state.photoList.map { PostArticleModel(id=it.hashCode().toLong(), type=CellType.ARTICLE_TEMP_IMAGE_CELL, url = it.uri.toString()) }))
        })
        finish()
    }


    companion object {
        fun newIntent(activity: Activity) = Intent(activity, GalleryActivity::class.java)

        const val URI_LIST_KEY = "uriList"
    }
}