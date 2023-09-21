package com.petpal.mungmate.ui.community

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.material.sidesheet.SideSheetBehavior
import com.google.android.material.sidesheet.SideSheetCallback
import com.google.android.material.sidesheet.SideSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentCommunityBinding
import com.petpal.mungmate.model.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs


class CommunityFragment : Fragment() {

    lateinit var communityBinding: FragmentCommunityBinding
    private lateinit var skeleton: Skeleton
    private lateinit var mainActivity: MainActivity
    private lateinit var communityAdapter: CommunityAdapter
    private lateinit var rootView: View
    private lateinit var firestoreListener: ListenerRegistration // Firestore에서 이벤트 리스너를 등록할 때 반환되는 객체
    private var firestoreJob: Job? = null
    var categoryName = ""
    private lateinit var commentViewModel: CommentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        communityBinding = FragmentCommunityBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        rootView = inflater.inflate(R.layout.fragment_community, container, false)
        commentViewModel = ViewModelProvider(requireActivity())[CommentViewModel::class.java]

        communityBinding.run {
            toolbar()
            communityRecyclerView()
            refreshLayout()
            configFirestore()
        }

        return communityBinding.root
    }

    private fun FragmentCommunityBinding.toolbar() {
        communityToolbar.run {
            setOnMenuItemClickListener {
                when (it?.itemId) {

                    R.id.item_community_search -> {
                        mainActivity
                            .navigate(R.id.action_mainFragment_to_communitySearchFragment)
                    }

                    R.id.item_community_category -> {
                        showSideSheet()
                    }
                }
                false
            }
        }

        communityPostWritingFab.setOnClickListener {
            mainActivity
                .navigate(R.id.action_mainFragment_to_communityWritingFragment)
        }

        communityPostWritingUpFab.setOnClickListener {
            // 최상단 이동
            communityPostRecyclerView.smoothScrollToPosition(0)
        }
    }

    private fun FragmentCommunityBinding.communityRecyclerView() {
        communityPostRecyclerView.run {
            communityAdapter = CommunityAdapter(requireContext(), mainActivity, mutableListOf())
            adapter = communityAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            skeleton = applySkeleton(R.layout.row_community, 5).apply { showSkeleton() }


            // 최상단 FAB 숨기기, 스크롤시 FAB 보이기
            val fadeIn = AlphaAnimation(0f, 1f).apply { duration = 500 }
            val fadeOut = AlphaAnimation(1f, 0f).apply { duration = 500 }
            var isTop = true

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val currentPosition =
                        (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    Log.d("currentPosition", currentPosition.toString())
                    // 최상단에 있는 순간 -> FAB 숨기기
                    if (!canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        communityBinding.communityPostWritingUpFab.startAnimation(fadeOut)
                        communityBinding.communityPostWritingUpFab.visibility = View.GONE
                        isTop = true
                    } else if (currentPosition >= 5) {
                        // 최상단에서 내리기 시작하는 순간 -> FAB 보이기
                        if (isTop) {
                            communityBinding.communityPostWritingUpFab.visibility = View.VISIBLE
                            communityBinding.communityPostWritingUpFab.startAnimation(fadeIn)
                            isTop = false
                        }
                    }
                }
            })
        }
    }

    private fun FragmentCommunityBinding.refreshLayout() {
        refreshLayout.setOnRefreshListener {
            // 새로고침 코드를 작성
            communityAdapter.clear()
            communityRecyclerView()
            configFirestore()
            communityAdapter.notifyDataSetChanged()
            refreshLayout.isRefreshing = false
            communityBinding.communityPostNotDataLinearLayout.visibility = View.GONE
            communityBinding.communityPostWritingFab.visibility = View.VISIBLE
            communityBinding.communityPostNotDataLottieView.cancelAnimation()

        }
    }

    private fun FragmentCommunityBinding.showSideSheet() {
        val sideSheetDialog = SideSheetDialog(requireContext())

        sideSheetDialog.behavior.addCallback(object : SideSheetCallback() {
            override fun onStateChanged(sheet: View, newState: Int) {
                if (newState == SideSheetBehavior.STATE_DRAGGING) {
                    sideSheetDialog.behavior.state = SideSheetBehavior.STATE_EXPANDED
                }
            }

            override fun onSlide(sheet: View, slideOffset: Float) {
            }
        })

        val inflater = layoutInflater.inflate(R.layout.community_side_sheet_behavior, null)
        val communityCategoryAll = inflater.findViewById<TextView>(R.id.communityCategoryAll)
        val communityCategoryDaily = inflater.findViewById<TextView>(R.id.communityCategoryDaily)
        val communityCategoryWalkLog =
            inflater.findViewById<TextView>(R.id.communityCategoryWalkLog)
        val communityCategoryWalkingMaidMatching =
            inflater.findViewById<TextView>(R.id.communityCategoryWalkingMaidMatching)
        val communityCategoryWalkingLocationReviews =
            inflater.findViewById<TextView>(R.id.communityCategoryWalkingLocationReviews)
        val communityCategoryDogProductReviews =
            inflater.findViewById<TextView>(R.id.communityCategoryDogProductReviews)
        val communityCategoryShihTzu =
            inflater.findViewById<TextView>(R.id.communityCategoryShihTzu)
        val communityCategoryPoodle = inflater.findViewById<TextView>(R.id.communityCategoryPoodle)
        val communityCategoryPomeranian =
            inflater.findViewById<TextView>(R.id.communityCategoryPomeranian)
        val communityCategorychihuahua =
            inflater.findViewById<TextView>(R.id.communityCategorychihuahua)
        val communityCategoryMaltese =
            inflater.findViewById<TextView>(R.id.communityCategoryMaltese)
        val communityCategoryJindoDog =
            inflater.findViewById<TextView>(R.id.communityCategoryJindoDog)
        val communityCategoryRetriever =
            inflater.findViewById<TextView>(R.id.communityCategoryRetriever)
        val communityCategoryWelshCorgi =
            inflater.findViewById<TextView>(R.id.communityCategoryWelshCorgi)
        val communityCategoryPuppies =
            inflater.findViewById<TextView>(R.id.communityCategoryPuppies)

        communityCategoryAll.setOnClickListener {
            communityBinding.communityPostNotDataLinearLayout.visibility = View.GONE
            categoryName = "전체"
            communityAdapter.clear()
            communityRecyclerView()
            configCategoryFirestore(categoryName)
            communityAdapter.notifyDataSetChanged()
        }
        communityCategoryDaily.setOnClickListener {
            communityBinding.communityPostNotDataLinearLayout.visibility = View.GONE
            categoryName = "일상"
            communityAdapter.clear()
            communityRecyclerView()
            configCategoryFirestore(categoryName)
            communityAdapter.notifyDataSetChanged()
        }
        communityCategoryWalkLog.setOnClickListener {
            communityBinding.communityPostNotDataLinearLayout.visibility = View.GONE
            categoryName = "산책일지"
            communityAdapter.clear()
            communityRecyclerView()
            configCategoryFirestore(categoryName)
            communityAdapter.notifyDataSetChanged()
        }
        communityCategoryWalkingMaidMatching.setOnClickListener {
            communityBinding.communityPostNotDataLinearLayout.visibility = View.GONE
            categoryName = "산책 메이트 구해요"
            communityAdapter.clear()
            communityRecyclerView()
            configCategoryFirestore(categoryName)
            communityAdapter.notifyDataSetChanged()
        }
        communityCategoryWalkingLocationReviews.setOnClickListener {
            communityBinding.communityPostNotDataLinearLayout.visibility = View.GONE
            categoryName = "장소 후기"
            communityAdapter.clear()
            communityRecyclerView()
            configCategoryFirestore(categoryName)
            communityAdapter.notifyDataSetChanged()
        }

        communityCategoryDogProductReviews.setOnClickListener {
            communityBinding.communityPostNotDataLinearLayout.visibility = View.GONE
            categoryName = "애견용품 후기"
            communityAdapter.clear()
            communityRecyclerView()
            configCategoryFirestore(categoryName)
            communityAdapter.notifyDataSetChanged()
        }
        communityCategoryShihTzu.setOnClickListener {
            communityBinding.communityPostNotDataLinearLayout.visibility = View.GONE
            categoryName = "시츄 아지트"
            communityAdapter.clear()
            communityRecyclerView()
            configCategoryFirestore(categoryName)
            communityAdapter.notifyDataSetChanged()
        }
        communityCategoryPoodle.setOnClickListener {
            communityBinding.communityPostNotDataLinearLayout.visibility = View.GONE
            categoryName = "푸들 아지트"
            communityAdapter.clear()
            communityRecyclerView()
            configCategoryFirestore(categoryName)
            communityAdapter.notifyDataSetChanged()
        }
        communityCategoryPomeranian.setOnClickListener {
            communityBinding.communityPostNotDataLinearLayout.visibility = View.GONE
            categoryName = "포메라니안 아지트"
            communityAdapter.clear()
            communityRecyclerView()
            configCategoryFirestore(categoryName)
            communityAdapter.notifyDataSetChanged()
        }
        communityCategorychihuahua.setOnClickListener {
            communityBinding.communityPostNotDataLinearLayout.visibility = View.GONE
            categoryName = "치와와 아지트"
            communityAdapter.clear()
            communityRecyclerView()
            configCategoryFirestore(categoryName)
            communityAdapter.notifyDataSetChanged()
        }
        communityCategoryMaltese.setOnClickListener {
            communityBinding.communityPostNotDataLinearLayout.visibility = View.GONE
            categoryName = "몰티즈 아지트"
            communityAdapter.clear()
            communityRecyclerView()
            configCategoryFirestore(categoryName)
            communityAdapter.notifyDataSetChanged()
        }
        communityCategoryJindoDog.setOnClickListener {
            communityBinding.communityPostNotDataLinearLayout.visibility = View.GONE
            categoryName = "진돗개 아지트"
            communityAdapter.clear()
            communityRecyclerView()
            configCategoryFirestore(categoryName)
            communityAdapter.notifyDataSetChanged()
        }
        communityCategoryRetriever.setOnClickListener {
            communityBinding.communityPostNotDataLinearLayout.visibility = View.GONE
            categoryName = "리트리버 아지트"
            communityAdapter.clear()
            communityRecyclerView()
            configCategoryFirestore(categoryName)
            communityAdapter.notifyDataSetChanged()
        }
        communityCategoryWelshCorgi.setOnClickListener {
            communityBinding.communityPostNotDataLinearLayout.visibility = View.GONE
            categoryName = "웰시 코기 아지트"
            communityAdapter.clear()
            communityRecyclerView()
            configCategoryFirestore(categoryName)
            communityAdapter.notifyDataSetChanged()
        }
        communityCategoryPuppies.setOnClickListener {
            communityBinding.communityPostNotDataLinearLayout.visibility = View.GONE
            categoryName = "나머지 아이들 아지트"
            communityAdapter.clear()
            communityRecyclerView()
            configCategoryFirestore(categoryName)
            communityAdapter.notifyDataSetChanged()
        }

        sideSheetDialog.setCancelable(false)
        sideSheetDialog.setCanceledOnTouchOutside(true)
        sideSheetDialog.setContentView(inflater)
        sideSheetDialog.show()
    }

    private fun configFirestore() {

        val db = FirebaseFirestore.getInstance()
        db.collection("Post")
            .orderBy("postDateCreated", Query.Direction.DESCENDING) // 최근에 등록한거 순으로..
            .get()
            .addOnSuccessListener { snapshots ->
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")

                for (document in snapshots) {
                    val community = document.toObject(Post::class.java)
                    val snapshotTime =
                        dateFormat.parse(community.postDateCreated) // Firestore에서 가져온 시간 문자열을 Date 객체로 변환

                    val currentTime = Date()
                    val timeDifferenceMillis =
                        currentTime.time - snapshotTime.time  // Firestore 시간에서 현재 시간을 뺌

                    val timeAgo = when {
                        timeDifferenceMillis < 60_000 -> "방금 전" // 1분 미만
                        timeDifferenceMillis < 3_600_000 -> "${timeDifferenceMillis / 60_000}분 전" // 1시간 미만
                        timeDifferenceMillis < 86_400_000 -> "${timeDifferenceMillis / 3_600_000}시간 전" // 1일 미만
                        else -> "${timeDifferenceMillis / 86_400_000}일 전" // 1일 이상 전
                    }
                    community.postDateCreated = "$timeAgo"
                    communityAdapter.add(community)
                }

                firestoreJob?.cancel() // 이전의 Job이 있으면 취소
                firestoreJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    skeleton.showOriginal()
                }

            }
            .addOnFailureListener {
                Snackbar.make(rootView, "데이터를 불러오는데 실패했습니다.", Snackbar.LENGTH_SHORT).show()
            }
    }

    private fun configCategoryFirestore(categoryName: String) {
        if (categoryName == "전체") {
            val db = FirebaseFirestore.getInstance()
            val postRef = db.collection("Post")
                .orderBy("postDateCreated", Query.Direction.DESCENDING) // 최근에 등록한거 순으로..
            firestoreListener = postRef.addSnapshotListener { values, error ->
                if (error != null) {
                    skeleton.showOriginal()
                    return@addSnapshotListener
                }

                if(values!!.isEmpty){
                    communityBinding.communityPostNotDataLinearLayout.visibility = View.VISIBLE
                    communityBinding.communityPostWritingFab.visibility = View.GONE
                }else{
                    communityBinding.communityPostNotDataLinearLayout.visibility = View.GONE
                    communityBinding.communityPostWritingFab.visibility = View.VISIBLE
                }

                for (value in values!!.documentChanges) {
                    val community = value.document.toObject(Post::class.java)
                    community.postID = value.document.id

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")// 시간대를 UTC로 설정

                    val snapshotTime = if (community.postDateCreated?.isNotEmpty() == true) {
                        dateFormat.parse(community.postDateCreated)// Firestore에서 가져온 시간 문자열을 Date 객체로 변환
                    } else {
                        null // 빈 문자열인 경우 파싱을 수행하지 않음
                    }
                    val currentTime = Date()
                    val timeDifferenceMillis =
                        currentTime.time - (snapshotTime?.time ?: 0)  // Firestore 시간에서 현재 시간을 뺌


                    val timeAgo = when {
                        timeDifferenceMillis < 60_000 -> "방금 전" // 1분 미만
                        timeDifferenceMillis < 3_600_000 -> "${timeDifferenceMillis / 60_000}분 전" // 1시간 미만
                        timeDifferenceMillis < 86_400_000 -> "${timeDifferenceMillis / 3_600_000}시간 전" // 1일 미만
                        else -> "${timeDifferenceMillis / 86_400_000}일 전" // 1일 이상 전
                    }

                    community.postDateCreated = "$timeAgo"
                    community.postImages as? List<String> ?: emptyList()

                    when (value.type) {
                        DocumentChange.Type.ADDED -> communityAdapter.add(community)
                        DocumentChange.Type.MODIFIED -> communityAdapter.update(community)
                        DocumentChange.Type.REMOVED -> communityAdapter.delete(community)
                        else -> {}
                    }
                }

                firestoreJob?.cancel() // 이전의 Job이 있으면 취소
                firestoreJob = CoroutineScope(Dispatchers.Main).launch {
                    skeleton.showOriginal()
                }
            }
        } else {
            var num = 0
            val db = FirebaseFirestore.getInstance()
            val postRef = db.collection("Post")
                .whereEqualTo("postCategory", categoryName)
                .orderBy("postDateCreated", Query.Direction.DESCENDING) // 최근에 등록한거 순으로..

            firestoreListener = postRef.addSnapshotListener { values, error ->
                if (error != null) {

                    if (num == 0) {
                        communityBinding.communityPostNotDataLinearLayout.visibility = View.VISIBLE
                        communityBinding.communityPostWritingFab.visibility = View.GONE
                    } else {
                        communityBinding.communityPostNotDataLinearLayout.visibility = View.GONE
                        communityBinding.communityPostWritingFab.visibility = View.VISIBLE
                    }

                    skeleton.showOriginal()
                    return@addSnapshotListener
                } else {
                    num++
                }

                for (value in values!!.documentChanges) {
                    val community = value.document.toObject(Post::class.java)
                    community.postID = value.document.id
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")// 시간대를 UTC로 설정

                    val snapshotTime = if (community.postDateCreated?.isNotEmpty() == true) {
                        dateFormat.parse(community.postDateCreated)// Firestore에서 가져온 시간 문자열을 Date 객체로 변환
                    } else {
                        null // 빈 문자열인 경우 파싱을 수행하지 않음
                    }
                    val currentTime = Date()
                    val timeDifferenceMillis =
                        currentTime.time - (snapshotTime?.time ?: 0)  // Firestore 시간에서 현재 시간을 뺌


                    val timeAgo = when {
                        timeDifferenceMillis < 60_000 -> "방금 전" // 1분 미만
                        timeDifferenceMillis < 3_600_000 -> "${timeDifferenceMillis / 60_000}분 전" // 1시간 미만
                        timeDifferenceMillis < 86_400_000 -> "${timeDifferenceMillis / 3_600_000}시간 전" // 1일 미만
                        else -> "${timeDifferenceMillis / 86_400_000}일 전" // 1일 이상 전
                    }

                    community.postDateCreated = "$timeAgo"
                    community.postImages as? List<String> ?: emptyList()
                    when (value.type) {
                        DocumentChange.Type.ADDED -> communityAdapter.add(community)
                        DocumentChange.Type.MODIFIED -> communityAdapter.update(community)
                        DocumentChange.Type.REMOVED -> communityAdapter.delete(community)
                        else -> {}
                    }
                }
                firestoreJob?.cancel() // 이전의 Job이 있으면 취소
                firestoreJob = CoroutineScope(Dispatchers.Main).launch {
                    skeleton.showOriginal()

                }
            }
        }
    }



    override fun onPause() {
        super.onPause()

        firestoreJob?.cancel()
    }
}