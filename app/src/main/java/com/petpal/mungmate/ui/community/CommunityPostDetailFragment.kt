package com.petpal.mungmate.ui.community

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentCommunityPostDetailBinding
import com.petpal.mungmate.model.Post


class CommunityPostDetailFragment : Fragment() {

    private lateinit var communityPostDetailBinding: FragmentCommunityPostDetailBinding
    var isClicked = false
    private var isFabVisible = true
    lateinit var postGetId: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        communityPostDetailBinding = FragmentCommunityPostDetailBinding.inflate(inflater)
        communityPostDetailBinding.run {
            toolbar()
            communityDetailRecyclerView()
            lottie()
            val args: CommunityPostDetailFragmentArgs by navArgs()
            val postid = args.position
            postGetId = postid
//            Log.d("확인", postid.toString())

            val fadeIn = AlphaAnimation(0f, 1f)
            fadeIn.duration = 500
            val fadeOut = AlphaAnimation(1f, 0f)
            val targetScrollPosition =
                resources.getDimensionPixelSize(R.dimen.target_scroll_position)

            fadeOut.duration = 500
            communityPostDetailNestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                Log.d("scrollY",scrollY.toString())

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
                communityPostDetailNestedScrollView.smoothScrollTo(0, 0)
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
                        val db = FirebaseFirestore.getInstance()
                        val postRef = db.collection("Post")
                        Log.d("여기 Id",postGetId)
                        postGetId?.let { id->
                            postRef.document(id)
                                .delete()
                                .addOnFailureListener {
                                    Snackbar.make(rootView, "데이터를 삭제 하는데 실패했습니다.", Snackbar.LENGTH_SHORT).show()
                                }
                        }
                        Snackbar.make(requireView(), "게시글이 삭제 되었습니다.", Snackbar.LENGTH_SHORT).show()
                        val navController = findNavController()
                        navController.popBackStack()
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