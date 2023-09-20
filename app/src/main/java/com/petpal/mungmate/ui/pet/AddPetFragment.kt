package com.petpal.mungmate.ui.pet

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentAddPetBinding
import com.petpal.mungmate.model.FirestoreUserBasicInfoData
import com.petpal.mungmate.model.PetData
import com.petpal.mungmate.model.UserBasicInfoData
import com.petpal.mungmate.ui.user.UserViewModel
import com.petpal.mungmate.utils.gallerySetting
import com.petpal.mungmate.utils.launchGallery
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
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
        galleryLauncher = gallerySetting() { bitmap ->
            fragmentAddPetBinding.imageViewAddPet.setImageBitmap(bitmap)
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

            //갤러리에서 사진 선택
            buttonSelectPetPhoto.setOnClickListener {
                launchGallery(galleryLauncher)
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

        val storage = Firebase.storage
        val userImageRef = storage.getReference("userImage").child(userUid)
        val petImageRef = storage.getReference("petImage")
            .child("${userUid}${fragmentAddPetBinding.textInputEditTextAddPetName.text.toString()}")

        //storage에 사진 저장
        uploadImage(userImageRef, petImageRef)

        val db = Firebase.firestore
        Log.d("user", userUid)

        val userRef = db.collection("users").document(userUid)
        val petRef = userRef.collection("pets").document()

        db.runBatch { transaction ->
            //사용자 정보 저장
            transaction.set(
                userRef, FirestoreUserBasicInfoData(
                    userImageRef.path,
                    userBasicInfoData.nickname,
                    userBasicInfoData.birthday,
                    userBasicInfoData.ageVisible,
                    userBasicInfoData.gender,
                    userBasicInfoData.availability,
                    userBasicInfoData.walkHoursStart,
                    userBasicInfoData.walkHoursEnd
                )
            )
            //반려견 정보 저장
            transaction.set(
                petRef, PetData(
                    petImageRef.path,
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

    private fun uploadImage(userImageRef: StorageReference, petImageRef: StorageReference) {
        val userImage = userBasicInfoData.userImage
        val petImage = (fragmentAddPetBinding.imageViewAddPet.drawable as BitmapDrawable).bitmap

        //image 압축하기
        val userBaos = ByteArrayOutputStream()
        userImage.compress(Bitmap.CompressFormat.JPEG, 100, userBaos)
        val userImageData = userBaos.toByteArray()

        val petBaos = ByteArrayOutputStream()
        petImage.compress(Bitmap.CompressFormat.JPEG, 100, petBaos)
        val petImageData = petBaos.toByteArray()

        //userImage/userUid
        userImageRef.putBytes(userImageData)
            .addOnSuccessListener { }
            .addOnFailureListener { }

        //petImage/userUid초롱이
        petImageRef.putBytes(petImageData)
            .addOnSuccessListener { }
            .addOnFailureListener { }
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