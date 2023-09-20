package com.petpal.mungmate

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.common.KakaoSdk
import com.petpal.mungmate.databinding.ActivityMainBinding
import com.petpal.mungmate.ui.user.UserViewModel
import com.petpal.mungmate.utils.isUserDataExists
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    lateinit var mainViewModel: MainActivityViewModel
    lateinit var userViewModel: UserViewModel
    private lateinit var _activityMainBinding: ActivityMainBinding
    private val activityMainBinding get() = _activityMainBinding

    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        //네이티브 앱 키로 카카오 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        mainViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        splashScreen.setKeepOnScreenCondition {
            mainViewModel.postSplashTheme.value
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        val currentUser = auth.currentUser

        if (currentUser != null) {
            GlobalScope.launch(Dispatchers.IO) {
                val isUserDataExists = auth.isUserDataExists()

                withContext(Dispatchers.Main) {
                    userViewModel.setUser(currentUser)
                    if (isUserDataExists) {

                        Log.d(TAG, "사용자 정보 있음")
                        //firebase 사용자가 있는 경우
                        Snackbar.make(
                            activityMainBinding.root,
                            "환영합니다. ${currentUser.displayName}",
                            Snackbar.LENGTH_SHORT
                        ).show()

                        //mainFragment로 이동하기 전, userStartFragment까지 다 삭제하기
                        navController.navigate(
                            R.id.mainFragment,
                            null,
                            navOptions {
                                popUpTo(R.id.userStartFragment) { inclusive = true }
                            }
                        )

                    } else {

                        Log.d(TAG, "사용자 정보 없음")
                        //로그인은 했지만 정보가 없는 경우
                        navController.navigate(
                            R.id.userInfoFragment,
                            bundleOf("isRegister" to true),
                            navOptions {
                                popUpTo(R.id.userStartFragment) { inclusive = true }
                            }
                        )
                    }
                }
            }

        } else {
            //로그인 하지 않은 경우
            navigate(R.id.userStartFragment)
        }

    }

    fun navigate(id: Int, arg: Bundle? = null, navOptions: NavOptions? = null) {
        navController.navigate(id, arg, navOptions)
    }

    fun popBackStack(fragment: Int, b: Boolean) {
        navController.popBackStack(fragment, b)
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
