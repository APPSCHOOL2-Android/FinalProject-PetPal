package com.petpal.mungmate

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
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
            bottomSheetDialog.behavior.peekHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300f, resources.displayMetrics).toInt() // 300dp를 예로 들었습니다.
            bottomSheetDialog.show()

            bottomSheetView.findViewById<Button>(R.id.buttonSubmitReview).setOnClickListener {
                findNavController().navigate(R.id.action_item_walk_to_writePlaceReviewFragment)
                bottomSheetDialog.dismiss()
            }

            bottomSheetView.findViewById<TextView>(R.id.placeUserReview1).setOnClickListener {
                val detailCardView = layoutInflater.inflate(R.layout.row_place_review, null)
                val detailDialog = BottomSheetDialog(requireActivity())
                detailDialog.setContentView(detailCardView)

                detailDialog.show()
            }
            bottomSheetView.findViewById<Chip>(R.id.chipViewAllReviews).setOnClickListener {
                val navController = findNavController()
                navController.navigate(R.id.action_item_walk_to_placeReviewFragment3)
                bottomSheetDialog.dismiss()
            }
        }

        fragmentWalkBinding.chipMapFilter.setOnClickListener {

            fragmentWalkBinding.drawerLayout.setScrimColor(Color.parseColor("#FFFFFF"))
            fragmentWalkBinding.drawerLayout.openDrawer(GravityCompat.END);
        }
        fragmentWalkBinding.buttonFilterSubmit.setOnClickListener {
            fragmentWalkBinding.drawerLayout.closeDrawer(GravityCompat.END)
        }

        fragmentWalkBinding.buttonStopWalk.setOnClickListener {
            fragmentWalkBinding.LinearLayoutOffWalk.visibility = View.VISIBLE
            fragmentWalkBinding.LinearLayoutOnWalk.visibility = View.GONE

        }


        return fragmentWalkBinding.root
    }

}