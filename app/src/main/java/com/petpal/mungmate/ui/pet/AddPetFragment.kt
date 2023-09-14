package com.petpal.mungmate.ui.pet

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentAddPetBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddPetFragment : Fragment() {
    private lateinit var _fragmentAddPetBinding: FragmentAddPetBinding
    private val fragmentAddPetBinding get() = _fragmentAddPetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentAddPetBinding = FragmentAddPetBinding.inflate(layoutInflater)

        val dogBreeds = resources.getStringArray(R.array.dog_breeds)
        //추가(true)인지 수정(false)인지 식별
        val isAdd = requireArguments().getBoolean("isAdd")

        fragmentAddPetBinding.run {

            //수정작업이면
            if(!isAdd) {
                //수정완료 아이콘 띄우기
                toolbarAddPet.inflateMenu(R.menu.complete_menu)
                //툴바 타이틀 반려견 정보 수정으로 변경
                toolbarAddPet.title = "반려견 정보 수정"
                //가입하기 버튼 삭제하기
                buttonPetComplete.visibility = View.GONE

                //성별 버튼 클릭 방지
                buttonHe.isClickable = false
                buttonShe.isClickable = false
            }

            ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, dogBreeds).also { adapter ->
                autoCompleteTextViewPetBreed.setAdapter(adapter)
            }


            textInputPetBirthText.setOnClickListener {
                // DatePicker 기본값 오늘로 설정
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("날짜 선택")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

                datePicker.addOnPositiveButtonClickListener { selectedDateInMillis ->
                    val selectDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(Date(selectedDateInMillis))
                    textInputPetBirthText.setText(selectDate)
                }
                datePicker.show(parentFragmentManager, "tag")
            }

            toolbarAddPet.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
        return fragmentAddPetBinding.root
    }

}