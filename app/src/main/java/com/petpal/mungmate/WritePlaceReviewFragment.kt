package com.petpal.mungmate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.petpal.mungmate.databinding.FragmentPlaceReviewBinding
import com.petpal.mungmate.databinding.FragmentWritePlaceReviewBinding


class WritePlaceReviewFragment : Fragment() {
    lateinit var fragmentWritePlaceReviewBinding: FragmentWritePlaceReviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentWritePlaceReviewBinding= FragmentWritePlaceReviewBinding.inflate(layoutInflater)


        return fragmentWritePlaceReviewBinding.root
    }

}