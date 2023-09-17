package com.petpal.mungmate.ui.walk

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentWalkBinding
import com.petpal.mungmate.model.Favorite
import com.petpal.mungmate.model.KakaoSearchResponse
import com.petpal.mungmate.model.Place
import com.petpal.mungmate.utils.LastKnownLocation
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
class WalkFragment : Fragment(),


    net.daum.mf.map.api.MapView.POIItemEventListener,
    net.daum.mf.map.api.MapView.CurrentLocationEventListener,
    net.daum.mf.map.api.MapView.MapViewEventListener {

    private lateinit var fragmentWalkBinding: FragmentWalkBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: WalkViewModel by viewModels { WalkViewModelFactory(WalkRepository()) }
    private lateinit var kakaoSearchResponse: KakaoSearchResponse
    private var isLocationPermissionGranted = false
    private var isFavorited = false

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        fragmentWalkBinding = FragmentWalkBinding.inflate(inflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
        Log.d("LocationCheck", "oncreate")
        if (LastKnownLocation.latitude == null) {
            Log.d("LocationCheck", "lastKnownLocation is null")
        } else {
            Log.d(
                "LocationCheck",
                "lastKnownLocation is not null: lat=${LastKnownLocation.latitude}, lon=${LastKnownLocation.longitude}"
            )
        }

        setupMapView()
        setupButtonListeners()
        observeViewModel()

        return fragmentWalkBinding.root
    }
    private fun setupMapView() {
        //필터 드로어 제어
        fragmentWalkBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        fragmentWalkBinding.mapView.setPOIItemEventListener(this)
        fragmentWalkBinding.mapView.setCurrentLocationEventListener(this)
        fragmentWalkBinding.mapView.setMapViewEventListener(this)

        requestLocationPermissionIfNeeded()
    }

    private fun setupButtonListeners() {
        fragmentWalkBinding.buttonWalk.setOnClickListener {
            fragmentWalkBinding.LinearLayoutOnWalk.visibility = View.VISIBLE
            fragmentWalkBinding.LinearLayoutOffWalk.visibility = View.GONE
            fragmentWalkBinding.imageViewWalkToggle.setImageResource(R.drawable.dog_walk)
        }


        fragmentWalkBinding.chipMapFilter.setOnClickListener {
            fragmentWalkBinding.drawerLayout.setScrimColor(Color.parseColor("#FFFFFF"))
            fragmentWalkBinding.drawerLayout.openDrawer(GravityCompat.END)
        }

        fragmentWalkBinding.buttonFilterSubmit.setOnClickListener {
            fragmentWalkBinding.drawerLayout.closeDrawer(GravityCompat.END)
        }

        fragmentWalkBinding.buttonStopWalk.setOnClickListener {
            fragmentWalkBinding.LinearLayoutOffWalk.visibility = View.VISIBLE
            fragmentWalkBinding.LinearLayoutOnWalk.visibility = View.GONE
            fragmentWalkBinding.imageViewWalkToggle.setImageResource(R.drawable.dog_home)
        }
        fragmentWalkBinding.imageViewMylocation.setOnClickListener {
            getCurrentLocation()
            LastKnownLocation.latitude = null
            LastKnownLocation.longitude= null
        }
    }

    private val currentMarkers: MutableList<MapPOIItem> = mutableListOf()
    private fun observeViewModel() {

        viewModel.searchResults.observe(viewLifecycleOwner) { response ->
            //검색 결과로 씀
            kakaoSearchResponse = response
            Log.d("sizeee", (kakaoSearchResponse.documents.size).toString())
            val newMarkers = kakaoSearchResponse.documents.map { place ->
                val mapPoint = MapPoint.mapPointWithGeoCoord(place.y, place.x)
                MapPOIItem().apply {
                    itemName = place.place_name
                    tag = place.id.hashCode() //id로 태그
                    this.mapPoint = mapPoint
                    markerType = MapPOIItem.MarkerType.CustomImage
                    customImageResourceId = R.drawable.paw_pin
                    //마커 크기 자동조정
                    isCustomImageAutoscale = true
                    //마커 위치 조정
                    setCustomImageAnchor(0.5f, 1.0f)
                }
            }

            //기존 마커 중 새로운 검색 결과에 없는 것은 지도에서 제거하고 currentMarkers에서도 제거
            val markersToRemove = currentMarkers.filter { marker ->
                newMarkers.none { it.tag == marker.tag }
            }
            for (marker in markersToRemove) {
                fragmentWalkBinding.mapView.removePOIItem(marker)
                currentMarkers.remove(marker)
            }

            //새로운 검색 결과 중 현재 지도에 없는 마커들을 추가
            val markersToAdd = newMarkers.filter { marker ->
                currentMarkers.none { it.tag == marker.tag }
            }
            for (marker in markersToAdd) {
                fragmentWalkBinding.mapView.addPOIItem(marker)
                currentMarkers.add(marker)
            }
        }
    }

    private fun requestLocationPermissionIfNeeded() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            // 권한이 승인되어있으면 위치 가져오기
            isLocationPermissionGranted = true
            Log.d("LastKnownLocation",LastKnownLocation.latitude.toString())
            if (LastKnownLocation.latitude != null || LastKnownLocation.longitude != null){
                getLastLocation()
            } else {
                getCurrentLocation()
            }
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    LastKnownLocation.latitude=it.latitude
                    LastKnownLocation.longitude=it.longitude
                    viewModel.searchPlacesByKeyword(it.latitude, it.longitude, "동물")
                    val mapPoint = MapPoint.mapPointWithGeoCoord(it.latitude, it.longitude)
                    fragmentWalkBinding.mapView.setMapCenterPoint(mapPoint, true)
                }
            }
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            LastKnownLocation.let {
                LastKnownLocation.latitude?.let { it1 ->
                    LastKnownLocation.longitude?.let { it2 ->
                        viewModel.searchPlacesByKeyword(it1, it2, "동물")
                    }
                }
                val mapPoint = LastKnownLocation.latitude?.let { it1 ->
                    LastKnownLocation.longitude?.let { it2 ->
                        MapPoint.mapPointWithGeoCoord(it1, it2)
                    }
                }
                fragmentWalkBinding.mapView.setMapCenterPoint(mapPoint, true)

                // Clear currentMarkers and remove all POI items from the map
                for (marker in currentMarkers) {
                    fragmentWalkBinding.mapView.removePOIItem(marker)
                }
                currentMarkers.clear()
            }
        }
    }

    //권한 핸들링
    //앱 초기 실행시 권한 부여 여부 결정 전에 위치를 받아올 수 없는 현상 핸들링
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 사용자가 위치 권한을 승인한 경우
                    isLocationPermissionGranted = true
                    getCurrentLocation()
                } else {
                    //이런 느낌? 근데 스낵바 띄우고 위치정보를 가져오지 못한채로 기본값(서울시청 기준)을 보여주는게 맞는건가? 아니면 다시 권한을 요청해야하나?
                    showSnackbar("현재 위치를 확인하려면 위치 권한이 필요합니다. 설정에서 권한을 허용해주세요.")
                }
                return
            }
            //다른 권한 필요하면 ㄱ
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(fragmentWalkBinding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onCurrentLocationUpdate(
        mapView: net.daum.mf.map.api.MapView?,
        mapPoint: MapPoint?,
        v: Float
    ) {
    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {}

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {}

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {}
    override fun onPOIItemSelected(p0: net.daum.mf.map.api.MapView?, p1: MapPOIItem?) {
        val selectedPlace = kakaoSearchResponse.documents.find { it.id.hashCode() == p1?.tag }
        val initialBottomSheetView =
            layoutInflater.inflate(R.layout.row_walk_bottom_sheet_place, null)

        val placeId = selectedPlace?.id
        placeId?.let {
            viewModel.fetchFavoriteCount(it)
            viewModel.fetchReviewCount(it)
            viewModel.fetchLatestReviewsForPlace(it)
            viewModel.fetchPlaceInfoFromFirestore(it)
            viewModel.fetchIsPlaceFavoritedByUser(it, "userid")
        }
        viewModel.isPlaceFavorited.observe(viewLifecycleOwner) { isPlaceFavorited ->
            if (isPlaceFavorited) {
                isFavorited = true
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteCount.collect { count ->
                val favoriteCountTextView =
                    initialBottomSheetView.findViewById<TextView>(R.id.textViewPlaceFavoriteCount)
                favoriteCountTextView.text = "${count}명의 유저가 추천합니다."
            }
        }


        viewModel.placeInfo.observe(viewLifecycleOwner) { placeInfo ->
            val textViewPlaceName = initialBottomSheetView.findViewById<TextView>(R.id.textView)
            textViewPlaceName.text =
                placeInfo?.get("name") as? String ?: "${selectedPlace?.place_name}"

        }


        viewModel.reviewCount.observe(viewLifecycleOwner) { reviewCount ->
            val reviewCountTextView =
                initialBottomSheetView.findViewById<TextView>(R.id.textViewPlaceReviewCount)
            reviewCountTextView.text = "${reviewCount}개의 리뷰가 있어요"
        }

        viewModel.latestReviews.observe(viewLifecycleOwner) { reviews ->
            val placeuserRating1 =
                initialBottomSheetView.findViewById<RatingBar>(R.id.placeUserRatingBar1)
            val placeuserRating2 =
                initialBottomSheetView.findViewById<RatingBar>(R.id.placeUserRatingBar2)
            val placeuserReview1 =
                initialBottomSheetView.findViewById<TextView>(R.id.placeUserReview1)
            val placeuserReview2 =
                initialBottomSheetView.findViewById<TextView>(R.id.placeUserReview2)

            if (reviews.isNotEmpty()) {
                val firstReview = reviews[0]
                placeuserRating1.rating = firstReview.rating!!
                placeuserReview1.text = firstReview.comment

                if (reviews.size > 1) {
                    val secondReview = reviews[1]
                    placeuserRating2.rating = secondReview.rating!!
                    placeuserReview2.text = secondReview.comment
                }
            } else {
                placeuserRating1.visibility = View.GONE
                placeuserRating2.visibility = View.GONE
                placeuserReview1.visibility = View.GONE
                placeuserReview2.visibility = View.GONE
                initialBottomSheetView.findViewById<Chip>(R.id.chipViewAllReviews).visibility =
                    View.GONE
                initialBottomSheetView.findViewById<TextView>(R.id.textViewNoReview).visibility =
                    View.VISIBLE
            }
        }


        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(initialBottomSheetView)

        val buttonsubmit = initialBottomSheetView.findViewById<Button>(R.id.buttonSubmitReview)


        Handler(Looper.getMainLooper()).postDelayed({
            bottomSheetDialog.show()
        }, 300)  // 0.5초 딜레이

        bottomSheetDialog.behavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        val bundle = Bundle()
                        bundle.putString("place_name", selectedPlace?.place_name)
                        bundle.putString("phone", selectedPlace?.phone)
                        bundle.putString("place_id", selectedPlace?.id)
                        bundle.putString("place_road_adress_name", selectedPlace?.road_address_name)
                        bundle.putString("place_category", selectedPlace?.category_group_name)
                        mainActivity.navigate(
                            R.id.action_mainFragment_to_placeReviewFragment,
                            bundle,
                        )
                        bottomSheetDialog.dismiss()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        initialBottomSheetView.findViewById<ImageView>(R.id.imageViewFavoirte).apply {
            val place = Place(
                selectedPlace!!.id,
                selectedPlace.place_name,
                selectedPlace.category_group_name,
                (selectedPlace.x).toString(),
                (selectedPlace.y).toString(),
                selectedPlace.phone,
                selectedPlace.road_address_name
            )
            setOnClickListener {
                if (!isFavorited) {
                    isFavorited = true
                    val favorite = Favorite("userid")
                    viewModel.addPlaceToFavorite(place, favorite)
                    // 좋아요가 눌릴 때마다 텍스트뷰 업데이트
                } else {
                    isFavorited = false
                    viewModel.removeFavorite(placeId!!, "userid")

                }

                // 좋아요 개수 반영 ?
                val alertDialog = AlertDialog.Builder(context)
                    .setTitle("멍메이트")
                    .setMessage("반영되었습니다.")
                    .setPositiveButton("확인") { dialog, which ->
                        // 확인 버튼 클릭 시 수행될 로직
                        dialog.dismiss()
                    }
                    .create()

                alertDialog.show()

                viewModel.fetchFavoriteCount(placeId!!)
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    viewModel.favoriteCount.collect { favoriteCount ->
                        Log.d("firstsubmit", favoriteCount.toString())
                        val favoriteCountTextView =
                            initialBottomSheetView.findViewById<TextView>(R.id.textViewPlaceFavoriteCount)
                        favoriteCountTextView.text = "${favoriteCount}명의 유저가 추천합니다."
                    }
                }

            }
        }

        buttonsubmit.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("place_name", selectedPlace?.place_name)
            bundle.putString("place_id", selectedPlace?.id)
            bundle.putString("place_category", selectedPlace?.category_group_name)
            bundle.putString("place_long", selectedPlace?.x.toString())
            bundle.putString("place_lat", selectedPlace?.y.toString())
            bundle.putString("phone", selectedPlace?.phone)
            bundle.putString("place_road_adress_name", selectedPlace?.road_address_name)

            mainActivity.navigate(R.id.action_mainFragment_to_writePlaceReviewFragment, bundle)
            bottomSheetDialog.dismiss()
        }

        initialBottomSheetView.findViewById<TextView>(R.id.placeUserReview1).setOnClickListener {
            val detailCardView = layoutInflater.inflate(R.layout.row_place_review, null)
            val detailDialog = BottomSheetDialog(requireActivity())
            detailDialog.setContentView(detailCardView)
            detailDialog.show()
        }

        initialBottomSheetView.findViewById<Chip>(R.id.chipViewAllReviews).setOnClickListener {
            val bundle = Bundle()
            bundle.putString("place_name", selectedPlace?.place_name)
            bundle.putString("place_id", selectedPlace?.id)
            bundle.putString("phone", selectedPlace?.phone)
            bundle.putString("place_road_adress_name", selectedPlace?.road_address_name)
            bundle.putString("place_category", selectedPlace?.category_group_name)
            mainActivity.navigate(
                R.id.action_mainFragment_to_placeReviewFragment,
                bundle,
            )
            bottomSheetDialog.dismiss()
        }
    }


    @Deprecated("Deprecated in Java")
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

    override fun onMapViewInitialized(p0: MapView?) {}
    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
        // 새로운 중심에서 검색
        p1?.mapPointGeoCoord?.let {
            LastKnownLocation.latitude = it.latitude
            LastKnownLocation.longitude = it.longitude
            viewModel.searchPlacesByKeyword(it.latitude, it.longitude, "동물")

            Log.d("lastlocation", (LastKnownLocation.latitude).toString())
        }
    }

    //줌 아웃 제한
    override fun onMapViewZoomLevelChanged(p0: MapView?, zoomLevel: Int) {
        val maxZoomLevel = 2
        if (zoomLevel > maxZoomLevel) {
            p0?.setZoomLevel(maxZoomLevel, true)
        }
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {}

    override fun onResume() {
        super.onResume()
        requestLocationPermissionIfNeeded()
    }

}


//뷰 모델 팩토리
class WalkViewModelFactory(private val repository: WalkRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WalkViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}