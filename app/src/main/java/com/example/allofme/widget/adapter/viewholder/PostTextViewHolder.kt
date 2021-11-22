package com.example.allofme.widget.adapter.viewholder

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.example.allofme.databinding.ViewholderPostArticleBinding
import com.example.allofme.model.board.postArticle.PostArticleModel
import com.example.allofme.screen.base.BaseViewModel
import com.example.allofme.screen.provider.ResourcesProvider
import com.example.allofme.widget.adapter.listener.AdapterListener

class PostTextViewHolder(
    private val binding: ViewholderPostArticleBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider
): ModelViewHolder<PostArticleModel>(binding, viewModel, resourcesProvider) {

    override fun reset() {

    }

    override fun bindViews(model: PostArticleModel, adapterListener: AdapterListener) = with(binding) {

    }

    override fun bindData(model: PostArticleModel) = with(binding) {
        binding.descriptionEditText.addTextChangedListener(MyTextWatcher(model))
        descriptionEditText.setText(model.text)
        super.bindData(model)

    }

    inner class MyTextWatcher(var model: PostArticleModel) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            model.text = s.toString()
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }
}
