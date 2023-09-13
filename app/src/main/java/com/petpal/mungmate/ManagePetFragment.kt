package com.petpal.mungmate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
                mainActivity.navigate(R.id.action_mainFragment_to_addPetFragment)
            }
            toolbarManagePet.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
        return fragmentManagePetBinding.root
    }
}