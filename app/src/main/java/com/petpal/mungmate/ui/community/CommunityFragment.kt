package com.petpal.mungmate.ui.community

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.sidesheet.SideSheetBehavior
import com.google.android.material.sidesheet.SideSheetCallback
import com.google.android.material.sidesheet.SideSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentCommunityBinding


class CommunityFragment : Fragment() {

    lateinit var communityBinding: FragmentCommunityBinding
    private lateinit var skeleton: Skeleton
    private lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communityBinding = FragmentCommunityBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        communityBinding.communityPostRecyclerView.run {
            val communityAdapter = CommunityAdapter(requireContext(), mainActivity)
            adapter = communityAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            skeleton = applySkeleton(R.layout.row_community, 10).apply { showSkeleton() }

            Handler(Looper.getMainLooper()).postDelayed({
                skeleton.showOriginal()
            }, 3000)

            // 최상단 FAB 숨기기, 스크롤시 FAB 보이기
            val fadeIn = AlphaAnimation(0f, 1f).apply { duration = 500 }
            val fadeOut = AlphaAnimation(1f, 0f).apply { duration = 500 }
            var isTop = true

            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    // 최상단에 있는 순간 -> FAB 숨기기
                    if (!canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        communityBinding.communityPostWritingUpFab.startAnimation(fadeOut)
                        communityBinding.communityPostWritingUpFab.visibility = View.GONE
                        isTop = true
                    } else {
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {



        communityBinding.run {
            communityToolbar.run {
                setOnMenuItemClickListener {
                    when (it?.itemId) {

                        R.id.item_community_search->{
                            mainActivity
                                .navigate(
                                    R.id.action_mainFragment_to_communitySearchFragment,

                                )
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
                    .navigate(
                        R.id.action_mainFragment_to_communityWritingFragment,

                    )
            }

            communityPostWritingUpFab.setOnClickListener {
                // 최상단 이동
                communityPostRecyclerView.smoothScrollToPosition(0)
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
            val communityAdapter = CommunityAdapter(requireContext(), mainActivity)
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