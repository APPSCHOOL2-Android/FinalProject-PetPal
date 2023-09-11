package com.petpal.mungmate.ui.community

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentCommunitySearchBinding

class CommunitySearchFragment : Fragment() {

    lateinit var communitySearchBinding: FragmentCommunitySearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        communitySearchBinding = FragmentCommunitySearchBinding.inflate(inflater)
        communitySearchBinding.run {
            communitySearchToolbar.run {
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    it.findNavController()
                        .popBackStack()
                }
            }
        }

        return communitySearchBinding.root
    }


}