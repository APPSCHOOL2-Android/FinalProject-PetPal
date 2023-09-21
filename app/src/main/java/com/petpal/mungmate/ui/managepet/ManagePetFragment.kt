package com.petpal.mungmate.ui.managepet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentManagePetBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ManagePetFragment : Fragment() {
    private lateinit var _fragmentManagePetBinding: FragmentManagePetBinding
    private val fragmentManagePetBinding get() = _fragmentManagePetBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var petAdapter: PetAdapter

    private val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val addPetList = mutableListOf<PetUiState>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mainActivity = activity as MainActivity
        petAdapter = PetAdapter(requireContext())
        _fragmentManagePetBinding = FragmentManagePetBinding.inflate(layoutInflater)
        fragmentManagePetBinding.run {
            fabGoToAddPet.setOnClickListener {
                mainActivity.navigate(
                    R.id.action_managePetFragment_to_addPetFragment,
                    bundleOf("isAdd" to true),

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
            Log.d("uid값", user!!.uid)
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .document(user!!.uid)
                .collection("pets")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val petList = mutableListOf<Map<String, Any>>()

                    for (document in querySnapshot) {
                        val petData = document.data
                        petList.add(petData)
                    }

                    for (petData in petList) {
                        val petName = petData["name"]
                        val birth = petData["birth"]
                        val breed = petData["breed"]
                        val weight = petData["weight"]
                        val character = petData["character"]
                        val petSex = petData["petSex"]
                        val petImageUrl = petData["petImageUrl"]



                        var whatGender = "남아"
                        if(petSex==1){
                            whatGender="여아"
                        }
                        var age= calculateAge(birth.toString())
                        addPetList.clear()
                        addPetList.add(PetUiState(petName.toString(),breed.toString(), whatGender, age.toLong(), character.toString(), weight.toString(),petImageUrl.toString()))
                        petAdapter.submitList(addPetList)

                        // 원하는 작업 수행
                        Log.d("addPetList", addPetList.toString())
                    }
                }
        }
        return fragmentManagePetBinding.root
    }

//    private fun getSampleData(): List<PetUiState> {
//        return listOf(
//            PetUiState("초롱이", "말티즈", "여아", 11, "착하고 순함", "5"),
//
//        )
//    }

    private fun calculateAge(birthDate: String): Int {

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val birthDateObj = dateFormat.parse(birthDate)


        val diff = currentDate.time - birthDateObj.time


        val ageInMillis = diff / (1000L * 60 * 60 * 24 * 365)

        // 연령을 정수로 변환하여 반환
        return ageInMillis.toInt()
    }
}