package com.petpal.mungmate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    lateinit var mainViewModel: MainActivityViewModel

    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        setContentView(R.layout.activity_main)

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
