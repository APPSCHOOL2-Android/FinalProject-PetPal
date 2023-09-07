package com.petpal.mungmate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.petpal.mungmate.databinding.FragmentWalkBinding


class WalkFragment : Fragment() {
    lateinit var fragmentWalkBinding: FragmentWalkBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
       fragmentWalkBinding= FragmentWalkBinding.inflate(layoutInflater)
        fragmentWalkBinding.buttonWalk.setOnClickListener {
            val bottomSheetView = layoutInflater.inflate(R.layout.walk_bottom_sheet_place_layout, null)
            val bottomSheetDialog = BottomSheetDialog(requireActivity())
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
//            fragmentWalkBinding.LinearLayoutOffWalk.visibility=View.GONE
//            fragmentWalkBinding.LinearLayoutOnWalk.visibility=View.VISIBLE
        }

        fragmentWalkBinding.buttonStopWalk.setOnClickListener {
            fragmentWalkBinding.LinearLayoutOffWalk.visibility=View.VISIBLE
            fragmentWalkBinding.LinearLayoutOnWalk.visibility=View.GONE

        }

        val spinnerItems = listOf("거리", "1km", "2km")
        val spinnerItems2 = listOf("성별", "남자", "여자")
        val spinnerItems3 = listOf("나이대", "10대", "20대","30대")
        val spinnerItems4 = listOf("견종", "푸들", "시츄","보더콜리")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems2)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val adapter3 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems3)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val adapter4 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems4)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner: Spinner = fragmentWalkBinding.spinner
        val spinner2: Spinner = fragmentWalkBinding.spinner2
        val spinner3: Spinner = fragmentWalkBinding.spinner3
        val spinner4: Spinner = fragmentWalkBinding.spinner4

        spinner.adapter = adapter
        spinner2.adapter = adapter2
        spinner3.adapter = adapter3
        spinner4.adapter = adapter4



        return fragmentWalkBinding.root
    }

}