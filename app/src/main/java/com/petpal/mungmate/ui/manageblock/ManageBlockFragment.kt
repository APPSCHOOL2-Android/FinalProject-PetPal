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
import com.petpal.mungmate.model.BlockUser
import com.petpal.mungmate.model.FirestoreUserBasicInfoData

class ManageBlockFragment : Fragment() {

    private lateinit var _fragmentManageBlockBinding: FragmentManageBlockBinding
    private val fragmentManageBlockBinding get() = _fragmentManageBlockBinding
    private lateinit var blockUserRecyclerAdapter: BlockUserRecyclerAdapter
    private val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    private val addBlackList = mutableListOf<BlockUser>()
    val db = FirebaseFirestore.getInstance()
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _fragmentManageBlockBinding = FragmentManageBlockBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        blockUserRecyclerAdapter = BlockUserRecyclerAdapter(requireContext())

        fragmentManageBlockBinding.run {
            recyclerViewBlockedUser.run {
                adapter = blockUserRecyclerAdapter
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

            getFireStoreUserInfo()
        }

        return fragmentManageBlockBinding.root
    }

    private fun getFireStoreUserInfo() {

        if (user != null) {

            db.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {

                        // 현재 사용자가 블락한 사용자 리스트
                        val blockUserList = documentSnapshot.get("blockUserList") as? List<String>
                        addBlackList.clear()
                        if (blockUserList != null) {
                            for (item in blockUserList) {
                                Log.d("MANAGE_BLOCK", "블락 사용자 : $item")

                                // 블락 사용자 정보 가져오기
                                db.collection("users").document(item)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        if (document != null) {
                                            val userImage = document.getString("userImage")
                                            val nickname = document.getString("nickname")
                                            val blockUser = BlockUser(userImage, nickname)
                                            Log.d("MANAGE_BLOCK", "블락 사용자 닉네임 : $nickname")
                                            addBlackList.add(blockUser)
                                            blockUserRecyclerAdapter.submitList(addBlackList)
                                        }
                                    }
                            }
                        } else {
                            // 차단한 사용자가 없을 경우
                            Log.d("MANAGE_BLOCK", "차단 사용자가 존재하지 않음")
                        }
                    } else {
                        // 사용자 없을 경우
                        Log.d("MANAGE_BLOCK", "사용자가 존재하지 않음")
                    }
                }
        }
    }
}