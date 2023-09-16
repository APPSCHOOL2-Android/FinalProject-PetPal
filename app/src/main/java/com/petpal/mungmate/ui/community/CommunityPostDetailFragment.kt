package com.petpal.mungmate.ui.community

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentCommunityPostDetailBinding


class CommunityPostDetailFragment : Fragment() {

    private lateinit var communityPostDetailBinding: FragmentCommunityPostDetailBinding
    var isClicked = false
    private var isFabVisible = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        communityPostDetailBinding = FragmentCommunityPostDetailBinding.inflate(inflater)
        communityPostDetailBinding.run {
            toolbar()
            communityDetailRecyclerView()
            lottie()

            val fadeIn = AlphaAnimation(0f, 1f)
            fadeIn.duration = 500
            val fadeOut = AlphaAnimation(1f, 0f)
            val targetScrollPosition = resources.getDimensionPixelSize(R.dimen.target_scroll_position)
            fadeOut.duration = 500
            communityPostDetailNestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                if (scrollY >= targetScrollPosition && !isFabVisible) {

                    communityPostDetailCommentFab.startAnimation(fadeIn)
                    communityPostDetailCommentFab.visibility = View.VISIBLE
                    isFabVisible = true

                } else if (scrollY < targetScrollPosition && isFabVisible) {

                    communityPostDetailCommentFab.startAnimation(fadeOut)
                    communityPostDetailCommentFab.visibility = View.GONE
                    isFabVisible = false

                }
            }

            communityPostDetailCommentFab.setOnClickListener {
                communityPostDetailNestedScrollView.smoothScrollTo(0,0)
            }
        }

        return communityPostDetailBinding.root
    }

    private fun FragmentCommunityPostDetailBinding.lottie() {

        communityPostDetailFavoriteLottie.scaleX = 2.0f
        communityPostDetailFavoriteLottie.scaleY = 2.0f

        communityPostDetailFavoriteLottie.setOnClickListener {
            isClicked = !isClicked // 클릭할 때마다 변수를 반전시킴
            if (isClicked) {
                communityPostDetailFavoriteLottie.playAnimation()

            } else {
                communityPostDetailFavoriteLottie.cancelAnimation()
                communityPostDetailFavoriteLottie.progress = 0f
            }

        }
    }

    private fun FragmentCommunityPostDetailBinding.toolbar() {
        communityPostDetailToolbar.run {
            inflateMenu(R.menu.community_post_detail_author)
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener {
                it.findNavController()
                    .popBackStack()
            }
            setOnMenuItemClickListener {

                when (it?.itemId) {
                    R.id.item_modify -> {
                        findNavController().navigate(R.id.action_communityPostDetailFragment_to_communityDetailModifyFragment)
                        Snackbar.make(requireView(), "글 수정하기", Snackbar.LENGTH_SHORT).show()
                    }

                    R.id.item_delete -> {
                        Snackbar.make(requireView(), "글 삭제하기", Snackbar.LENGTH_SHORT).show()
                    }

                }
                false
            }

        }
    }

    private fun FragmentCommunityPostDetailBinding.communityDetailRecyclerView() {
        communityPostDetailCommentRecyclerView.run {
            val communityAdapter = CommunityDetailCommentAdapter(requireContext())
            adapter = communityAdapter
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(requireContext())

        }
    }

}