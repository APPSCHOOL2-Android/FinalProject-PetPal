package com.petpal.mungmate.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.petpal.mungmate.databinding.FragmentReportUserBinding

class ReportUserFragment : Fragment() {

    private var _fragmentReportUserBinding :FragmentReportUserBinding? = null
    private val fragmentReportUserBinding get() = _fragmentReportUserBinding!!

    private val reportCategoryArray = arrayOf("홍보 계정이에요", "욕설을 해요", "약속 시간에 나오지 않았어요", "비매너 유저", "기타")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentReportUserBinding = FragmentReportUserBinding.inflate(inflater)
        return fragmentReportUserBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentReportUserBinding.run {
            toolbarReportUser.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, reportCategoryArray)
            textViewReportCategory.setAdapter(adapter)

            editTextReportNickname.setText("멍멍이네")

            buttonReport.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}