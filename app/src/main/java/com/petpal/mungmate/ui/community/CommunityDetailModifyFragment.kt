package com.petpal.mungmate.ui.community

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentCommunityDetailModifyBinding


class CommunityDetailModifyFragment : Fragment() {
    lateinit var fragmentCommunityDetailModifyBinding: FragmentCommunityDetailModifyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCommunityDetailModifyBinding = FragmentCommunityDetailModifyBinding.inflate(inflater)
        bottomNavigationViewGone()
        fragmentCommunityDetailModifyBinding.run {
            toolbar()
        }
        return fragmentCommunityDetailModifyBinding.root
    }

    private fun bottomNavigationViewGone() {
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.visibility = View.GONE
    }

    private fun FragmentCommunityDetailModifyBinding.toolbar() {
        communityModifyToolbar.run {
            inflateMenu(R.menu.community_post_modify)
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener {

                when (it?.itemId) {
                    R.id.item_modify -> {
                        Snackbar.make(requireView(), "완료", Snackbar.LENGTH_SHORT).show()
                    }

                }
                false
            }
        }
    }
}