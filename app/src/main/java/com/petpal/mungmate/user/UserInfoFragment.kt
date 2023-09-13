package com.petpal.mungmate.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val isProfile = requireArguments().getBoolean("isProfile")

        fragmentUserInfoBinding.run {
            if (isProfile) {
                userInfoToolbar.inflateMenu(R.menu.my_profile_menu)
                infoToNextButton.visibility = View.GONE
            }

            userInfoToolbar.run {
                setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
            }

            infoToNextButton.setOnClickListener {
                mainActivity.navigate(R.id.action_mainFragment_to_addPetFragment)
            }
        }
        return fragmentUserInfoBinding.root
    }
}