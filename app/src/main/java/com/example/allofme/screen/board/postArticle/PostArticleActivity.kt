package com.example.allofme.screen.board.postArticle

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.allofme.R
import com.example.allofme.databinding.ActivityPostArticleBinding
import com.example.allofme.model.CellType
import com.example.allofme.model.board.postArticle.PostArticleModel
import com.example.allofme.screen.base.BaseActivity
import com.example.allofme.screen.board.postArticle.gallery.GalleryActivity
import com.example.allofme.screen.provider.ResourcesProvider
import com.example.allofme.widget.adapter.ModelRecyclerAdapter
import com.example.allofme.widget.adapter.listener.board.postArticle.PostArticleListener
import com.example.allofme.widget.adapter.listener.board.postArticle.TempImageListener
import com.example.allofme.widget.adapter.viewholder.TempImageViewHolder
import kotlinx.coroutines.Job
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel


class PostArticleActivity : BaseActivity<PostArticleViewModel, ActivityPostArticleBinding>() {
    override val viewModel by viewModel<PostArticleViewModel>()

    override fun getViewBinding(): ActivityPostArticleBinding =
        ActivityPostArticleBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private var imageUriList : ArrayList<PostArticleModel> = arrayListOf() // GalleyActivity에서 받아오는 imageUriList

    override fun initViews() = with(binding) {
        viewModel.fetchData()
        recyclerView.adapter = descAdapter
        imageListRecyclerView.adapter = tempImageListAdapter

        addPhotoButton.setOnClickListener {

            viewModel._postArticleLiveData.add(
                PostArticleModel(
                    id = viewModel.viewHolderCount.toLong(),
                    type = CellType.ARTICLE_IMAGE_CELL
                )
            )
            viewModel._postArticleLiveData.add(
                PostArticleModel(
                    id = viewModel.viewHolderCount.toLong() + 1,
                    type = CellType.ARTICLE_EDIT_CELL,
                )
            )
            viewModel.viewHolderCount += 2
            recyclerView.setItemViewCacheSize(viewModel.viewHolderCount) // 이미 생성됏지만 스크롤하여 사라진 viewholder를, 다시 나타날 때 다시 만드는것이 아니라 cache에 저장후 사용하게 해준다.
            viewModel.fetchData()

            checkExternalStoragePermission {
                startGalleryScreen()
            }

        }


    }


    private val descAdapter by lazy {
        ModelRecyclerAdapter<PostArticleModel, PostArticleViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : PostArticleListener {
                override fun onClickItem(model: PostArticleModel) {
                    //
                }

                override fun onSaveItem(mode: PostArticleModel) {
                    //
                }
            }
        )
    }

    private val tempImageListAdapter by lazy {
        ModelRecyclerAdapter<PostArticleModel, PostArticleViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object: TempImageListener {
                override fun onClickItem(model: PostArticleModel) {
                    //
                }
            }
        )
    }


    override fun observeData()  {
        viewModel.postArticleLiveData.observe(this) {
            descAdapter.submitList(it)
        }
    }

    private fun checkExternalStoragePermission(uploadAction: () -> Unit) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                uploadAction()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                showPermissionContextPopup()
            }
            else -> {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            }
            .create()
            .show()

    }

    private fun startGalleryScreen() {
        startActivityForResult(
            GalleryActivity.newIntent(this),
            GALLERY_REQUEST_CODE
        )
    }

    // GalleryActivity에서 돌아왔을 때
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                data?.let {
                    val uriList = it.getParcelableArrayListExtra<PostArticleModel>("uriList")
                    uriList?.let { list ->
                        imageUriList.addAll(list)
                    }
                    tempImageListAdapter.submitList(imageUriList)
                    binding.imageListRecyclerView.isVisible = true
                    Log.e("???",imageUriList.toString())
                } ?: kotlin.run {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    companion object {

        fun newIntent(context: Context) =
            Intent(context, PostArticleActivity::class.java).apply {

            }

        const val PERMISSION_REQUEST_CODE = 1000
        const val GALLERY_REQUEST_CODE = 1001
    }

}


/*
*  ViewBinding -> DataBinding 더미데이터
* */

//class PostArticleActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityPostArticleBinding
//    private lateinit var vm : PostArticleViewModel
//
//    private lateinit var fetchJob: Job
//
//    private val resourcesProvider by inject<ResourcesProvider>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_article)
//        binding.lifecycleOwner = this
//
//        //viewModel 연결
//        vm = ViewModelProvider(this)[PostArticleViewModel::class.java]
//        binding.viewModel = vm
//
//        initViews()
//        fetchJob = vm.fetchData()
//        observeData()
//
//    }
//
//    private fun initViews() = with(binding)  {
//        vm.fetchData()
//        recyclerView.adapter = adapter
//
//        floatbtn1.setOnClickListener {
//
//            vm._postArticleLiveData.add(
//                PostArticleModel(
//                    id = vm.viewHolderCount.toLong(),
//                    type = CellType.ARTICLE_IMAGE_CELL
//                )
//            )
//            vm._postArticleLiveData.add(
//                PostArticleModel(
//                    id = vm.viewHolderCount.toLong() + 1,
//                    type = CellType.ARTICLE_EDIT_CELL,
//                )
//            )
//            vm.viewHolderCount += 2
//            recyclerView.setItemViewCacheSize(vm.viewHolderCount) // 이미 생성됏지만 스크롤하여 사라진 viewholder를, 다시 나타날 때 다시 만드는것이 아니라 cache에 저장후 사용하게 해준다.
//            vm.fetchData()
//
//            Log.e("testTExt",vm.postArticleLiveData.value?.first()?.text.toString())
////            Log.e("testTExt",vm.postArticleLiveData.value?.get(2)?.text.toString())
////            Log.e("testTExt",vm.postArticleLiveData.value?.get(4)?.text.toString())
//        }
//
//
//    }
//
//    override fun onDestroy() {
//        if (fetchJob.isActive) {
//            fetchJob.cancel()
//        }
//        super.onDestroy()
//    }
//
//
//    private val adapter by lazy {
//        ModelRecyclerAdapter<PostArticleModel, PostArticleViewModel>(
//            listOf(),
//            vm,
//            resourcesProvider,
//            adapterListener = object : PostArticleListener {
//                override fun onClickItem(model: PostArticleModel) {
//                    //
//                }
//
//                override fun onSaveItem(mode: PostArticleModel) {
//                    //
//                }
//            }
//        )
//
//    }
//
//
//    private fun observeData() = with(binding)  {
//        vm.postArticleLiveData.observe(lifecycleOwner!!) {
//            adapter.submitList(it)
//        }
//        vm.tempText.observe(lifecycleOwner!!) {
//            Log.e("observing", it.toString())
//        }
//    }
//
//
//    companion object {
//
//        fun newIntent(context: Context) =
//            Intent(context, PostArticleActivity::class.java).apply {
//
//            }
//    }
//
//}

