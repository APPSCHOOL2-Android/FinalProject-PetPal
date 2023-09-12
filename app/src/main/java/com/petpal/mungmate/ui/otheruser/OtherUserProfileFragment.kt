package com.petpal.mungmate.ui.otheruser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentOtherUserProfileBinding

class OtherUserProfileFragment : Fragment() {
    private lateinit var _fragmentOtherUserProfileBinding: FragmentOtherUserProfileBinding
    private val fragmentOtherUserProfileBinding get() = _fragmentOtherUserProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentOtherUserProfileBinding = FragmentOtherUserProfileBinding.inflate(layoutInflater)

        fragmentOtherUserProfileBinding.run {
            recyclerViewPost.run {
                adapter = PostAdapter()
                layoutManager =LinearLayoutManager(requireContext())
                addItemDecoration(
                    MaterialDividerItemDecoration(
                        context,
                        MaterialDividerItemDecoration.VERTICAL
                    )
                )
            }

            toolbarOtherUserProfile.run {
                setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
            }
        }

        return fragmentOtherUserProfileBinding.root
    }

}