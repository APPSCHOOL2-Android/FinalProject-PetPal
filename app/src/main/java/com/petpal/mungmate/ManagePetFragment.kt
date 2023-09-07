package com.petpal.mungmate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.petpal.mungmate.databinding.FragmentManagePetBinding


class ManagePetFragment : Fragment() {
    private lateinit var _fragmentManagePetBinding: FragmentManagePetBinding
    private val fragmentManagePetBinding get() = _fragmentManagePetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentManagePetBinding = FragmentManagePetBinding.inflate(layoutInflater)
        fragmentManagePetBinding.run{
            fabGoToAddPet.setOnClickListener {
                //TODO: 반려견 정보 입력으로 이동
            }
            toolbarManagePet.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
        return fragmentManagePetBinding.root
    }
}