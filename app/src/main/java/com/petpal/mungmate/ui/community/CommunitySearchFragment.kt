package com.petpal.mungmate.ui.community

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.inputmethod.EditorInfo
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentCommunityBinding
import com.petpal.mungmate.databinding.FragmentCommunitySearchBinding
import com.petpal.mungmate.model.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone


class CommunitySearchFragment : Fragment() {

    lateinit var communitySearchBinding: FragmentCommunitySearchBinding
    private lateinit var communitySearchViewModel: CommunitySearchViewModel
    private lateinit var communityRecentSearchesAdapter: CommunityRecentSearchesAdapter

    private lateinit var firestoreListener: ListenerRegistration // Firestore에서 이벤트 리스너를 등록할 때 반환되는 객체
    private var firestoreJob: Job? = null

    private lateinit var communityAdapter: CommunitySearchAdapter
    private lateinit var skeleton: Skeleton

    private lateinit var mainActivity: MainActivity
    var searchWord=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        communitySearchBinding = FragmentCommunitySearchBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        communitySearchViewModel = ViewModelProvider(this)[CommunitySearchViewModel::class.java]

        communitySearchBinding.run {

            communitySearchToolbar.run {
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    it.findNavController()
                        .popBackStack()
                }

            }
            val layoutManager = LinearLayoutManager(requireContext())

            // 아이템 역순 코드
            layoutManager.reverseLayout = true
            layoutManager.stackFromEnd = true

            communityRecentSearchesRecyclerView.layoutManager = layoutManager

            communityRecentSearchesAdapter = CommunityRecentSearchesAdapter(
                onItemDeleteListener = { deletedItem ->
                    // 뷰모델을 통해 아이템 삭제
                    communitySearchViewModel.delete(deletedItem)
                },
                onItemSearchListener = { query ->
                    Log.d("뭐징",query)
                    searchWord=query
                    searchRecyclerView(communitySearchBinding, this@CommunitySearchFragment)
                    configCategoryFirestore(searchWord)
                    searchView.hide()
                }
            )
            communityRecentSearchesRecyclerView.adapter = communityRecentSearchesAdapter

            communitySearchViewModel.allSearchHistory.observe(viewLifecycleOwner) {
                communityRecentSearchesAdapter.submitList(it)
            }

            communityRecentSearchesAllClearButton.setOnClickListener {
                communitySearchViewModel.deleteAllData()
            }

            searchView.editText.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = searchView.text.toString().trim()

                    // 검색어가 비어 있지 않으면..
                    if (query.isNotEmpty()) {
                        val searchesEntity = SearchesEntity(0, query)

                        communitySearchViewModel.insert(searchesEntity)
                        communitySearchViewModel.updateSearchQuery(query)
                        searchRecyclerView(communitySearchBinding, this@CommunitySearchFragment)
                        configCategoryFirestore(query)
                    }
                    Log.d("뭐징2",searchWord)
                    communitySearchBinding.searchBar.text = query
                    searchView.hide()
                    true
                } else {
                    false
                }
            }
        }

        return communitySearchBinding.root
    }

    private fun searchRecyclerView(
        fragmentCommunitySearchBinding: FragmentCommunitySearchBinding,
        communitySearchFragment: CommunitySearchFragment
    ) {
        fragmentCommunitySearchBinding.communitySearchRecyclerView.run {
            communityAdapter =
                CommunitySearchAdapter(requireContext(), mainActivity, mutableListOf())
            adapter = communitySearchFragment.communityAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            skeleton = applySkeleton(R.layout.row_community, 5).apply { showSkeleton() }
        }
    }

    private fun configCategoryFirestore(categoryName: String) {
        communitySearchBinding.communityPostSearchNotDataLinearLayout.visibility = View.GONE
        var num = 0
        val db = FirebaseFirestore.getInstance()
        val postRef = db.collection("Post")
            .whereEqualTo("postTitle", categoryName)
            .orderBy("postDateCreated", Query.Direction.DESCENDING) // 최근에 등록한거 순으로..

        firestoreListener = postRef.addSnapshotListener { values, error ->
            if (error != null) {
                if (num == 0) {
                    communitySearchBinding.communityPostSearchNotDataLinearLayout.visibility =
                        View.VISIBLE
                } else {
                    communitySearchBinding.communityPostSearchNotDataLinearLayout.visibility =
                        View.GONE

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

    override fun onResume() {
        super.onResume()
        val query = communitySearchViewModel.searchQuery.value ?: ""

        searchRecyclerView(communitySearchBinding, this@CommunitySearchFragment)
        communitySearchBinding.communityPostSearchNotDataLinearLayout.visibility = View.GONE
        var num = 0
        val db = FirebaseFirestore.getInstance()
        val postRef = db.collection("Post")
            .whereEqualTo("postTitle", query)
            .orderBy("postDateCreated", Query.Direction.DESCENDING) // 최근에 등록한거 순으로..

        firestoreListener = postRef.addSnapshotListener { values, error ->
            if (error != null) {
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

