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
                .addOnSuccessListener { document ->
                    if (document != null) {

                        val blockUserList = document.get("blockUserList")
                        val resultList = blockUserList.toString()
                            .replace("[", "")  // '[' 제거
                            .replace("]", "")  // ']' 제거
                            .replace(" ", "")  // 공백 제거
                            .split(",")        // 쉼표(,)를 기준으로 문자열을 분리하여 리스트로 변환

                        val blackList = mutableListOf<String>()

                        for (matchResult in resultList) {

                            blackList.add(matchResult)
                        }
                        addBlackList.clear()
                        for (item in blackList) {
                            Log.d("아이템1", item)
                            db.collection("users").document(item)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        val userImage = document.getString("userImage")
                                        val nickname = document.getString("nickname")
                                        val blockUser = BlockUser(userImage, nickname)
                                        Log.d("아이템이요2", nickname.toString())
                                        addBlackList.add(blockUser)
                                        blockUserRecyclerAdapter.submitList(addBlackList)
                                    }
                                }
                        }

                    }
                }
        }
    }
}