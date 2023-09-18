package com.petpal.mungmate.ui.community


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentCommunityPostDetailBinding
import com.petpal.mungmate.model.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone


class CommunityPostDetailFragment : Fragment() {

    private lateinit var communityPostDetailBinding: FragmentCommunityPostDetailBinding
    var isClicked = false
    private var isFabVisible = true
    lateinit var postGetId: String
    private lateinit var skeleton: Skeleton
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
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

            scrollUpFab()

            coroutineScope.launch(Dispatchers.IO) {
                skeleton = communityPostDetailBinding.skeletonLayout.apply { showSkeleton() }
                val documentSnapshot = getFirestoreData(postGetId)
                withContext(Dispatchers.Main) {
                    updateUI(documentSnapshot)

                    skeleton.showOriginal()
                }
            }
        }

        return communityPostDetailBinding.root
    }

    private fun FragmentCommunityPostDetailBinding.scrollUpFab() {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 500
        val fadeOut = AlphaAnimation(1f, 0f)
        val targetScrollPosition =
            resources.getDimensionPixelSize(R.dimen.target_scroll_position)

        fadeOut.duration = 500
        communityPostDetailNestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            Log.d("scrollY", scrollY.toString())

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
                        val bundle = Bundle().apply {
                            putString("positionPostId", postGetId)
                        }

                        findNavController().navigate(
                            R.id.action_communityPostDetailFragment_to_communityDetailModifyFragment,
                            bundle
                        )

                    }

                    R.id.item_delete -> {
                        val dlg = CommunityDeleteDialog(requireContext())
                        dlg.listener =
                            object : CommunityDeleteDialog.LessonDeleteDialogClickedListener {
                                override fun onDeleteClicked() {
                                    val db = FirebaseFirestore.getInstance()
                                    val postRef = db.collection("Post")
                                    postGetId?.let { id ->
                                        postRef.document(id)
                                            .delete()
                                            .addOnFailureListener {
                                                Snackbar.make(
                                                    rootView,
                                                    "데이터를 삭제 하는데 실패했습니다.",
                                                    Snackbar.LENGTH_SHORT
                                                ).show()
                                            }
                                    }

                                    Snackbar.make(rootView, "게시글이 삭제 되었습니다.", Snackbar.LENGTH_SHORT)
                                        .show()
                                    val navController = findNavController()
                                    navController.popBackStack()
                                }
                            }
                        dlg.start()
                    }
                }
                false
            }
        }
    }

    // Firestore에서 데이터 가져오기
    private suspend fun getFirestoreData(id: String): DocumentSnapshot? {
        val db = FirebaseFirestore.getInstance()
        val postRef = db.collection("Post").document(id).get().await()
        return if (postRef.exists()) postRef else null
    }

    // UI 업데이트
    private fun updateUI(documentSnapshot: DocumentSnapshot?) {
        if (documentSnapshot != null) {
            val userImage = documentSnapshot.getString("userImage")
            val postTitle = documentSnapshot.getString("postTitle")
            val userPlace = documentSnapshot.getString("userPlace")
            val postDateCreated = documentSnapshot.getString("postDateCreated")
            val postImages = documentSnapshot.getString("postImages")
            val postLike = documentSnapshot.getLong("postLike")
            val postComment = documentSnapshot.getString("postComment")
            val postContent = documentSnapshot.getString("postContent")

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            val snapshotTime =
                dateFormat.parse(postDateCreated) // Firestore에서 가져온 시간 문자열을 Date 객체로 변환

            val currentTime = Date()
            val timeDifferenceMillis =
                currentTime.time - snapshotTime.time  // Firestore 시간에서 현재 시간을 뺌


            val timeAgo = when {
                timeDifferenceMillis < 60_000 -> "방금 전" // 1분 미만
                timeDifferenceMillis < 3_600_000 -> "${timeDifferenceMillis / 60_000}분 전" // 1시간 미만
                timeDifferenceMillis < 86_400_000 -> "${timeDifferenceMillis / 3_600_000}시간 전" // 1일 미만
                else -> "${timeDifferenceMillis / 86_400_000}일 전" // 1일 이상 전
            }

            communityPostDetailBinding.run {
                Glide
                    .with(requireContext())
                    .load(userImage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .into(communityPostDetailProfileImage)

                Glide
                    .with(requireContext())
                    .load(postImages)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .into(communityPostDetailPostImage)

                communityPostDetailPostTitle.text = postTitle
                communityPostUserPlace.text = userPlace
                communityPostDateCreated.text = timeAgo
                communityPostDetailFavoriteCounter.text = postLike.toString()
                communityPostDetailCommentCounter.text = postComment
                communityPostDetailContent.text=postContent
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