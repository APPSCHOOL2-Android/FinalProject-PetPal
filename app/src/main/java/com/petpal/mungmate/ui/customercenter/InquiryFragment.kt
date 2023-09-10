package com.petpal.mungmate.ui.customercenter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentInquiryBinding

class InquiryFragment : Fragment() {
    private var _fragmentInquiryBinding: FragmentInquiryBinding? = null
    private val fragmentInquiryBinding get() = _fragmentInquiryBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentInquiryBinding = FragmentInquiryBinding.inflate(inflater)
        return fragmentInquiryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentInquiryBinding.run {
            toolbarInquiry.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            buttonContactUs.setOnClickListener {
                findNavController().navigate(R.id.action_inquiryFragment_to_contactUsFragment)
            }
        }
    }

}