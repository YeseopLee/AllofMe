package com.example.allofme.screen.my

import com.example.allofme.databinding.FragmentMyBinding
import com.example.allofme.screen.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel

class MyFragment:BaseFragment<MyViewModel, FragmentMyBinding>() {

    override val viewModel by viewModel<MyViewModel>()

    override fun getViewBinding(): FragmentMyBinding = FragmentMyBinding.inflate(layoutInflater)

    override fun observeData() {
    }


}