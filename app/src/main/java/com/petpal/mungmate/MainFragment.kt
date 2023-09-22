package com.petpal.mungmate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
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

            bottomNavigation.setupWithNavController(navController)
        }

        // -> 방향
        val rightNavOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.from_right) // 화면 진입 애니메이션
            .setExitAnim(R.anim.to_left)   // 화면 나가는 애니메이션
            .setPopEnterAnim(R.anim.from_left) // 백스택에서 화면으로 돌아올 때 애니메이션
            .setPopExitAnim(R.anim.to_right)
            //.setPopUpTo(R.id.item_walk, false)// 백스택에서 화면으로 돌아올 때 애니메이션
            .build()

        // <- 방향
        val leftNavOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.from_left)
            .setExitAnim(R.anim.to_right)
            .setPopEnterAnim(R.anim.from_right)
            .setPopExitAnim(R.anim.to_left)
            //.setPopUpTo(R.id.item_walk, false)
            .build()

        //  setOnItemSelectedListener : 탭을 선택할 때 발생하는 이벤트 리스너
        fragmentMainBinding.bottomNavigation.setOnItemSelectedListener { menuItem ->

            // 현재 탭 ID -> 현재 목적지 ID를 가져오고 현재 목적지를 찾지 못하면 -1
            val currentId = navController.currentDestination?.id ?: -1

            // 다음으로 이동할 탭 ID
            val nextDestinationId = menuItem.itemId

            val options = when {

                // 쇼핑 -> 커뮤니티
                currentId == R.id.item_shop && nextDestinationId == R.id.item_community -> rightNavOptions

                // 쇼핑 -> 산책
                currentId == R.id.item_shop && nextDestinationId == R.id.item_walk -> rightNavOptions

                // 쇼핑 -> 채팅
                currentId == R.id.item_shop && nextDestinationId == R.id.item_chat -> rightNavOptions

                // 쇼핑 -> 마이페이지
                currentId == R.id.item_shop && nextDestinationId == R.id.item_mypage -> rightNavOptions

                // 커뮤니티 -> 쇼핑
                currentId == R.id.item_community && nextDestinationId == R.id.item_shop -> leftNavOptions

                // 커뮤니티 -> 산책
                currentId == R.id.item_community && nextDestinationId == R.id.item_walk -> rightNavOptions

                // 커뮤니티 -> 채팅
                currentId == R.id.item_community && nextDestinationId == R.id.item_chat -> rightNavOptions

                // 커뮤니티 -> 마이페이지
                currentId == R.id.item_community && nextDestinationId == R.id.item_mypage -> rightNavOptions

                // 산책 -> 커뮤니티
                currentId == R.id.item_walk && nextDestinationId == R.id.item_community -> leftNavOptions

                // 산책 -> 쇼핑
                currentId == R.id.item_walk && nextDestinationId == R.id.item_shop -> leftNavOptions

                // 산책 -> 채팅
                currentId == R.id.item_walk && nextDestinationId == R.id.item_chat -> rightNavOptions

                // 산책 -> 마이페이지
                currentId == R.id.item_walk && nextDestinationId == R.id.item_mypage -> rightNavOptions

                // 채팅 -> 쇼핑
                currentId == R.id.item_chat && nextDestinationId == R.id.item_shop -> leftNavOptions

                // 채팅 -> 커뮤니티
                currentId == R.id.item_chat && nextDestinationId == R.id.item_community -> leftNavOptions

                // 채팅 -> 산책
                currentId == R.id.item_chat && nextDestinationId == R.id.item_walk -> leftNavOptions

                // 채팅 -> 마이페이지
                currentId == R.id.item_chat && nextDestinationId == R.id.item_mypage -> rightNavOptions

                // 마이페이지 -> 쇼핑
                currentId == R.id.item_mypage && nextDestinationId == R.id.item_shop -> leftNavOptions

                // 마이페이지 -> 커뮤니티
                currentId == R.id.item_mypage && nextDestinationId == R.id.item_community -> leftNavOptions

                // 마이페이지 -> 산책
                currentId == R.id.item_mypage && nextDestinationId == R.id.item_walk -> leftNavOptions

                // 마이페이지 -> 채팅
                currentId == R.id.item_mypage && nextDestinationId == R.id.item_chat -> leftNavOptions

                else -> null // 다른 경우에는 애니메이션 없음
            }

            // 화면 전환 애니메이션 설정이 있는떄 실행
            options?.let {

                //
                navController.navigate(nextDestinationId, null, it)
                true
            } == true
        }

        return fragmentMainBinding.root

    }
}