package com.petpal.mungmate.ui.walkreview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentWalkReviewHistoryBinding

class WalkReviewHistoryFragment : Fragment() {
    private var _fragmentWalkReviewHistoryBinding: FragmentWalkReviewHistoryBinding? = null
    private val fragmentWalkReviewHistoryBinding get() = _fragmentWalkReviewHistoryBinding!!

    private var oldScrollYPosition: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentWalkReviewHistoryBinding = FragmentWalkReviewHistoryBinding.inflate(inflater)
        return fragmentWalkReviewHistoryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentWalkReviewHistoryBinding.run {
            toolbarWalkReview.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            recyclerViewWalkReview.run {
                adapter = WalkReviewHistoryAdapter()
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }

            nestedScrollViewWalkReview.viewTreeObserver.addOnScrollChangedListener {
                if (nestedScrollViewWalkReview.scrollY > oldScrollYPosition || nestedScrollViewWalkReview.scrollY <= 0) {
                    // 아래로 스크롤 중
                    fabWalkReviewHistory.hide()
                }else if (nestedScrollViewWalkReview.scrollY < oldScrollYPosition) {
                    // 위로 스크롤 중
                    fabWalkReviewHistory.show()
                }
                oldScrollYPosition = nestedScrollViewWalkReview.scrollY
            }

            fabWalkReviewHistory.setOnClickListener {
                nestedScrollViewWalkReview.smoothScrollTo(0, 0)
            }
        }
    }
}