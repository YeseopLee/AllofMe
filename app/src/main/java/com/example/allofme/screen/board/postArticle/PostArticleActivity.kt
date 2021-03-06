package com.example.allofme.screen.board.postArticle

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.allofme.R
import com.example.allofme.data.entity.ArticleEntity
import com.example.allofme.databinding.ActivityPostArticleBinding
import com.example.allofme.model.CellType
import com.example.allofme.model.board.postArticle.PostArticleModel
import com.example.allofme.screen.base.BaseActivity
import com.example.allofme.screen.board.articlelist.ArticleListFragment
import com.example.allofme.screen.board.articlelist.detail.DetailActivity
import com.example.allofme.screen.board.postArticle.gallery.GalleryActivity
import com.example.allofme.screen.board.postArticle.gallery.GalleryActivity.Companion.URI_LIST_KEY
import com.example.allofme.screen.main.MainActivity
import com.example.allofme.screen.my.MyState
import com.example.allofme.screen.provider.ResourcesProvider
import com.example.allofme.widget.adapter.ModelRecyclerAdapter
import com.example.allofme.widget.adapter.PostArticleRecyclerAdapter
import com.example.allofme.widget.adapter.listener.board.postArticle.PostArticleListener
import com.example.allofme.widget.adapter.listener.board.postArticle.TempImageListener
import com.example.allofme.widget.adapter.viewholder.TempImageViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel


class PostArticleActivity : BaseActivity<PostArticleViewModel, ActivityPostArticleBinding>() {
    override val viewModel by viewModel<PostArticleViewModel>()

    override fun getViewBinding(): ActivityPostArticleBinding =
        ActivityPostArticleBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val articleId by lazy {
        intent.getStringExtra(DetailActivity.ARTICLE_KEY)
    }

    private var imageUriList : ArrayList<PostArticleModel> = arrayListOf() // GalleyActivity?????? ???????????? imageUriList

    override fun initViews() = with(binding) {

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerView.adapter = descAdapter
        imageListRecyclerView.adapter = tempImageListAdapter

        // articleId??? ???????????? = ??????????????? ????????? ??? ????????? ??????.
        articleId?.let {
            viewModel.getArticle(articleId!!)
        } ?: kotlin.run {
            viewModel.initData()
        }


        addPhotoButton.setOnClickListener {

            checkExternalStoragePermission {
                startGalleryScreen()
            }
        }

    }

    private val descAdapter by lazy {
        PostArticleRecyclerAdapter<PostArticleModel, PostArticleViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : PostArticleListener {
                override fun onClickItem(model: PostArticleModel) {

                }

                override fun onRemoveItem(model: PostArticleModel) {
                    saveStrings()
                    viewModel.removeImage(model)
                }
            }
        )
    }

    private fun saveStrings() {
        val editTextList = descAdapter.currentList.filter { it.text != null }
        viewModel.stringList.clear()
        for (i in editTextList.indices) {
            viewModel.stringList.add(editTextList[i].text!!)
        }
    }

    private val tempImageListAdapter by lazy {
        ModelRecyclerAdapter<PostArticleModel, PostArticleViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object: TempImageListener {
                override fun onClickItem(model: PostArticleModel) {

                    // ????????? ????????? CellType?????? ???????????? ???????????????.
                    val transModel = model.copy(
                        id = model.id,
                        type = CellType.ARTICLE_IMAGE_CELL,
                        url = model.url
                    )

                    // ????????? ???????????? edittext??? string?????? list??? ????????????.
                    val editTextList = descAdapter.currentList.filter { it.text != null }
                    viewModel.stringList.clear()
                    for (i in editTextList.indices) {
                        viewModel.stringList.add(editTextList[i].text!!)
                    }
                    viewModel.updateDescription(transModel)
                    binding.recyclerView.setItemViewCacheSize(viewModel.articleDescList.size)
                }

                override fun removeItem(model: PostArticleModel) {
                    val index = imageUriList.indexOf(model)
                    imageUriList.removeAt(index)
                    notifyData()
                }
            }
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyData() {
        tempImageListAdapter.notifyDataSetChanged()
    }


    override fun observeData()  {

        viewModel.postArticleStateLiveData.observe(this) {
            when (it) {
                is PostArticleState.Uninitialized -> Unit
                is PostArticleState.Loading -> handleLoadingState()
                is PostArticleState.Success -> handleSuccessState(it)
                is PostArticleState.Finish -> handleFinishState()
                is PostArticleState.Updated -> handleUpdatedState()
            }
        }
    }

    private fun handleLoadingState() {
        binding.progressBar.isVisible = true
    }

    private fun handleSuccessState(state: PostArticleState.Success) {
        binding.progressBar.isGone = true
        descAdapter.submitList(state.articleDescList)

        Handler(Looper.getMainLooper()).postDelayed({
            binding.recyclerView.smoothScrollToPosition(descAdapter.itemCount - 1) },300)

        binding.addArticleButton.setOnClickListener {

            val title = binding.titleEditText.text.toString()
            val name = firebaseAuth.currentUser?.displayName.orEmpty()
            val userId = firebaseAuth.currentUser?.uid.orEmpty()
            val profileImage = firebaseAuth.currentUser?.photoUrl!!
            val uploadedImageUriList : ArrayList<PostArticleModel> = arrayListOf() // imageList?????? ????????? ????????? ????????? image???.

            // ????????? ????????? ????????? imageList
            state.articleDescList.forEach {
                if( it.type == CellType.ARTICLE_IMAGE_CELL )
                    uploadedImageUriList.add(it)
            }

            // ??? ????????? image??? ???????????? ?????? image??? firebase storage??? ?????? ??????
            if(uploadedImageUriList.isNotEmpty()) {
                articleId?.let {
                    viewModel.updatedPhotoOnStorage(articleId!!, title, name, uploadedImageUriList, state.articleDescList, userId, profileImage)
                }?: kotlin.run {
                    viewModel.uploadPhotoOnStorage(title, name, uploadedImageUriList, state.articleDescList, userId, profileImage)
                }
            } else {
                articleId?.let {
                    viewModel.updateArticle(articleId!!, userId, title, state.articleDescList)
                }?: kotlin.run {
                    viewModel.uploadArticle(userId, title, name, state.articleDescList, profileImage)
                }
            }
        }

    }

    private fun handleFinishState() {
        binding.progressBar.isGone = true
        finish()
    }

    private fun handleUpdatedState() {
        binding.progressBar.isGone = true
        startActivity(
            MainActivity.newIntent(this)
        )
        finish()
    }

    // ????????? ?????? ??????
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
            .setTitle("????????? ???????????????.")
            .setMessage("????????? ???????????? ?????? ???????????????.")
            .setPositiveButton("??????") { _, _ ->
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

    // GalleryActivity?????? ???????????? ???
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                data?.let {
                    val uriList = it.getParcelableArrayListExtra<PostArticleModel>(URI_LIST_KEY)
                    uriList?.let { list ->
                        imageUriList.addAll(list)
                    }
                    tempImageListAdapter.submitList(imageUriList)
                    binding.imageListRecyclerView.isVisible = true
                } ?: kotlin.run {
                    Toast.makeText(this, "????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        tempImageListAdapter.notifyDataSetChanged()
        super.onResume()
    }


    companion object {

        fun newIntent(context: Context, articleId: String?) =
            Intent(context, PostArticleActivity::class.java).apply {
                putExtra(DetailActivity.ARTICLE_KEY, articleId)
            }

        const val PERMISSION_REQUEST_CODE = 1000
        const val GALLERY_REQUEST_CODE = 1001
    }

}


/*
*  ViewBinding -> DataBinding ???????????????
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
//        //viewModel ??????
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
//            recyclerView.setItemViewCacheSize(vm.viewHolderCount) // ?????? ??????????????? ??????????????? ????????? viewholder???, ?????? ????????? ??? ?????? ??????????????? ????????? cache??? ????????? ???????????? ?????????.
//            vm.fetchData()
//
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

