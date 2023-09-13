package com.petpal.mungmate.ui.pet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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



        fragmentAddPetBinding.run {

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
        }
        return fragmentAddPetBinding.root
    }

}