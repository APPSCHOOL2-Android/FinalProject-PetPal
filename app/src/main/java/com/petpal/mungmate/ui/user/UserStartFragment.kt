package com.petpal.mungmate.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentUserStartBinding

class UserStartFragment : Fragment() {

    lateinit var fragmentUserStartBinding: FragmentUserStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentUserStartBinding = FragmentUserStartBinding.inflate(layoutInflater)

        return fragmentUserStartBinding.root

    }
}