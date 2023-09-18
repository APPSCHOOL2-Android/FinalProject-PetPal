package com.petpal.mungmate.ui.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
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
    private lateinit var userViewModel: UserViewModel

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

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        fragmentUserStartBinding.run {
            googleLoginButton.setOnClickListener {
                googleLogIn()
            }

            kakaoLoginButton.setOnClickListener {
                kakaoLogIn()
            }
        }
    }

    private fun googleLogIn() {
        val signInIntent = googleSignInClient.signInIntent
        googleLoginLauncher.launch(signInIntent)

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
            Snackbar.make(requireView(), "로그인에 실패하였습니다. 다시 시도해주세요.", Snackbar.LENGTH_SHORT).show()
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
                    Snackbar.make(requireView(), "로그인에 실패하였습니다. 다시 시도해주세요.", Snackbar.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        Log.d("카카오", "ui 업데이트")
        if (user == null) {
            Snackbar.make(requireView(), "로그인에 실패하였습니다. 다시 시도해주세요.", Snackbar.LENGTH_SHORT).show()
        } else {
            //snackbar 띄워주고
            Snackbar.make(requireView(), "환영합니다. ${user!!.displayName}", Snackbar.LENGTH_SHORT)
                .show()

            //Viewmodel통해 사용자 데이터 설정
            userViewModel.setUser(user)

            //사용자 정보 입력 화면으로 넘어가기
            findNavController().navigate(R.id.action_userStartFragment_to_userInfoFragment, bundleOf("isRegister" to true))
        }
    }


    private fun kakaoLogIn() {
        // 카카오톡 어플이 있으면 카톡으로 로그인, 없으면 카카오 계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {

            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                Log.d("카카오", "카톡으로 로그인")
                if (error != null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }
                    loginWithKaKaoAccount(requireContext())
                } else if (token != null) {
                    getCustomToken(token.accessToken)
                }
            }
        } else {

            loginWithKaKaoAccount(requireContext())
        }
    }

    // 카카오 계정으로 로그인
    private fun loginWithKaKaoAccount(context: Context) {
        Log.d("카카오", "카카오 계정으로 로그인")
        UserApiClient.instance.loginWithKakaoAccount(context) { token: OAuthToken?, error: Throwable? ->
            if (token != null) {
                getCustomToken(token.accessToken)
            }
        }
    }

    // firebase functions에 배포한 kakaoCustomAuth 호출
    private fun getCustomToken(accessToken: String) {

        Log.d("카카오", "커스텀 토큰 받아오기")
        val functions: FirebaseFunctions = Firebase.functions("asia-northeast3")

        val data = hashMapOf(
            "token" to accessToken
        )

        functions
            .getHttpsCallable("kakaoCustomAuth")
            .call(data)
            .addOnCompleteListener { task ->
                try {
                    // 호출 성공
                    val result = task.result?.data as HashMap<*, *>
                    Log.d("카카오", "호출 성공")
                    var mKey: String? = null
                    for (key in result.keys) {
                        mKey = key.toString()
                    }
                    val customToken = result[mKey!!].toString()

                    // 호출 성공해서 반환받은 커스텀 토큰으로 Firebase Authentication 인증받기
                    firebaseAuthWithKakao(customToken)
                } catch (e: RuntimeExecutionException) {
                    // 호출 실패
                    Log.d("카카오", "호출 실패")
                    Snackbar.make(requireView(), "로그인에 실패하였습니다. 다시 시도해주세요.", Snackbar.LENGTH_SHORT).show()
                    Log.d("카카오", e.message!!)
                }
            }
    }

    private fun firebaseAuthWithKakao(customToken: String) {
        Log.d("카카오", "Firebase authentication 받아오기")
        auth.signInWithCustomToken(customToken).addOnCompleteListener { result ->
            if (result.isSuccessful) {
                updateUI(auth.currentUser)
            } else {
                // 실패 후 로직
                Snackbar.make(requireView(), "로그인에 실패하였습니다. 다시 시도해주세요.", Snackbar.LENGTH_SHORT).show()
                updateUI(null)
            }
        }
    }



    companion object {
        private const val TAG = "UserStartActivity"
    }

}