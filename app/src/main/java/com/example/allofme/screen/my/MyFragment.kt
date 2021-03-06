package com.example.allofme.screen.my

import android.app.Activity
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.allofme.R
import com.example.allofme.databinding.FragmentMyBinding
import com.example.allofme.screen.base.BaseFragment
import com.example.allofme.screen.board.articlelist.FieldCategory
import com.example.allofme.screen.board.articlelist.YearCategory
import com.example.allofme.screen.provider.ResourcesProvider
import com.example.allofme.util.load
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestoreSettings
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.time.Year
import com.google.firebase.auth.GoogleAuthCredential as GoogleAuthCredential

class MyFragment:BaseFragment<MyViewModel, FragmentMyBinding>() {

    override val viewModel by viewModel<MyViewModel>()

    override fun getViewBinding(): FragmentMyBinding = FragmentMyBinding.inflate(layoutInflater)

    private val fireStore: FirebaseFirestore by inject<FirebaseFirestore>()

    private val resourcesProvider by inject<ResourcesProvider>()

    override fun observeData() = viewModel.myStateLiveData.observe(viewLifecycleOwner) {
        when(it) {
            is MyState.Uninitialized -> initViews()
            is MyState.Loading -> handleLoadingState()
            is MyState.Login -> handleLoginState(it)
            is MyState.Success -> handleSuccessState(it)
            is MyState.Error -> handleErrorState(it)
        }
    }

    private fun handleErrorState(state: MyState.Error) {
        Toast.makeText(requireContext(), state.messageId, Toast.LENGTH_SHORT).show()
    }

    private fun handleSuccessState(state: MyState.Success) = with(binding) {
        progressBar.isGone = true
        when (state) {
            is MyState.Success.Registered -> { // ???????????? ???????????? ?????? (token??? ????????????????????????)
                handleRegisteredState(state)
            }
            is MyState.Success.NotRegistered -> { // ???????????? ??????????????? ??????
                profileGroup.isGone = true
                loginRequiredGroup.isVisible = true
            }
        }
    }

    /*
    * ????????? ????????????, State??? Login??????????????? idToken?????? ????????????.
    * idToken?????? ???????????? ??????????????? api??? ??????, ???????????? ????????????.
    * */

    private fun handleLoginState(state: MyState.Login) = with(binding) {
        progressBar.isVisible = true
        val credential = GoogleAuthProvider.getCredential(state.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    viewModel.setInitUserInfo(user) // MyState.Registered??? user ????????? ????????????.
                } else {
                    firebaseAuth.signOut()
                    viewModel.setInitUserInfo(null)
                }
            }
    }

    private fun handleRegisteredState(state: MyState.Success.Registered) = with(binding) {
        profileGroup.isVisible = true
        loginRequiredGroup.isGone = true
        userNameTextView.text = state.userName
        profileImageView.load(state.profileImageUri.toString())
        myFieldButton.text = state.field
        myYearButton.text = when (state.year) {
            "NEW" -> resourcesProvider.getString(YearCategory.NEW.categoryNameId)
            "TWO_THREE" -> resourcesProvider.getString(YearCategory.TWO_THREE.categoryNameId)
            "FOUR_FIVE" -> resourcesProvider.getString(YearCategory.FOUR_FIVE.categoryNameId)
            "FIVE" -> resourcesProvider.getString(YearCategory.FIVE.categoryNameId)
            else ->  "Field"
        }
    }

    private fun handleLoadingState() = with(binding) {
        progressBar.isVisible = true
        loginRequiredGroup.isGone = true
    }

    override fun initViews() = with(binding) {
        loginButton.setOnClickListener {
            signInGoogle()
        }
        logoutButton.setOnClickListener {
            viewModel.logOut()
        }
        myFieldButton.setOnClickListener {
            saveField()
        }
        myYearButton.setOnClickListener {
            saveYear()
        }
    }

    private fun saveField() {

        val fieldItems = arrayOf(FieldCategory.ANDROID.toString(), FieldCategory.BACKEND.toString(), FieldCategory.IOS.toString(), FieldCategory.WEB.toString())
        var checkItem = 0
        var myField = "ANDROID"

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.category_field_all))
            .setNeutralButton(resources.getString(R.string.alert_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.alert_confirm)) { dialog, _ ->
                myField = fieldItems[checkItem]

                viewModel.setField(firebaseAuth.currentUser!!.uid, firebaseAuth.currentUser!!.displayName!!, myField)
                binding.myFieldButton.text = FieldCategory.valueOf(myField!!).toString()
                dialog.dismiss()
            }
            .setSingleChoiceItems(fieldItems, checkItem) { dialog, which ->
                checkItem = which
            }
            .show()
    }

    private fun saveYear() {
        val yearItems = arrayOf(
            resourcesProvider.getString(YearCategory.NEW.categoryNameId),
            resourcesProvider.getString(YearCategory.TWO_THREE.categoryNameId),
            resourcesProvider.getString(YearCategory.FOUR_FIVE.categoryNameId),
            resourcesProvider.getString(YearCategory.FIVE.categoryNameId)
        )
        var checkItem = 0
        var myYear = "NEW"

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.category_field_all))
            .setNeutralButton(resources.getString(R.string.alert_cancel)) {
                    _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.alert_confirm)) { _, _ ->
                when (checkItem) {
                    // YearCategory??? ???????????? valueOf(const name)?????? ???????????? ?????? Category??? const??? ???????????? ???????????????.
                    0 -> myYear = "NEW"
                    1 -> myYear = "TWO_THREE"
                    2 -> myYear = "FOUR_FIVE"
                    3 -> myYear = "FIVE"
                }

                viewModel.setYear(firebaseAuth.currentUser!!.uid, firebaseAuth.currentUser!!.displayName!!, myYear)

                binding.myYearButton.text = resourcesProvider.getString(YearCategory.valueOf(myYear!!).categoryNameId)
            }
            .setSingleChoiceItems(yearItems, checkItem) { dialog, which ->
                checkItem = which
            }
            .show()
    }

    private val gso: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    private val gsc by lazy { GoogleSignIn.getClient(requireActivity(), gso) }

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                task.getResult(ApiException::class.java)?.let { account ->
                    viewModel.saveToken(account.idToken ?: throw Exception())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun signInGoogle() {
        val signInIntent = gsc.signInIntent
        loginLauncher.launch(signInIntent)
    }

    companion object {

        fun newInstance() = MyFragment()

        const val TAG = "MyFragment"
    }


}