package com.petpal.mungmate.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.petpal.mungmate.databinding.FragmentReportUserBinding
import com.petpal.mungmate.model.UserReport

class ReportUserFragment : Fragment() {

    private var _fragmentReportUserBinding :FragmentReportUserBinding? = null
    private val fragmentReportUserBinding get() = _fragmentReportUserBinding!!

    private lateinit var viewModel: ChatRoomViewModel

    private val reportCategoryArray = arrayOf("홍보 계정이에요", "욕설을 해요", "약속 시간에 나오지 않았어요", "비매너 유저", "기타")

    lateinit var reportedUserId : String
    lateinit var reportedUserNickname: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ChatRoomViewModel::class.java]

        // Safe Args 방법으로 전달받은 신고 대상 유저 id
        val args = ReportUserFragmentArgs.fromBundle(requireArguments())
        reportedUserId = args.reportedUserId
        reportedUserNickname = args.reportedUserNickname
    }

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
            editTextReportNickname.setText(reportedUserNickname)
            
            toolbarReportUser.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, reportCategoryArray)
            textViewReportCategory.setAdapter(adapter)

            buttonReportUser.setOnClickListener {
                reportUser()
                findNavController().popBackStack()
            }
        }
    }

    // 사용자 신고
    private fun reportUser() {
        // 신고 카테고리, 내용
        val reportCategory = fragmentReportUserBinding.textViewReportCategory.text.toString()
        val reportContent = fragmentReportUserBinding.editTextReportContent.text.toString()

        val userReport = UserReport(reportedUserId, reportCategory, reportContent, Timestamp.now())

        viewModel.saveReport(userReport)
        Snackbar.make(requireView(), "사용자 신고가 접수되었습니다.", Snackbar.LENGTH_SHORT).show()
    }
}