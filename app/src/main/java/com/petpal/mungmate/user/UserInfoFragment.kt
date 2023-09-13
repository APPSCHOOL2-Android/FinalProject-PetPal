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
        mainActivity = activity as MainActivity
        _fragmentUserInfoBinding = FragmentUserInfoBinding.inflate(layoutInflater)

        fragmentUserInfoBinding.run {
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