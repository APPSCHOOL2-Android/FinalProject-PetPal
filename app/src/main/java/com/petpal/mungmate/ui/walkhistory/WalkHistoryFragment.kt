package com.petpal.mungmate.ui.walkhistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentWalkHistoryBinding

class WalkHistoryFragment : Fragment() {
    private lateinit var _fragmentWalkHistoryBinding: FragmentWalkHistoryBinding
    private val fragmentWalkHistoryBinding get() = _fragmentWalkHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentWalkHistoryBinding = FragmentWalkHistoryBinding.inflate(layoutInflater)

        fragmentWalkHistoryBinding.run {
            calendar.setOnDateChangeListener { calendarView, i, i2, i3 ->
                //TODO: 아래 해당 날짜에 대한 데이터 보여줘야 함
            }

            cardViewDailyWalk.setOnClickListener {
                findNavController().navigate(R.id.action_walkHistoryFragment_to_walkHistoryDetailFragment)
            }

            toolbarWalkHistory.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }

        return fragmentWalkHistoryBinding.root
    }

}