package com.petpal.mungmate

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
            val bottomSheetView = layoutInflater.inflate(R.layout.row_walk_bottom_sheet_place, null)
            val bottomSheetDialog = BottomSheetDialog(requireActivity())
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
//            fragmentWalkBinding.LinearLayoutOffWalk.visibility=View.GONE
//            fragmentWalkBinding.LinearLayoutOnWalk.visibility=View.VISIBLE
        }
        fragmentWalkBinding.chipMapFilter.setOnClickListener {

            fragmentWalkBinding.drawerLayout.setScrimColor(Color.parseColor("#FFFFFF"))
            fragmentWalkBinding.drawerLayout.openDrawer(GravityCompat.END);
        }

        fragmentWalkBinding.buttonStopWalk.setOnClickListener {
            fragmentWalkBinding.LinearLayoutOffWalk.visibility=View.VISIBLE
            fragmentWalkBinding.LinearLayoutOnWalk.visibility=View.GONE

        }


        return fragmentWalkBinding.root
    }

}