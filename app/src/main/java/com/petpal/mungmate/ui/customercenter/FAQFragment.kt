package com.petpal.mungmate.ui.customercenter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentFAQBinding
import com.petpal.mungmate.model.FAQ

class FAQFragment : Fragment() {
    private var _fragmentFaqBinding: FragmentFAQBinding? = null
    private val fragmentFAQBinding get() = _fragmentFaqBinding!!

    private lateinit var faqAdapter: FAQAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentFaqBinding = FragmentFAQBinding.inflate(inflater)
        return fragmentFAQBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentFAQBinding.run {
            toolbarFaq.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            recyclerViewFaq.run {
                faqAdapter = FAQAdapter(getSampleData())
                adapter = faqAdapter
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    MaterialDividerItemDecoration(
                        context,
                        MaterialDividerItemDecoration.VERTICAL
                    )
                )
                itemAnimator = null
            }

            buttonInquiry.setOnClickListener {
                findNavController().navigate(R.id.action_FAQFragment_to_contactUsFragment)
            }

            chipGroupFaqCategory.setOnCheckedStateChangeListener { group, checkedIds ->
                changeFAQCategoryFilter(checkedIds.first())
            }
        }
    }

    private fun changeFAQCategoryFilter(checkedId: Int) {
        val selectedChip = fragmentFAQBinding.root.findViewById<Chip>(checkedId)
        if (selectedChip != null) {
            val faqCategory = selectedChip.text.toString()
            faqAdapter.filter.filter(faqCategory)
        }
    }

    private fun getSampleData(): List<FAQ> {
        return listOf(
            FAQ("포인트", "포인트는 어떻게 얻나요?", "포인트가 적립되는 경우는 아래와 같습니다.\n\n- 혼자 산책 : 10포인트\n- 같이 산책 : 20포인트\n- 후기 5점 만점 보너스 : 5포인트\n- 일주일 연속 산책 : 50포인트"),
            FAQ("포인트", "포인트 적립이 누락되었어요.", "1:1문의로 와주시면 적립 도와드리겠습니다."),
            FAQ("회원정보", "반려견 정보를 잘못 입력했어요", "[마이페이지>반려견관리>반려견 정보 수정] 에서 수정하실 수 있습니다."),
            FAQ("포인트", "포인트는 어떻게 얻나요?", "포인트가 적립되는 경우는 아래와 같습니다.\n\n- 혼자 산책 : 10포인트\n- 같이 산책 : 20포인트\n- 후기 5점 만점 보너스 : 5포인트\n- 일주일 연속 산책 : 50포인트"),
            FAQ("포인트", "포인트 적립이 누락되었어요.", "1:1문의로 와주시면 적립 도와드리겠습니다."),
            FAQ("회원정보", "반려견 정보를 잘못 입력했어요", "[마이페이지>반려견관리>반려견 정보 수정] 에서 수정하실 수 있습니다."),
            FAQ("포인트", "포인트는 어떻게 얻나요?", "포인트가 적립되는 경우는 아래와 같습니다.\n\n- 혼자 산책 : 10포인트\n- 같이 산책 : 20포인트\n- 후기 5점 만점 보너스 : 5포인트\n- 일주일 연속 산책 : 50포인트"),
            FAQ("포인트", "포인트 적립이 누락되었어요.", "1:1문의로 와주시면 적립 도와드리겠습니다."),
            FAQ("회원정보", "반려견 정보를 잘못 입력했어요", "[마이페이지>반려견관리>반려견 정보 수정] 에서 수정하실 수 있습니다."),
            FAQ("포인트", "포인트는 어떻게 얻나요?", "포인트가 적립되는 경우는 아래와 같습니다.\n\n- 혼자 산책 : 10포인트\n- 같이 산책 : 20포인트\n- 후기 5점 만점 보너스 : 5포인트\n- 일주일 연속 산책 : 50포인트"),
            FAQ("포인트", "포인트 적립이 누락되었어요.", "1:1문의로 와주시면 적립 도와드리겠습니다."),
            FAQ("회원정보", "반려견 정보를 잘못 입력했어요", "[마이페이지>반려견관리>반려견 정보 수정] 에서 수정하실 수 있습니다.")
        )
    }

}