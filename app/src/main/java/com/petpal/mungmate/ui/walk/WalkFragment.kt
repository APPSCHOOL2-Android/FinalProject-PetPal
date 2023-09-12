package com.petpal.mungmate.ui.walk

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentWalkBinding
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
            markerType = MapPOIItem.MarkerType.CustomImage
            customImageResourceId = R.drawable.petplace_pin
            // 마커 크기 자동 조절 활성화
            isCustomImageAutoscale = true
            // 앵커 포인트 설정 (중앙 아래로 설정)
            setCustomImageAnchor(0.5f, 1.0f)
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
        // 초기 상태의 바텀시트 레이아웃을 설정합니다.
        val initialBottomSheetView = layoutInflater.inflate(R.layout.row_walk_bottom_sheet_place, null)
        val bottomSheetDialog = BottomSheetDialog(requireActivity())
        bottomSheetDialog.setContentView(initialBottomSheetView)

        // 바텀시트의 상태 변화를 감지하는 콜백을 설정합니다.
        bottomSheetDialog.behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // 완전히 펼친 상태의 레이아웃으로 변경
                        val fullyExpandedView = layoutInflater.inflate(R.layout.fragment_place_review, null)
                        // fully_expanded_layout은 새로운 레이아웃을 나타냅니다.
                        bottomSheetDialog.setContentView(fullyExpandedView)
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        // 축소된 상태의 레이아웃으로 변경 (원하시는 대로 변경 가능)
                        bottomSheetDialog.setContentView(initialBottomSheetView)
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 여기는 슬라이드 될 때마다 호출되는 부분입니다. 필요에 따라 구현할 수 있습니다.
            }
        })

        // 기타 초기 바텀시트 뷰와 관련된 로직 (기존 코드에서 가져옴)
        initialBottomSheetView.findViewById<Button>(R.id.buttonSubmitReview).setOnClickListener {
            mainActivity.navigate(R.id.action_mainFragment_to_writePlaceReviewFragment)
            bottomSheetDialog.dismiss()
        }

        initialBottomSheetView.findViewById<TextView>(R.id.placeUserReview1).setOnClickListener {
            val detailCardView = layoutInflater.inflate(R.layout.row_place_review, null)
            val detailDialog = BottomSheetDialog(requireActivity())
            detailDialog.setContentView(detailCardView)
            detailDialog.show()
        }

        initialBottomSheetView.findViewById<Chip>(R.id.chipViewAllReviews).setOnClickListener {
            mainActivity.navigate(R.id.action_mainFragment_to_placeReviewFragment)
            bottomSheetDialog.dismiss()
        }

        // 바텀시트를 보여줍니다.
        bottomSheetDialog.show()

        initialBottomSheetView.findViewById<Button>(R.id.buttonSubmitReview).setOnClickListener {
            mainActivity.navigate(R.id.action_mainFragment_to_writePlaceReviewFragment)
            bottomSheetDialog.dismiss()
        }

        initialBottomSheetView.findViewById<TextView>(R.id.placeUserReview1).setOnClickListener {
            val detailCardView = layoutInflater.inflate(R.layout.row_place_review, null)
            val detailDialog = BottomSheetDialog(requireActivity())
            detailDialog.setContentView(detailCardView)

            detailDialog.show()
        }
        initialBottomSheetView.findViewById<Chip>(R.id.chipViewAllReviews).setOnClickListener {
            mainActivity.navigate(R.id.action_mainFragment_to_placeReviewFragment)
            bottomSheetDialog.dismiss()
        }
    }

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: net.daum.mf.map.api.MapView?,
        p1: MapPOIItem?
    ) {

    }

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: net.daum.mf.map.api.MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
    }

    override fun onDraggablePOIItemMoved(
        p0: net.daum.mf.map.api.MapView?,
        p1: MapPOIItem?,
        p2: MapPoint?
    ) {
    }

}