package com.petpal.mungmate.ui.pet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentManagePetBinding
import com.petpal.mungmate.ui.mypage.MyPageViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ManagePetFragment : Fragment() {
    private lateinit var _fragmentManagePetBinding: FragmentManagePetBinding
    private val fragmentManagePetBinding get() = _fragmentManagePetBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var petAdapter: PetAdapter
    private lateinit var myPageViewModel: MyPageViewModel

    private val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val addPetList = mutableListOf<PetUiState>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mainActivity = activity as MainActivity
        petAdapter = PetAdapter(requireContext())

        myPageViewModel = ViewModelProvider(requireActivity())[MyPageViewModel::class.java]
        //내 반려견 정보 불러오기
        myPageViewModel.loadPetInfo(user!!.uid)

        myPageViewModel.simplePetList.observe(viewLifecycleOwner) {
            Log.d("managepet",it.toString())
            petAdapter.submitList(it)
        }

        _fragmentManagePetBinding = FragmentManagePetBinding.inflate(layoutInflater)
        fragmentManagePetBinding.run {
            fabGoToAddPet.setOnClickListener {
                mainActivity.navigate(
                    R.id.action_managePetFragment_to_addPetFragment,
                    bundleOf("isAdd" to true, "isUserJoin" to false),

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
        }
        return fragmentManagePetBinding.root
    }


}