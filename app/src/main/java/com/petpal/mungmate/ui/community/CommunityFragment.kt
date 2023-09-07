package com.petpal.mungmate.ui.community

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentCommunityBinding


class CommunityFragment : Fragment() {

    lateinit var communityBinding: FragmentCommunityBinding
    private lateinit var skeleton: Skeleton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        communityBinding = FragmentCommunityBinding.inflate(inflater)

        bottomNavigationViewVISIBLE()
        communityBinding.run {
            communityToolbar.run {
                inflateMenu(R.menu.community_category_menu)

                setOnMenuItemClickListener {
                    when (it?.itemId) {
                        R.id.item_category -> {
                            findNavController()
                                .navigate(R.id.action_item_community_to_communityCategoryListFragment)
                        }

                    }
                    false
                }
            }

            //Chip 이벤트 처리
            communityChipGroup.setOnCheckedStateChangeListener { group, checkedId ->

                val selectedChip = group.findViewById<Chip>(group.checkedChipId)
                val selectedCategory = selectedChip.text.toString()
                Log.d("확인", selectedCategory)
                when (selectedCategory) {
                    "전체보기" -> {
                        communityRecyclerView()
                    }

                    "일상보기" -> {

                    }

                    "산책일지" -> {

                    }

                    "장소후기" -> {

                    }

                }
            }

            communityPostWritingFab.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_item_community_to_communityWritingFragment)
            }

        }

        return communityBinding.root
    }

    private fun bottomNavigationViewVISIBLE() {
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.visibility = View.VISIBLE
    }

    private fun FragmentCommunityBinding.communityRecyclerView() {
        communityPostRecyclerView.run {
            val communityAdapter = CommunityAdapter(requireContext())
            adapter = communityAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            skeleton = applySkeleton(R.layout.row_community, 10).apply { showSkeleton() }

            Handler(Looper.getMainLooper()).postDelayed({
                skeleton.showOriginal()
            }, 3000)
        }
    }

}