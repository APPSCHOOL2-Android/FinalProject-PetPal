package com.petpal.mungmate.ui.pet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentAddPetBinding
import com.petpal.mungmate.model.PetData
import com.petpal.mungmate.model.UserBasicInfoData
import com.petpal.mungmate.ui.user.UserViewModel
import com.petpal.mungmate.utils.gallerySetting
import com.petpal.mungmate.utils.launchGallery
import com.petpal.mungmate.utils.resizeAndCropBitmap
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddPetFragment : Fragment() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var _fragmentAddPetBinding: FragmentAddPetBinding
    private val fragmentAddPetBinding get() = _fragmentAddPetBinding
    private var userUid = ""
    private lateinit var userBasicInfoData: UserBasicInfoData

    // 갤러리 실행
    lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentAddPetBinding = FragmentAddPetBinding.inflate(layoutInflater)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        galleryLauncher = gallerySetting() { bitmap, uri ->
            //크기 조정
            val resizedBitmap = resizeAndCropBitmap(bitmap, 120, 120)
            fragmentAddPetBinding.imageViewAddPet.setImageBitmap(resizedBitmap)

            //저장할 uri 태그에 붙여두기
            fragmentAddPetBinding.imageViewAddPet.tag = uri
        }

        // StateFlow를 사용하여 사용자 데이터 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    userViewModel.user.collect { userData ->
                        // userData를 사용하여 사용자 정보 표시
                        if (userData != null) {
                            userUid = userData.uid
                        }
                    }
                }
                launch {
                    userViewModel.userBasicInfo.collect {
                        if (it != null) {
                            userBasicInfoData = it
                        }
                    }
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

            //견종 입력 자동완성
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                dogBreeds
            ).also { adapter ->
                autoCompleteTextViewPetBreed.setAdapter(adapter)
            }

            buttonSelectPetPhoto.setOnClickListener {
                launchGallery(galleryLauncher)
//                //갤러리에서 이미지 불러오기
//                loadImageFromGallery { bitmap, uri ->
//                    //크기 조정
//                    val resizedBitmap = resizeBitmap(bitmap, 120, 120)
//                    imageViewAddPet.setImageBitmap(resizedBitmap)
//
//                    //저장할 uri 태그에 붙여두기
//                    imageViewAddPet.tag = uri
//                }
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
                saveUserDataAndPetData()
                Snackbar.make(requireView(), "정보가 저장됐습니다.", Snackbar.LENGTH_SHORT).show()
            }
        }
        return fragmentAddPetBinding.root
    }

    private fun FragmentAddPetBinding.saveUserDataAndPetData() {

        //storage에 이미지 저장해야함


        val db = Firebase.firestore
        Log.d("user", userUid)

        val userRef = db.collection("users").document(userUid)
        val petRef = userRef.collection("pets").document()

        db.runBatch { transaction ->
            //사용자 정보 저장
            transaction.set(userRef, userBasicInfoData)
            transaction.set(
                petRef, PetData(
                    if (imageViewAddPet.tag == null) null else imageViewAddPet.tag as Uri,
                    textInputEditTextAddPetName.text.toString(),
                    autoCompleteTextViewPetBreed.text.toString(),
                    textInputPetBirthText.text.toString(),
                    getPetSex(toggleButtonPetGender.checkedButtonId).ordinal,
                    isPetNeutered(toggleButtonNeuter.checkedButtonId),
                    textInputEditTextPetWeight.text.toString().toDouble(),
                    textInputEditTextPetCharacter.text.toString()
                )
            )
        }.addOnSuccessListener {
            Snackbar.make(requireView(), "정보가 저장됐습니다.", Snackbar.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addPetFragment_to_mainFragment)
        }.addOnFailureListener { e ->
            Log.w("saveUser", "Error writing document", e)
        }

    }

    private fun savePetData(
        userUid: String,
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

    private fun saveUserData(
        userUid: String,
        userBasicInfoData: UserBasicInfoData,
    ) {
        val db = Firebase.firestore
        db.collection("users").document(userUid).set(
            userBasicInfoData
        ).addOnSuccessListener {
            Log.d("saveUser", "DocumentSnapshot successfully written!")
        }.addOnFailureListener { e -> Log.w("saveUser", "Error writing document", e) }

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