package com.petpal.mungmate.ui.manageblock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.petpal.mungmate.databinding.FragmentManageBlockBinding

class ManageBlockFragment : Fragment() {

    private lateinit var _fragmentManageBlockBinding: FragmentManageBlockBinding
    private val fragmentManageBlockBinding get() = _fragmentManageBlockBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _fragmentManageBlockBinding = FragmentManageBlockBinding.inflate(layoutInflater)

        fragmentManageBlockBinding.run {
            recyclerViewBlockedUser.run {
                adapter = BlockUserRecyclerAdapter()
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    MaterialDividerItemDecoration(
                        context,
                        MaterialDividerItemDecoration.VERTICAL
                    )
                )
            }

            toolbarManageBlock.run {
                setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
            }
        }

        return fragmentManageBlockBinding.root
    }


}