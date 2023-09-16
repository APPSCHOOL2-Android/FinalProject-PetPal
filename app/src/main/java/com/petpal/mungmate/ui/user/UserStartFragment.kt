package com.petpal.mungmate.ui.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentUserStartBinding

class UserStartFragment : Fragment() {
    private lateinit var _fragmentUserStartBinding: FragmentUserStartBinding
    private val fragmentUserStartBinding get() = _fragmentUserStartBinding

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var googleLoginLauncher: ActivityResultLauncher<Intent>

    //TODO: db에 정보가 있는 유저의 경우 반려견 정보 입력 생략하고 바로 mainFragment로 넘어가기

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentUserStartBinding = FragmentUserStartBinding.inflate(layoutInflater)

        // [START config_signin]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        // [END config_signin]

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth
        // [END initialize_auth]

        googleLoginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    handleGoogleSignInResult(data)
                }
            }

        return fragmentUserStartBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentUserStartBinding.run {
            googleLoginButton.setOnClickListener {
                googleLogIn()
            }

            kakaoLoginButton.setOnClickListener {
                kakaoLogIn()
            }
        }
    }

    private fun handleGoogleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {

        if (user == null) {
            Snackbar.make(requireView(), "로그인에 실패하였습니다. 다시 시도해주세요.", Snackbar.LENGTH_SHORT).show()
        } else {
            //snackbar 띄워주고
            Snackbar.make(requireView(), "환영합니다. ${user!!.displayName}", Snackbar.LENGTH_SHORT)
                .show()

            //반려견 정보 입력 화면으로 넘어가기
            findNavController().navigate(R.id.action_userStartFragment_to_addPetFragment)
        }
    }


    private fun kakaoLogIn() {
        TODO("Not yet implemented")
    }

    private fun googleLogIn() {
        val signInIntent = googleSignInClient.signInIntent
        googleLoginLauncher.launch(signInIntent)

    }

    companion object {
        private const val TAG = "UserStartActivity"
    }


}