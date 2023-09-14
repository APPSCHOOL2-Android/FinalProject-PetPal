package com.petpal.mungmate.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentUserInfoBinding

class UserInfoFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var _fragmentUserInfoBinding: FragmentUserInfoBinding
    private val fragmentUserInfoBinding get() = _fragmentUserInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentUserInfoBinding = FragmentUserInfoBinding.inflate(layoutInflater)

        //가입(true)인지 수정(false)인지 식별
        val isRegister = requireArguments().getBoolean("isRegister")

        fragmentUserInfoBinding.run {
            //수정화면이면
            if (!isRegister) {
                //수정완료 버튼 보이기
                userInfoToolbar.inflateMenu(R.menu.complete_menu)
                //다음 버튼 안보이기
                infoToNextButton.visibility = View.GONE
            }

            userInfoToolbar.run {
                setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
            }

            infoToNextButton.setOnClickListener {
                mainActivity.navigate(R.id.action_mainFragment_to_addPetFragment, bundleOf("isAdd" to true))
            }
        }
        return fragmentUserInfoBinding.root
    }
}