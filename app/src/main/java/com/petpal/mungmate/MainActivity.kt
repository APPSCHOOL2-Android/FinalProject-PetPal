package com.petpal.mungmate

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    lateinit var mainViewModel: MainActivityViewModel

    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        setContentView(R.layout.activity_main)

        //네이티브 앱 키로 카카오 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)

        val keyHash = Utility.getKeyHash(this)
        Log.d("키해시", keyHash)

        mainViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        splashScreen.setKeepOnScreenCondition {
            mainViewModel.postSplashTheme.value
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        val currentUser = auth.currentUser

        if (currentUser != null) {
            //firebase 사용자가 있는 경우
            navigate(R.id.mainFragment)
        } else {
            //로그인 하지 않은 경우
            navigate(R.id.userStartFragment)
        }

    }

    fun navigate(id: Int, arg: Bundle? = null) {
        navController.navigate(id, arg)
    }

}
