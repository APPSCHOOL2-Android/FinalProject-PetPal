package com.petpal.mungmate.ui.placereview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentWritePlaceReviewBinding


class WritePlaceReviewFragment : Fragment() {
    lateinit var fragmentWritePlaceReviewBinding: FragmentWritePlaceReviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentWritePlaceReviewBinding= FragmentWritePlaceReviewBinding.inflate(layoutInflater)

        //리뷰 등록 완료하면 place 바텀 시트 올라와있는 상태로 돌아가게 ㄱ
        fragmentWritePlaceReviewBinding.buttonPlaceReviewSubmit.setOnClickListener {
            findNavController().navigate(R.id.action_writePlaceReviewFragment_to_item_walk)

        }


        return fragmentWritePlaceReviewBinding.root
    }

}