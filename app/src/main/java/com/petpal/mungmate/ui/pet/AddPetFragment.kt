package com.petpal.mungmate.ui.pet

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentAddPetBinding
import com.petpal.mungmate.model.PetData
import com.petpal.mungmate.ui.user.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddPetFragment : Fragment() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var _fragmentAddPetBinding: FragmentAddPetBinding
    private val fragmentAddPetBinding get() = _fragmentAddPetBinding
    private var userUid = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentAddPetBinding = FragmentAddPetBinding.inflate(layoutInflater)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        // StateFlow를 사용하여 사용자 데이터 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.user.collect { userData ->
                // userData를 사용하여 사용자 정보 표시
                if (userData != null) {
                    userUid = userData.uid
                }
            }
        }

        val dogBreeds = resources.getStringArray(R.array.dog_breeds)
        //추가(true)인지 수정(false)인지 식별
        val isAdd = requireArguments().getBoolean("isAdd")

        fragmentAddPetBinding.run {

            //수정작업이면
            if (!isAdd) {
                //수정완료 아이콘 띄우기
                toolbarAddPet.inflateMenu(R.menu.complete_menu)
                //툴바 타이틀 반려견 정보 수정으로 변경
                toolbarAddPet.title = "반려견 정보 수정"
                //가입하기 버튼 삭제하기
                buttonPetComplete.visibility = View.GONE

                //성별 버튼 클릭 방지
                buttonHe.isClickable = false
                buttonShe.isClickable = false
            }

            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                dogBreeds
            ).also { adapter ->
                autoCompleteTextViewPetBreed.setAdapter(adapter)
            }


            textInputPetBirthText.setOnClickListener {
                // DatePicker 기본값 오늘로 설정
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("날짜 선택")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

                datePicker.addOnPositiveButtonClickListener { selectedDateInMillis ->
                    val selectDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(Date(selectedDateInMillis))
                    textInputPetBirthText.setText(selectDate)
                }
                datePicker.show(parentFragmentManager, "tag")
            }

            toolbarAddPet.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            buttonPetComplete.setOnClickListener {
                savePetData(
                    if(imageViewAddPet.tag == null) null else imageViewAddPet.tag as Uri,
                    textInputEditTextAddPetName.text.toString(),
                    autoCompleteTextViewPetBreed.text.toString(),
                    textInputPetBirthText.text.toString(),
                    getPetSex(toggleButtonPetGender.checkedButtonId),
                    isPetNeutered(toggleButtonNeuter.checkedButtonId),
                    textInputEditTextPetWeight.text.toString().toDouble(),
                    textInputEditTextPetCharacter.text.toString()
                ) {
                    Snackbar.make(requireView(), "반려견 정보가 저장됐습니다.", Snackbar.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_addPetFragment_to_mainFragment)
                }

            }
        }
        return fragmentAddPetBinding.root
    }

    private fun savePetData(
        imageURI: Uri? = null,
        name: String,
        breed: String,
        birth: String,
        petSex: PetSex,
        isNeutered: Boolean,
        weight: Double,
        character: String,
        onSuccessCallback: () -> Unit,
    ) {
        val db = Firebase.firestore

        db.collection("users").document(userUid).collection("pets")
            .document().set(
                PetData(imageURI, name, breed, birth, petSex.ordinal, isNeutered, weight, character)
            ).addOnSuccessListener {
                Snackbar.make(requireView(), "가입을 환영합니다!", Snackbar.LENGTH_SHORT).show()
                Log.d("savePet", "DocumentSnapshot successfully written!")
                onSuccessCallback()
            }
            .addOnFailureListener { e ->
                Log.w("savePet", "Error writing document", e)
            }
    }

    private fun isPetNeutered(checkedButtonId: Int): Boolean {
        return checkedButtonId == R.id.buttonNeuter
    }

    private fun getPetSex(checkedButtonId: Int): PetSex {
        return if (checkedButtonId == R.id.buttonHe) {
            PetSex.MALE
        } else {
            PetSex.FEMALE
        }
    }

}

enum class PetSex {
    MALE, FEMALE
}