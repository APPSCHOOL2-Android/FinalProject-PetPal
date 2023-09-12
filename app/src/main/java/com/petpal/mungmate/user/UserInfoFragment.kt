package com.petpal.mungmate.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentUserInfoBinding

class UserInfoFragment : Fragment() {
    private lateinit var _fragmentUserInfoBinding: FragmentUserInfoBinding
    private val fragmentUserInfoBinding get() = _fragmentUserInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentUserInfoBinding = FragmentUserInfoBinding.inflate(layoutInflater)

        val isProfile = requireArguments().getBoolean("isProfile")
        if(isProfile) {
            fragmentUserInfoBinding.run{
                userInfoToolbar.run {
                    inflateMenu(R.menu.my_profile_menu)
                    setNavigationIcon(R.drawable.arrow_back_24px)
                    setNavigationOnClickListener {
                        findNavController().popBackStack()
                    }

                }

                infoToNextButton.visibility = View.GONE
            }

        }
        return fragmentUserInfoBinding.root
    }
}