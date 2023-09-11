package com.petpal.mungmate.ui.customercenter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentInquiryBinding
import com.petpal.mungmate.model.Inquiry

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

            recyclerViewInquiry.run {
                adapter = InquiryAdapter(getSampleData())
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    MaterialDividerItemDecoration(
                        context,
                        MaterialDividerItemDecoration.VERTICAL
                    )
                )
                itemAnimator = null
            }

            buttonContactUs.setOnClickListener {
                findNavController().navigate(R.id.action_inquiryFragment_to_contactUsFragment)
            }
        }
    }

    private fun getSampleData(): List<Inquiry> {
        return listOf(
            Inquiry("포인트", "포인트가 누락됐어요", "어제 산책 다 끝냈는데 포인트가 안들어왔어요ㅠㅠ", "100 포인트 적립 도와드렸습니다. 이용에 불편을 드려서 죄송합니다.", "aaa", "2023.09.10 10:04", false),
            Inquiry("포인트", "포인트가 누락됐어요", "어제 산책 다 끝냈는데 포인트가 안들어왔어요ㅠㅠ", "100 포인트 적립 도와드렸습니다. 이용에 불편을 드려서 죄송합니다.", "aaa", "2023.09.10 10:04", true)
        )

    }

}