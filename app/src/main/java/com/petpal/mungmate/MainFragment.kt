package com.petpal.mungmate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.petpal.mungmate.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private lateinit var fragmentMainBinding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentMainBinding = FragmentMainBinding.inflate(layoutInflater)

        // navigation
        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragmentMainContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        fragmentMainBinding.run {
            bottomNavigation.run {
                setupWithNavController(navController)
            }
        }

        return fragmentMainBinding.root
    }

}