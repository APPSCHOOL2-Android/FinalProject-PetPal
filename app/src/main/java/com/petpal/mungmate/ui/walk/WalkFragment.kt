package com.petpal.mungmate.ui.walk

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentWalkBinding
import net.daum.android.map.MapView
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint


class WalkFragment : Fragment(), net.daum.mf.map.api.MapView.POIItemEventListener {
    lateinit var fragmentWalkBinding: FragmentWalkBinding
    lateinit var mainActivity: MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mainActivity=activity as MainActivity
        fragmentWalkBinding= FragmentWalkBinding.inflate(layoutInflater)

        val mapView = fragmentWalkBinding.mapView
        val mapPoint = MapPoint.mapPointWithGeoCoord(37.56647, 126.977963)

        // Marker 객체 생성 및 설정
        val marker = MapPOIItem().apply {
            itemName = "서울시청"
            tag = 0
            this.mapPoint = mapPoint
            markerType = MapPOIItem.MarkerType.BluePin // 기본 마커
            selectedMarkerType = MapPOIItem.MarkerType.RedPin // 마커 클릭 시 사용할 타입
        }
        mapView.addPOIItem(marker)
        mapView.setPOIItemEventListener(this)


        fragmentWalkBinding.buttonWalk.setOnClickListener {
            fragmentWalkBinding.LinearLayoutOnWalk.visibility=View.VISIBLE
            fragmentWalkBinding.LinearLayoutOffWalk.visibility=View.GONE
            fragmentWalkBinding.imageViewWalkToggle.setImageResource(R.drawable.dog_walk)
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
            fragmentWalkBinding.imageViewWalkToggle.setImageResource(R.drawable.dog_home)

        }


        return fragmentWalkBinding.root
    }

    override fun onPOIItemSelected(p0: net.daum.mf.map.api.MapView?, p1: MapPOIItem?) {
        val bottomSheetView = layoutInflater.inflate(R.layout.row_walk_bottom_sheet_place, null)
        val bottomSheetDialog = BottomSheetDialog(requireActivity())
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.behavior.peekHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300f, resources.displayMetrics).toInt() // 300dp를 예로 들었습니다.
        bottomSheetDialog.show()

        bottomSheetView.findViewById<Button>(R.id.buttonSubmitReview).setOnClickListener {
            mainActivity.navigate(R.id.action_mainFragment_to_writePlaceReviewFragment)
            bottomSheetDialog.dismiss()
        }

        bottomSheetView.findViewById<TextView>(R.id.placeUserReview1).setOnClickListener {
            val detailCardView = layoutInflater.inflate(R.layout.row_place_review, null)
            val detailDialog = BottomSheetDialog(requireActivity())
            detailDialog.setContentView(detailCardView)

            detailDialog.show()
        }
        bottomSheetView.findViewById<Chip>(R.id.chipViewAllReviews).setOnClickListener {
            mainActivity.navigate(R.id.action_mainFragment_to_placeReviewFragment)
            bottomSheetDialog.dismiss()
        }
    }

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: net.daum.mf.map.api.MapView?,
        p1: MapPOIItem?
    ) {
        TODO("Not yet implemented")
    }

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: net.daum.mf.map.api.MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
        TODO("Not yet implemented")
    }

    override fun onDraggablePOIItemMoved(
        p0: net.daum.mf.map.api.MapView?,
        p1: MapPOIItem?,
        p2: MapPoint?
    ) {
        TODO("Not yet implemented")
    }

}