package com.petpal.mungmate.ui.managepet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentManagePetBinding
import com.petpal.mungmate.model.Announcement


class ManagePetFragment : Fragment() {
    private lateinit var _fragmentManagePetBinding: FragmentManagePetBinding
    private val fragmentManagePetBinding get() = _fragmentManagePetBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var petAdapter: PetAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mainActivity = activity as MainActivity
        petAdapter = PetAdapter()
        _fragmentManagePetBinding = FragmentManagePetBinding.inflate(layoutInflater)
        fragmentManagePetBinding.run {
            fabGoToAddPet.setOnClickListener {
                mainActivity.navigate(
                    R.id.action_managePetFragment_to_addPetFragment,
                    bundleOf("isAdd" to true)
                )
            }
            toolbarManagePet.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            //리사이클러뷰 어댑터 붙이고, 항목 클릭하면 아래 코드 달아주기
            recyclerViewPets.run {
                adapter = petAdapter
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    MaterialDividerItemDecoration(
                        context,
                        MaterialDividerItemDecoration.VERTICAL
                    )
                )
            }
            petAdapter.submitList(getSampleData())
        }
        return fragmentManagePetBinding.root
    }

    private fun getSampleData(): List<PetUiState> {
        return listOf(
            PetUiState("초롱이", "말티즈", "여아", 11, "착하고 순함", 5),
            PetUiState("초롱이", "말티즈", "여아", 11, "착하고 순함", 5),
            PetUiState("초롱이", "말티즈", "여아", 11, "착하고 순함", 5),
            PetUiState("초롱이", "말티즈", "여아", 11, "착하고 순함", 5),
            PetUiState("초롱이", "말티즈", "여아", 11, "착하고 순함", 5),
            PetUiState("초롱이", "말티즈", "여아", 11, "착하고 순함", 5),
            PetUiState("초롱이", "말티즈", "여아", 11, "착하고 순함", 5),
            PetUiState("초롱이", "말티즈", "여아", 11, "착하고 순함", 5),
        )
    }
}