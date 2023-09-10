package com.petpal.mungmate.ui.community

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.sidesheet.SideSheetBehavior
import com.google.android.material.sidesheet.SideSheetCallback
import com.google.android.material.sidesheet.SideSheetDialog
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

                        R.id.item_community_search->{
                            findNavController()
                                .navigate(R.id.action_item_community_to_communitySearchFragment)
                        }

                        R.id.item_community_category -> {
                            showSideSheet()
                        }

                    }
                    false
                }
            }

            communityPostWritingFab.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_item_community_to_communityWritingFragment)
            }
            communityRecyclerView()
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

    private fun showSideSheet() {
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

        communityCategoryAll.setOnClickListener {
            Snackbar.make(communityCategoryAll,"전체", Snackbar.LENGTH_SHORT)
                .show()
        }
        sideSheetDialog.setCancelable(false)
        sideSheetDialog.setCanceledOnTouchOutside(true)
        sideSheetDialog.setContentView(inflater)
        sideSheetDialog.show()
    }

}