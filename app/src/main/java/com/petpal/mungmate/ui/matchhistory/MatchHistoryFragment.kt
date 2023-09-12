package com.petpal.mungmate.ui.matchhistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentMatchHistoryBinding


class MatchHistoryFragment : Fragment() {

    private lateinit var _fragmentMatchHistoryFragment: FragmentMatchHistoryBinding
    private val fragmentMatchHistoryBinding get() = _fragmentMatchHistoryFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentMatchHistoryFragment = FragmentMatchHistoryBinding.inflate(layoutInflater)

        fragmentMatchHistoryBinding.run {
            recyclerViewMatchHistory.run {
                adapter = MatchHistoryRecyclerAdapter()
                layoutManager = LinearLayoutManager(requireContext())
            }

            toolbarManageHistory.run {
                setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
            }
        }
        return fragmentMatchHistoryBinding.root
    }

}