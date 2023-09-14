package com.petpal.mungmate.ui.otheruser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.petpal.mungmate.databinding.FragmentOtherUserProfileBinding

class OtherUserProfileFragment : Fragment() {
    private lateinit var _fragmentOtherUserProfileBinding: FragmentOtherUserProfileBinding
    private val fragmentOtherUserProfileBinding get() = _fragmentOtherUserProfileBinding
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentOtherUserProfileBinding = FragmentOtherUserProfileBinding.inflate(layoutInflater)
        postAdapter = PostAdapter()

        fragmentOtherUserProfileBinding.run {
            recyclerViewPost.run {
                adapter = postAdapter
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    MaterialDividerItemDecoration(
                        context,
                        MaterialDividerItemDecoration.VERTICAL
                    )
                )
            }

            postAdapter.submitList(getSampleData())

            toolbarOtherUserProfile.run {
                setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
            }
        }

        return fragmentOtherUserProfileBinding.root
    }

    private fun getSampleData(): List<PostUiState> {
        return listOf(
            PostUiState("제목", "카테고리", "2023-09-13", 5),
            PostUiState("제목", "카테고리", "2023-09-13", 5),
            PostUiState("제목", "카테고리", "2023-09-13", 5),
            PostUiState("제목", "카테고리", "2023-09-13", 5),
            PostUiState("제목", "카테고리", "2023-09-13", 5),
            PostUiState("제목", "카테고리", "2023-09-13", 5),
        )

    }

}