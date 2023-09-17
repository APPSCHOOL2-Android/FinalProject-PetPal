package com.petpal.mungmate.ui.walkhistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentWalkHistoryDetailBinding

class WalkHistoryDetailFragment : Fragment() {
    private lateinit var _fragmentWalkHistoryDetail: FragmentWalkHistoryDetailBinding
    private val fragmentWalkHistoryDetailBinding get() = _fragmentWalkHistoryDetail
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentWalkHistoryDetail = FragmentWalkHistoryDetailBinding.inflate(layoutInflater)

        fragmentWalkHistoryDetailBinding.run {
            toolbarWalkHistoryDetail.setNavigationOnClickListener {
                findNavController().popBackStack()
            }


        }

        return fragmentWalkHistoryDetailBinding.root
    }

}