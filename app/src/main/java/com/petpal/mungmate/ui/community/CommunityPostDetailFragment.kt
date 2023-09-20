package com.petpal.mungmate.ui.community


import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.faltenreich.skeletonlayout.Skeleton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentCommunityPostDetailBinding
import com.petpal.mungmate.model.Comment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class CommunityPostDetailFragment : Fragment() {

    private lateinit var communityPostDetailBinding: FragmentCommunityPostDetailBinding
    var isClicked = false
    lateinit var postGetId: String
    private lateinit var skeleton: Skeleton
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val postCommentList: MutableList<Comment> = mutableListOf()

    private lateinit var communityDetailCommentAdapter: CommunityDetailCommentAdapter
    private lateinit var commentViewModel: CommentViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        communityPostDetailBinding = FragmentCommunityPostDetailBinding.inflate(inflater)
        commentViewModel = ViewModelProvider(requireActivity())[CommentViewModel::class.java]

        communityPostDetailBinding.run {

            val args: CommunityPostDetailFragmentArgs by navArgs()
            val postid = args.position
            postGetId = postid
            toolbar()
            lottie()
            getDataFirebasFirestore()
            communityDetailRecyclerView()

            commentViewModel.postCommentList.observe(viewLifecycleOwner) { commentList ->
                communityDetailCommentAdapter.updateData(commentList)
                communityPostDetailCommentCount.text = "댓글 ${commentList.size.toString()}"
            }
            val iconColorNotInput =
                ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer)
            val iconColorInput = ContextCompat.getColor(requireContext(), R.color.black)
            communityPostDetailCommentTextInputLayout.setEndIconOnClickListener {
                if (communityPostDetailCommentTextInputEditText.text.toString().isEmpty()) {
                    val snackbar = Snackbar.make(
                        communityPostDetailCommentTextInputLayout,
                        "댓글을 입력해주세요",
                        Snackbar.LENGTH_SHORT
                    )

                    // Snackbar 위치 변경 바로 위로..
                    snackbar.anchorView = communityPostDetailCommentTextInputLayout
                    snackbar.show()
                }
            }

            communityPostDetailCommentTextInputEditText.addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        communityPostDetailCommentTextInputLayout.setEndIconTintList(
                            ColorStateList.valueOf(
                                iconColorNotInput
                            )
                        )
                    } else {

                        communityPostDetailCommentTextInputLayout.setEndIconTintList(
                            ColorStateList.valueOf(
                                iconColorInput
                            )
                        )
                        communityPostDetailCommentTextInputLayout.setEndIconOnClickListener {
                            coroutineScope.launch(Dispatchers.IO) {
                                val documentSnapshot = getFirestoreData(postGetId)
                                withContext(Dispatchers.Main) {

                                    updateComment(documentSnapshot)

                                    communityPostDetailCommentTextInputEditText.text?.clear()
                                    val imm =
                                        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                    imm.hideSoftInputFromWindow(view?.windowToken, 0)
                                }
                            }
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

        }

        return communityPostDetailBinding.root
    }

    private fun getDataFirebasFirestore() {
        coroutineScope.launch(Dispatchers.IO) {
            skeleton = communityPostDetailBinding.skeletonLayout.apply { showSkeleton() }
            val documentSnapshot = getFirestoreData(postGetId)
            withContext(Dispatchers.Main) {
                updateUI(documentSnapshot)
                skeleton.showOriginal()
            }
        }
    }

//    private fun FragmentCommunityPostDetailBinding.scrollUpFab() {
//        val fadeIn = AlphaAnimation(0f, 1f)
//        fadeIn.duration = 500
//        val fadeOut = AlphaAnimation(1f, 0f)
//        val targetScrollPosition =
//            resources.getDimensionPixelSize(R.dimen.target_scroll_position)
//
//        fadeOut.duration = 500
//        communityPostDetailNestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
//            Log.d("scrollY", scrollY.toString())
//
//            if (scrollY >= targetScrollPosition && !isFabVisible) {
//
//                communityPostDetailCommentFab.startAnimation(fadeIn)
//                communityPostDetailCommentFab.visibility = View.VISIBLE
//                isFabVisible = true
//
//            } else if (scrollY < targetScrollPosition && isFabVisible) {
//
//                communityPostDetailCommentFab.startAnimation(fadeOut)
//                communityPostDetailCommentFab.visibility = View.GONE
//                isFabVisible = false
//
//            }
//        }
//
//        communityPostDetailCommentFab.setOnClickListener {
//            communityPostDetailNestedScrollView.smoothScrollTo(0, 0)
//        }
//    }

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

            val postImagesList = documentSnapshot.get("postImages") as? List<*>
            var postImagesGetList = mutableListOf<String>()
            if (postImagesList!!.isNotEmpty()) {
                val cleanedString = postImagesList?.get(0).toString().replace("{image=", postGetId)
                val imageUrl = cleanedString.trim()
                val imageUrlWithoutBrace = imageUrl.removeSuffix("}")
                postImagesGetList.add(imageUrlWithoutBrace)
            }


            val postLike = documentSnapshot.getLong("postLike")

            val postCommentData = documentSnapshot.get("postComment") as? List<HashMap<String, Any>>

            if (postCommentData != null) {
                for (dataMap in postCommentData) {
                    val comment = Comment(
                        commentUid = dataMap["commentUid"] as Long,
                        commentNickName = dataMap["commentNickName"] as String?,
                        commentUserPlace = dataMap["commentUserPlace"] as String?,
                        commentDateCreated = dataMap["commentDateCreated"] as String?,
                        commentContent = dataMap["commentContent"] as String?,
                        commentLike = dataMap["commentLike"] as Long,
                        parentID = dataMap["parentID"] as String?
                    )
                    postCommentList.add(comment)

                }
                communityPostDetailBinding.communityPostDetailCommentCount.text =
                    "댓글 ${postCommentList.size.toString()}"
                communityDetailCommentAdapter.updateData(postCommentList)
                Log.d("성공", postCommentList.toString())
            } else {
                Log.e("오류", "postComment 데이터를 가져오지 못했습니다.")
            }

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
                Log.d("확인",postImagesGetList[0].toString())
                if (postImagesList != null) {
                    if (postImagesGetList.isNotEmpty()) {
                        Glide
                            .with(requireContext())
                            .load(postImagesGetList[0])
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .fitCenter()
                            .error(R.drawable.baseline_error_24)
                            .fallback(R.drawable.main_image)
                            .into(communityPostDetailPostImage)
                    } else {
                        communityPostDetailPostCardView.visibility = View.GONE
                    }
                } else {
                    Snackbar.make(
                        communityPostDetailPostImage,
                        "이미지를 가져오는데 실패했습니다.",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }

                communityPostDetailPostTitle.text = postTitle
                communityPostUserPlace.text = userPlace
                communityPostDateCreated.text = timeAgo
                communityPostDetailFavoriteCounter.text = postLike.toString()
                communityPostDetailCommentCounter.text = postCommentList.size.toString()
                communityPostDetailContent.text = postContent
            }
        }
    }

    private fun FragmentCommunityPostDetailBinding.communityDetailRecyclerView() {
        communityPostDetailCommentRecyclerView.run {
            communityDetailCommentAdapter =
                CommunityDetailCommentAdapter(requireContext(), mutableListOf(), postGetId)
            adapter = communityDetailCommentAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun updateComment(documentSnapshot: DocumentSnapshot?) {
        if (documentSnapshot != null) {
            val db = FirebaseFirestore.getInstance()
            val postID = documentSnapshot.getString("postID")

            val currentDateTime = Date()
            val formattedDateTime = formatDateTimeToNewFormat(currentDateTime)

            val newComment = Comment(
                commentId = UUID.randomUUID().toString(),
                1,
                "작성자",
                "장소",
                formattedDateTime,
                communityPostDetailBinding.communityPostDetailCommentTextInputEditText.text.toString(),
                3,
                "0"
            )

            val newPostCommentList: MutableList<Comment> = mutableListOf()
            newPostCommentList.addAll(postCommentList)
            newPostCommentList.add(newComment)

            val documentRef = db.collection("Post").document(postID!!)
            documentRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    documentRef.update("postComment", newPostCommentList)
                        .addOnSuccessListener {
                            postCommentList.clear()
                            postCommentList.addAll(newPostCommentList)
                            commentViewModel.setCommentList(newPostCommentList)
                        }
                        .addOnFailureListener { e ->
                            Snackbar.make(requireView(), "실패", Snackbar.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    private fun formatDateTimeToNewFormat(date: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }

    override fun onDestroy() {
        super.onDestroy()
        postCommentList.clear()
    }

}