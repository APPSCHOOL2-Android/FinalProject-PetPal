package com.petpal.mungmate.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentUserInfoBinding

class UserInfoFragment : Fragment() {

    lateinit var fragmentUserInfoBinding: FragmentUserInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentUserInfoBinding = FragmentUserInfoBinding.inflate(layoutInflater)

        fragmentUserInfoBinding.run {

            // 상단 툴바
            userInfoToolbar.run {

            }
            // 이미지 선택 btn
            infoSelectImageButton.setOnClickListener {

            }
            // 닉네임 textInputLayout
            textInputUserNickname.run {
                error = getString(R.string.nickname_error)
            }
            // 생일 비공객 처리 switch
            switchUserInfo.run {

            }
            // 생일 textInputLayout
            textInputUserBirth.run {

            }
            // user 성별 chipGroup
            userInfoChipgroup.run {

            }
            // user 산책 가능 시간대 radioGroup
            userInfoRadiogroup.run {

            }
            // 작성 완료 -> 다음 화면 btn
            infoToNextButton.setOnClickListener {

            }

        }

        return fragmentUserInfoBinding.root

    }
}