package com.petpal.mungmate.ui.managepet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentManagePetBinding


class ManagePetFragment : Fragment() {
    private lateinit var _fragmentManagePetBinding: FragmentManagePetBinding
    private val fragmentManagePetBinding get() = _fragmentManagePetBinding
    private lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mainActivity = activity as MainActivity
        _fragmentManagePetBinding = FragmentManagePetBinding.inflate(layoutInflater)
        fragmentManagePetBinding.run {
            fabGoToAddPet.setOnClickListener {
                mainActivity.navigate(R.id.action_managePetFragment_to_addPetFragment, bundleOf("isAdd" to true))
            }
            toolbarManagePet.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            //리사이클러뷰 어댑터 붙이고, 항목 클릭하면 아래 코드 달아주기

//            mainActivity.navigate(R.id.action_mainFragment_to_addPetFragment, bundleOf("isAdd" to false))
        }
        return fragmentManagePetBinding.root
    }
}