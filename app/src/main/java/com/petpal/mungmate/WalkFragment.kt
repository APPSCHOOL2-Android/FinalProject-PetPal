package com.petpal.mungmate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        return fragmentWalkBinding.root
    }

}