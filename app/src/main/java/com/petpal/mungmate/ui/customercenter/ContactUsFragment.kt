package com.petpal.mungmate.ui.customercenter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.petpal.mungmate.databinding.FragmentContactUsBinding

class ContactUsFragment : Fragment() {

    private var _fragmentContactUsBinding: FragmentContactUsBinding? = null
    private val fragmentContactUsBinding get() = _fragmentContactUsBinding!!

    private val inquiryCategoryArray = arrayOf( "회원정보", "산책", "포인트", "쇼핑", "커뮤니티", "채팅", "기타" )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentContactUsBinding = FragmentContactUsBinding.inflate(inflater)
        return fragmentContactUsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentContactUsBinding.run {
            toolbarContactUs.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            val adapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_dropdown_item_1line, inquiryCategoryArray)
            textViewInquiryCategory.setAdapter(adapter)
        }

    }

}