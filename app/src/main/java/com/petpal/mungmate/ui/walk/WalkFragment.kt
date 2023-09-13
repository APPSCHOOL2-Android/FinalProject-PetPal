package com.petpal.mungmate.ui.walk

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentWalkBinding
import com.petpal.mungmate.model.KakaoSearchResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query



class WalkFragment : Fragment(),


    net.daum.mf.map.api.MapView.POIItemEventListener,
    net.daum.mf.map.api.MapView.CurrentLocationEventListener,
    net.daum.mf.map.api.MapView.MapViewEventListener {

    private lateinit var fragmentWalkBinding: FragmentWalkBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: WalkViewModel by viewModels { WalkViewModelFactory(WalkRepository()) }
    private lateinit var kakaoSearchResponse: KakaoSearchResponse
    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1
        const val API_KEY="29842459a2117277a50ac661f2f7e089"

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainActivity = activity as MainActivity
        fragmentWalkBinding = FragmentWalkBinding.inflate(inflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
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
        getCurrentLocationAndSearch()
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
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { response ->
            kakaoSearchResponse = response
            for ((index, place) in response.documents.withIndex()) {
                val mapPoint = MapPoint.mapPointWithGeoCoord(place.y, place.x)
                val marker = MapPOIItem().apply {
                    itemName = place.place_name
                    tag = index
                    this.mapPoint = mapPoint
                    markerType = MapPOIItem.MarkerType.CustomImage
                    customImageResourceId = R.drawable.paw_pin
                    isCustomImageAutoscale = true
                    setCustomImageAnchor(0.5f, 1.0f)
                }
                fragmentWalkBinding.mapView.addPOIItem(marker)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentLocationAndSearch() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                viewModel.searchPlacesByKeyword(it.latitude, it.longitude, "동물병원")
                val mapPoint = MapPoint.mapPointWithGeoCoord(it.latitude, it.longitude)
                fragmentWalkBinding.mapView.setMapCenterPoint(mapPoint, true)
            }
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
    }

    override fun onCurrentLocationUpdate(mapView: net.daum.mf.map.api.MapView?, mapPoint: MapPoint?, v: Float) {}

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {}

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {}

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {}
    override fun onPOIItemSelected(p0: net.daum.mf.map.api.MapView?, p1: MapPOIItem?) {
        val selectedPlace = kakaoSearchResponse.documents[p1?.tag ?: return]

        Log.d("WalkFragment", "onPOIItemSelected called")
        val initialBottomSheetView = layoutInflater.inflate(R.layout.row_walk_bottom_sheet_place, null)
        val bottomSheetDialog = BottomSheetDialog(requireActivity())
        bottomSheetDialog.setContentView(initialBottomSheetView)
        initialBottomSheetView.findViewById<TextView>(R.id.textView).text = selectedPlace.place_name
        bottomSheetDialog.show()

        bottomSheetDialog.behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        val fullyExpandedView = layoutInflater.inflate(R.layout.fragment_place_review, null)
                        bottomSheetDialog.setContentView(fullyExpandedView)
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        bottomSheetDialog.setContentView(initialBottomSheetView)
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

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
            val bundle = Bundle()
            bundle.putString("place_name", selectedPlace.place_name)
            bundle.putString("phone", selectedPlace.phone)
            bundle.putString("place_road_adress_name", selectedPlace.road_address_name)
            bundle.putString("place_category", selectedPlace.category_group_name)
            mainActivity.navigate(R.id.action_mainFragment_to_placeReviewFragment, bundle)
            bottomSheetDialog.dismiss()
        }
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: net.daum.mf.map.api.MapView?, p1: MapPOIItem?) {}

    override fun onCalloutBalloonOfPOIItemTouched(p0: net.daum.mf.map.api.MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?) {}

    override fun onDraggablePOIItemMoved(p0: net.daum.mf.map.api.MapView?, p1: MapPOIItem?, p2: MapPoint?) {}

    override fun onMapViewInitialized(p0: MapView?) {}

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
        fragmentWalkBinding.mapView.removeAllPOIItems()

        // 새로운 중심에서 검색 수행
        p1?.mapPointGeoCoord?.let {
            viewModel.searchPlacesByKeyword(it.latitude, it.longitude, "동물병원")
        }
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {}

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {}


}
class WalkViewModelFactory(private val repository: WalkRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WalkViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}