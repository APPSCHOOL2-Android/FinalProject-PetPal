package com.petpal.mungmate.ui.manageblock

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.databinding.FragmentManageBlockBinding
import com.petpal.mungmate.model.FirestoreUserBasicInfoData

class ManageBlockFragment : Fragment() {
    private val TAG = "MANAGE_BLOCK"

    private lateinit var _fragmentManageBlockBinding: FragmentManageBlockBinding
    private val fragmentManageBlockBinding get() = _fragmentManageBlockBinding
    private lateinit var mainActivity: MainActivity

    private lateinit var blockedUserRecyclerAdapter: BlockUserRecyclerAdapter

    val user = FirebaseAuth.getInstance().currentUser
    private val usersCollectionRef = FirebaseFirestore.getInstance().collection("users")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _fragmentManageBlockBinding = FragmentManageBlockBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        blockedUserRecyclerAdapter = BlockUserRecyclerAdapter()

        fragmentManageBlockBinding.run {
            recyclerViewBlockedUser.run {
                adapter = blockedUserRecyclerAdapter
                layoutManager = LinearLayoutManager(requireContext())

                addItemDecoration(
                    MaterialDividerItemDecoration(
                        context,
                        MaterialDividerItemDecoration.VERTICAL
                    )
                )
            }

            toolbarManageBlock.run {
                setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
            }

            // 사용자의 차단 목록 가져와서 RecyclerView에 설정
            getBlockUserList()
        }

        return fragmentManageBlockBinding.root
    }

    private fun getBlockUserList() {
        if (user != null) {
            usersCollectionRef
                .document(user.uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {

                        // 현재 사용자가 블락한 사용자 리스트
                        val userInfo = documentSnapshot.toObject(FirestoreUserBasicInfoData::class.java)
                        val blockUserList = userInfo?.blockUserList ?: emptyList()

                        // RecyclerView 어댑터에 차단 사용자 리스트 설정
                        blockedUserRecyclerAdapter.submitList(blockUserList)

                        updateBlockListUI()
                    } else {
                        // 사용자 문서가 없을 경우
                        Log.d(TAG, "사용자가 존재하지 않음")
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "문서 가져오기 실패")
                }
        }
    }

    fun updateBlockListUI() {
        if (blockedUserRecyclerAdapter.currentList.isEmpty()) {
            fragmentManageBlockBinding.layoutNoBlockUser.visibility = View.VISIBLE
            fragmentManageBlockBinding.recyclerViewBlockedUser.visibility = View.GONE
        } else {
            fragmentManageBlockBinding.layoutNoBlockUser.visibility = View.GONE
            fragmentManageBlockBinding.recyclerViewBlockedUser.visibility = View.VISIBLE
        }
    }
}