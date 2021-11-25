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
import androidx.core.net.toUri
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
import com.example.allofme.screen.board.postArticle.gallery.GalleryActivity
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
    private val firestore: FirebaseFirestore by inject<FirebaseFirestore>()
    private val storage: FirebaseStorage by inject<FirebaseStorage>()

    private var imageUriList : ArrayList<PostArticleModel> = arrayListOf() // GalleyActivity에서 받아오는 imageUriList

    override fun initViews() = with(binding) {
        viewModel.fetchData()
        recyclerView.adapter = descAdapter
        imageListRecyclerView.adapter = tempImageListAdapter

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

                    // 본문에 적합한 CellType으로 변경해서 보내줘야함.
                    var transModel = model.copy(
                        id = model.id,
                        type = CellType.ARTICLE_IMAGE_CELL,
                        url = model.url
                    )

                    var editTextList = descAdapter.currentList.filter { it.text != null }
                    viewModel.stringList.clear()
                    for (i in editTextList.indices) {
                        viewModel.stringList.add(editTextList[i].text!!)
                    }
                    viewModel.updateDescription(transModel)
                    binding.recyclerView.setItemViewCacheSize(viewModel.articleDescList.size)
                }
            }
        )
    }


    override fun observeData()  {

        viewModel.postArticleStateLiveData.observe(this) {
            when (it) {
                is PostArticleState.Uninitialized -> Unit
                is PostArticleState.Loading -> handleLoadingState()
                is PostArticleState.Success -> handleSuccessState(it)
            }
        }
    }

    private fun handleLoadingState() {

    }

    private fun handleSuccessState(state: PostArticleState.Success) {
        descAdapter.submitList(state.articleDescList)
        binding.recyclerView.smoothScrollToPosition(descAdapter.itemCount - 1)

        binding.addArticleButton.setOnClickListener {

            val title = binding.titleEditText.text.toString()
            val name = firebaseAuth.currentUser?.displayName.orEmpty()

            val userId = firebaseAuth.currentUser?.uid.orEmpty()

            // 글 내용에 image가 들어있는 경우 image를 firebase storage에 우선 저장
            if(imageUriList.isNotEmpty()) {
                lifecycleScope.launch {
                    val results = uploadPhotoOnStorage(imageUriList)
                    afterUploadPhoto(results, title, name, state.articleDescList, userId)
                }
            }
            else {
                uploadArticle(userId, title, name,  state.articleDescList)
            }

        }

    }
    private suspend fun uploadPhotoOnStorage(modelList: ArrayList<PostArticleModel>) = withContext(Dispatchers.IO) {
        val tempUriList : ArrayList<String> = arrayListOf()
        modelList.forEach {
            it.url?.let { url -> tempUriList.add(url) }
        }

        Log.e("uriList", tempUriList.toString())

        val uriList = tempUriList.toList()

        Log.e("uriList2", uriList.toString())
        val uploadedDeferred: List<Deferred<Any>> = uriList.mapIndexed { index, uri ->
            lifecycleScope.async {
                try {
                    val fileName = "image${index}.png"
                    return@async storage.reference.child("article/photo").child(fileName)
                        .putFile(uri.toUri())
                        .await()
                        .storage
                        .downloadUrl
                        .await()
                        .toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@async Pair(uri, e)
                }
            }
        }
        return@withContext uploadedDeferred.awaitAll()
    }

    private fun afterUploadPhoto(results: List<Any>, title: String, name: String, model: List<PostArticleModel>, userId: String) {
        val errorResults = results.filterIsInstance<Pair<Uri, Exception>>()
        val successResults = results.filterIsInstance<String>()

        var e = 0
        model.forEach {
            if(it.type == CellType.ARTICLE_IMAGE_CELL) {
                it.url = successResults[e]
                e += 1
            }
        }

        when {
            errorResults.isNotEmpty() && successResults.isNotEmpty() -> {
                //photoUploadErrorButContinurDialog(errorResults, successResults, title, model, userId)
            }
            errorResults.isNotEmpty() && successResults.isEmpty() -> {
                //uploadError()
            }
            else -> {
                uploadArticle(userId, title, name, model)
            }
        }
    }

    private fun uploadArticle(userId: String, title: String, name: String, model: List<PostArticleModel>) {


        val article = ArticleEntity(userId, title, name, System.currentTimeMillis(), model, viewModel.year, viewModel.field)


        Log.e("uploadArticle?", article.toString())
        firestore
            .collection("article")
            .add(article)

        finish()

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
                } ?: kotlin.run {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        tempImageListAdapter.notifyDataSetChanged()
        super.onResume()
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

