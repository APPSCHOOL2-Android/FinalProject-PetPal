package com.petpal.mungmate.ui.user

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentUserInfoBinding
import com.petpal.mungmate.model.UserBasicInfoData
import com.petpal.mungmate.utils.gallerySetting
import com.petpal.mungmate.utils.launchGallery
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserInfoFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var _fragmentUserInfoBinding: FragmentUserInfoBinding
    private val fragmentUserInfoBinding get() = _fragmentUserInfoBinding
    private lateinit var userViewModel: UserViewModel
    private var userUid = ""

    // 갤러리 실행
    lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentUserInfoBinding = FragmentUserInfoBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        galleryLauncher = gallerySetting() { bitmap ->
            fragmentUserInfoBinding.startMainImageView.setImageBitmap(bitmap)
        }

        return fragmentUserInfoBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        //가입(true)인지 수정(false)인지 식별
        val isRegister = requireArguments().getBoolean("isRegister")

        if(isRegister) {
            Snackbar.make(requireView(), "사용자 정보를 입력해주세요", Snackbar.LENGTH_SHORT).show()
        }


        // StateFlow를 사용하여 사용자 데이터 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.user.collect { userData ->
                // userData를 사용하여 사용자 정보 표시
                if (userData != null) {
                    fragmentUserInfoBinding.run {
                        //닉네임 표시
                        textInputUserNicknameText.setText(userData.displayName)

                        //프로필 사진 표시
                        Glide.with(requireContext())
                            .load(userData.photoUrl)
                            .placeholder(R.drawable.user_profile)
                            .fallback(R.drawable.user_profile)
                            .into(startMainImageView)
                    }

                    //authentication의 uid
                    userUid = userData.uid
                }
            }
        }



        fragmentUserInfoBinding.run {
            //수정화면이면
            if (!isRegister) {
                //수정완료 버튼 보이기
                userInfoToolbar.inflateMenu(R.menu.complete_menu)
                //툴바 타이틀 내 정보 수정으로 변경
                userInfoToolbar.title = "내 정보 수정"
                //다음 버튼 안보이기
                infoToNextButton.visibility = View.GONE
            }

            userInfoToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }


            textInputUserBirthText.setOnClickListener {
                // DatePicker 기본값 오늘로 설정
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("날짜 선택")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

                datePicker.addOnPositiveButtonClickListener { selectedDateInMillis ->
                    val selectDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(Date(selectedDateInMillis))
                    textInputUserBirthText.setText(selectDate)
                }
                datePicker.show(parentFragmentManager, "tag")
            }

            //언제든 가능해요 옵션을 누르면 산책가능 시간 입력 칸이 없어지도록
            userInfoRadiogroup.setOnCheckedChangeListener { radioGroup, i ->
                if (i == R.id.radioAlways) {
                    linearWhenSelected.visibility = View.GONE
                } else {
                    linearWhenSelected.visibility = View.VISIBLE
                }
            }

            //사진 선택 버튼을 통해 갤러리에서 사진 불러오기
            infoSelectImageButton.setOnClickListener {
                launchGallery(galleryLauncher)
            }

            infoToNextButton.setOnClickListener {

                //입력한 데이터로부터 저장할 데이터 구성하기
                val userInfoData = UserBasicInfoData(
                    (startMainImageView.drawable as BitmapDrawable).bitmap,
                    textInputUserNicknameText.text.toString(),
                    textInputUserBirthText.text.toString(),
                    switchUserInfo.isChecked,
                    getSelectedSex(toggleButtonUserSex.checkedButtonId).ordinal,
                    getSelectedWalkHour(userInfoRadiogroup.checkedRadioButtonId).ordinal,
                    textInputStartText.text.toString(),
                    textInputEndText.text.toString()
                )

                //viewmodel에 저장하기
                userViewModel.setUserBasicInfoData(userInfoData)

                mainActivity.navigate(
                    R.id.action_userInfoFragment_to_addPetFragment,
                    bundleOf("isAdd" to true, "isUserJoin" to true)
                )

            }


        }


    }


    private fun getSelectedWalkHour(checkedRadioButtonId: Int): Availability {
        return if (checkedRadioButtonId == R.id.radioAlways) {
            Availability.WHENEVER
        } else {
            Availability.SPECIFIC
        }
    }

    private fun getSelectedSex(checkedButtonId: Int): Sex {
        return when (checkedButtonId) {
            R.id.buttonMale -> {
                Sex.MALE
            }

            R.id.buttonFemale -> {
                Sex.FEMALE
            }

            else -> {
                Sex.NONE
            }
        }
    }

}

enum class Availability {
    WHENEVER, SPECIFIC
}

enum class Sex {
    MALE, FEMALE, NONE
}
