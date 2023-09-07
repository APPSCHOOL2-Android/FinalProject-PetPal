package com.petpal.mungmate.community

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentCommunityPostDetailBinding


class CommunityPostDetailFragment : Fragment() {

    private lateinit var communityPostDetailBinding: FragmentCommunityPostDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        communityPostDetailBinding = FragmentCommunityPostDetailBinding.inflate(inflater)
        bottomNavigationViewGone()
        communityPostDetailBinding.run {
            toolbar()
            communityDetailRecyclerView()
        }

        return communityPostDetailBinding.root
    }

    private fun bottomNavigationViewGone() {
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.visibility = View.GONE
    }

    private fun FragmentCommunityPostDetailBinding.toolbar() {
        communityPostDetailToolbar.run {
            inflateMenu(R.menu.community_post_detail_author)
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_communityPostDetailFragment_to_item_community)
            }
            setOnMenuItemClickListener {

                when (it?.itemId) {
                    R.id.item_modify -> {
                        findNavController().navigate(R.id.action_communityPostDetailFragment_to_communityDetailModifyFragment)
                        Snackbar.make(requireView(), "글 수정하기", Snackbar.LENGTH_SHORT).show()
                    }

                    R.id.item_delete -> {
                        Snackbar.make(requireView(), "글 삭제하기", Snackbar.LENGTH_SHORT).show()
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