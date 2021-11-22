package com.example.allofme.widget.adapter.viewholder

import androidx.core.content.ContextCompat
import com.example.allofme.R
import com.example.allofme.databinding.ViewholderGalleryPhotoItemBinding
import com.example.allofme.screen.base.BaseViewModel
import com.example.allofme.model.board.postArticle.gallery.GalleryPhotoModel
import com.example.allofme.screen.provider.ResourcesProvider
import com.example.allofme.util.load
import com.example.allofme.widget.adapter.listener.AdapterListener
import com.example.allofme.widget.adapter.listener.board.postArticle.gallery.GalleryPhotoListener

class GalleryPhotoViewHolder(
    private val binding: ViewholderGalleryPhotoItemBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider
): ModelViewHolder<GalleryPhotoModel>(binding, viewModel, resourcesProvider) {

    override fun reset() {
//
    }

    override fun bindViews(model: GalleryPhotoModel, adapterListener: AdapterListener) = with(binding) {
        if(adapterListener is GalleryPhotoListener) {
            root.setOnClickListener {
                adapterListener.onClickItem(model)
            }
        }
    }

    override fun bindData(model: GalleryPhotoModel) = with(binding) {
        super.bindData(model)
        with(binding) {
            photoImageView.load(model.uri.toString())
            checkButton.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.root.context,
                    if (model.isSelected)
                        R.drawable.ic_check_enabled
                    else
                        R.drawable.ic_check_disabled
                )
            )
        }
    }

}