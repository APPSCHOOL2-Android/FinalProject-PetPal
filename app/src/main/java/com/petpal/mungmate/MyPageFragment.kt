package com.petpal.mungmate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.petpal.mungmate.databinding.FragmentMyPageBinding

class MyPageFragment : Fragment() {
    private lateinit var _fragmentMyPageBinding: FragmentMyPageBinding
    private val fragmentMyPageBinding get() = _fragmentMyPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentMyPageBinding = FragmentMyPageBinding.inflate(layoutInflater)

        fragmentMyPageBinding.run {
            buttonManagePet.setOnClickListener {
                findNavController().navigate(R.id.action_to_managePetFragment)
            }

            buttonWalkHistory.setOnClickListener {
                findNavController().navigate(R.id.action_to_walkHistoryFragment)
            }
        }
        return fragmentMyPageBinding.root
    }

}