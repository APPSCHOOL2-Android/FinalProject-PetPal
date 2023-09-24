package com.petpal.mungmate.ui.walk

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils.formatElapsedTime
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.petpal.mungmate.BlockingDialogFragment
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentWalkBinding
import com.petpal.mungmate.model.Favorite
import com.petpal.mungmate.model.KakaoSearchResponse
import com.petpal.mungmate.model.Match
import com.petpal.mungmate.model.PlaceData
import com.petpal.mungmate.model.ReceiveUser
import com.petpal.mungmate.model.Review
import com.petpal.mungmate.utils.LastKnownLocation
import com.petpal.mungmate.utils.observeOnce
import com.petpal.mungmate.utils.onWalk.onWalk
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapPolyline
import net.daum.mf.map.api.MapView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class WalkFragment : Fragment(), net.daum.mf.map.api.MapView.POIItemEventListener, net.daum.mf.map.api.MapView.CurrentLocationEventListener, net.daum.mf.map.api.MapView.MapViewEventListener {

    private lateinit var fragmentWalkBinding: FragmentWalkBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: WalkViewModel by activityViewModels {
        WalkViewModelFactory(WalkRepository(), requireActivity().application)
    }
    // 거리 필터링을 위한 코드
    private var distanceFilterValue:Double?=null

    private var location123:Location?=null
    private var repeatJob: Job? = null
    val CURRENT_LOCATION_MARKER_TAG = 100
    private lateinit var kakaoSearchResponse: KakaoSearchResponse
    private var isLocationPermissionGranted = false
    private var isFavorited1 = false
    private var latestReviews: List<Review>? = null
    private val currentMarkers: MutableList<MapPOIItem> = mutableListOf()
    private val avgRatingBundle = Bundle()
    private lateinit var initialBottomSheetView: View
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var onWalkBottomSheetView: View
    private lateinit var onWalkbottomSheetDialog: BottomSheetDialog
    private val auth = Firebase.auth
    private lateinit var userId:String
    private val locationList = mutableListOf<Location>()
    private lateinit var userNickname:String
    private var walkMateNickname: String? = null
    private var lastLocation: Location? = null
    private var totalDistance = 0.0f
    private var elapsedTime = 0L
    private var userLocationMarker: MapPOIItem? = null
    private var walkWithUser:Match?=null
    private var nearbyUsers: List<ReceiveUser>?=null
    private val countDownInterval = 1000
    private var countDownTimer: CountDownTimer? = null
    private var countdownValue = 3
    private var savedUri: Uri? = null
    var matches1: MutableList<Match> = mutableListOf()
    val storage = Firebase.storage
    private val userMarkers = HashMap<String, MapPOIItem>()
    private var observeJob: Job? = null
    //val storageReference = storage.reference
    private val handler = Handler()
    private val delayMillis: Long = 2* 1000 // 1분 (1분 = 60,000 밀리초)

    private val periodicRunnable: Runnable = object : Runnable {
        override fun run() {
            // 여기에 주기적으로 실행하고자 하는 함수를 호출
            getCurrentLocationOnWalk2()
            // 다음에도 호출되도록 다시 예약
            handler.postDelayed(this, delayMillis)
        }
    }


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

        initialBottomSheetView = inflater.inflate(R.layout.row_walk_bottom_sheet_place, null)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(initialBottomSheetView)

        onWalkBottomSheetView=inflater.inflate(R.layout.row_walk_bottom_sheet_user,null)
        onWalkbottomSheetDialog=BottomSheetDialog(requireContext())
        onWalkbottomSheetDialog.setContentView(onWalkBottomSheetView)
        setupMapView()
        setupButtonListeners()
        val user=auth.currentUser
        userId= user?.uid.toString()
        viewModel.fetchMatchesByUserId(userId)
        if(onWalk==false){
            viewModel.updateOnWalkStatusFalse(userId)
        }

        viewModel.fetchUserNickname(userId)


        viewModel.userNickname.observe(viewLifecycleOwner){
            userNickname=it!!
        }

        return fragmentWalkBinding.root
    }

    private fun toggleVisibility(viewToShow: View, viewToHide: View) {
        viewToShow.visibility = View.VISIBLE
        viewToHide.visibility = View.GONE
    }
    private fun setupMapView() {
        //필터 드로어 제어
        //fragmentWalkBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        fragmentWalkBinding.mapView.setPOIItemEventListener(this)
        fragmentWalkBinding.mapView.setCurrentLocationEventListener(this)
        fragmentWalkBinding.mapView.setMapViewEventListener(this)
        requestLocationPermissionIfNeeded()

    }
    fun setupAndShowDialog(builder: AlertDialog.Builder) {
        builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
            showProgress()
            startCountdown()
            onWalk = true
            startLocationUpdates()
            observeViewModelonWalk()
            viewModel.observeUsersOnWalk()

            fragmentWalkBinding.mapView.removeAllPOIItems()
            updateCurrentLocationOnce()
        }
        builder.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
    private fun setupButtonListeners() {
        fragmentWalkBinding.buttonWalk.setOnClickListener {
            walkWithUser?.walkRecordId?.let { it1 -> viewModel.updateMatchStatusToFour(it1) }
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("멍메이트")

            if (walkWithUser != null) {
                val targetUserId = if (userId == walkWithUser!!.senderId) {
                    walkWithUser!!.receiverId
                } else {
                    walkWithUser!!.senderId
                }

                targetUserId?.let { it1 ->
                    viewModel.fetchUserByUserId(it1)
                    viewModel.userLiveData.observeOnce(viewLifecycleOwner, Observer { user ->
                        walkMateNickname=user?.nickname
                        builder.setMessage("${user!!.nickname} 님과의 산책을 시작하시겠습니까?")

                        // 여기서 다이얼로그를 설정하고 표시합니다.
                        setupAndShowDialog(builder)
                    })
                }
            } else {
                builder.setMessage("산책을 시작하시겠습니까?")
                setupAndShowDialog(builder)
            }
        }

        fragmentWalkBinding.chipMapFilter.setOnClickListener {
            fragmentWalkBinding.drawerLayout.setScrimColor(Color.parseColor("#FFFFFF"))
            fragmentWalkBinding.drawerLayout.openDrawer(GravityCompat.END)
            //fragmentWalkBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN)
        }

        fragmentWalkBinding.buttonFilterSubmit.setOnClickListener {
            val isAnyChipSelected = listOf(
                fragmentWalkBinding.filterDistanceGroup,
                fragmentWalkBinding.filterUserGenderGroup,
                fragmentWalkBinding.filterAgeRangeGroup,
                fragmentWalkBinding.filterPetGenderGroup,
                fragmentWalkBinding.filterPetPropensityGroup,
                fragmentWalkBinding.filterNeuterStatusGroup
            ).any { it.checkedChipId != View.NO_ID }

            if (isAnyChipSelected) {
                showSnackbar("필터가 적용되었습니다.")
                when(fragmentWalkBinding.filterDistanceGroup.checkedChipId) {
                    R.id.distance1 -> distanceFilterValue = 1000.0
                    R.id.distance2 -> distanceFilterValue = 2000.0
                    R.id.distance3 -> distanceFilterValue = 3000.0
                    else -> distanceFilterValue = 2000.0
                }
                fragmentWalkBinding.drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                fragmentWalkBinding.chipMapFilter.isChecked = false
                showSnackbar("필터가 선택되지 않았습니다 기본값으로 적용됩니다.")
                fragmentWalkBinding.drawerLayout.closeDrawer(GravityCompat.END)
            }
        }




        fragmentWalkBinding.buttonStopWalk.setOnClickListener {
            onWalk=false
            handler.removeCallbacks(periodicRunnable)
            fragmentWalkBinding.mapView.removeAllPOIItems()
            toggleVisibility(fragmentWalkBinding.LinearLayoutOffWalk, fragmentWalkBinding.LinearLayoutOnWalk)
            fragmentWalkBinding.imageViewWalkToggle.setImageResource(R.drawable.dog_home)

            val endTimestamp=timestampToString(System.currentTimeMillis())
            val startTimestamp=timestampToString(viewModel.walkStartTime)
            val bundle=Bundle()
            bundle.putString("walkRecorduid",userId)
            bundle.putString("walkRecordDate",getCurrentDate())
            bundle.putString("walkRecordStartTime",startTimestamp)
            bundle.putString("walkRecordEndTime",endTimestamp)
            bundle.putLong("walkDuration",elapsedTime)
            bundle.putString("walkDistance",totalDistance.toString())
            bundle.putString("mateNickname",walkMateNickname)
            walkMateNickname?.let { it1 -> Log.d("닉닉닉", it1) }
            if(walkWithUser!=null) {
                if (userId != walkWithUser!!.receiverId) {
                    bundle.putString("walkMatchingId", walkWithUser!!.receiverId)
                    bundle.putString("walkMatchingRecorId",walkWithUser!!.walkRecordId)
                } else {
                    bundle.putString("walkMatchingId", walkWithUser!!.senderId)
                    bundle.putString("walkMatchingRecorId",walkWithUser!!.walkRecordId)
                }
            }

            //맵에 이동 경로 그리기 -> 근데 그려도 보안정책상 캡쳐가 안됨
//            if (locationList.size > 1) {
//                val polyline=MapPolyline()
//                polyline.tag=1000
//                polyline.lineColor = Color.argb(128, 255, 51, 0);
//                for (location in locationList) {
//                    polyline.addPoint(MapPoint.mapPointWithGeoCoord(location.latitude, location.longitude))
//                }
//                fragmentWalkBinding.mapView.addPolyline(polyline)
//            }
            viewModel.stopTimer()
            totalDistance=0.0f
            viewModel.distanceMoved.value=totalDistance
            elapsedTime = 0L
            viewModel.elapsedTimeLiveData.value= elapsedTime.toString()
            viewModel.updateOnWalkStatusFalse(userId)
            mainActivity.navigate(R.id.action_mainFragment_to_WriteWalkReviewFragment, bundle)
            getCurrentLocation()
            //여기에 메서드로 walkWithUser.matchid로 해당match문서의 status=4로 바꾸는 메서드 넣어야함
            matches1.remove(walkWithUser)
            val currentTimestamp = com.google.firebase.Timestamp.now()
            val differences = matches1.map { match ->
                kotlin.math.abs(currentTimestamp.seconds - match.walkTimestamp!!.seconds)
            }
            val closestIndex = differences.indexOf(differences.minOrNull())
            walkWithUser = if (closestIndex != -1) matches1[closestIndex] else null
            if (matches1.isNotEmpty()) {
                fragmentWalkBinding.buttonWalk.text = "같이 산책하기"
            } else {
                fragmentWalkBinding.buttonWalk.text = "혼자 산책하기"
            }
            // 이제 가장 가까운 약속이 있으면 그것을 walkWithUser로 설정, 없으면 null로 설정
            walkWithUser = if (closestIndex != -1) matches1[closestIndex] else null

        }


        fragmentWalkBinding.imageViewMylocation.setOnClickListener {
            updateCurrentLocationOnce()

//            if(onWalk==true) {
//                getCurrentLocationOnWalk()
//            }else {
//                getCurrentLocation()
//            }
            LastKnownLocation.latitude = null
            LastKnownLocation.longitude= null
        }
    }
    @SuppressLint("SimpleDateFormat")
    fun timestampToString(timestamp: Long): String {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return formatter.format(date)
    }
    private fun captureMapView(mapView: MapView): Bitmap {
        mapView.isDrawingCacheEnabled = true
        mapView.buildDrawingCache(true)
        val bitmap = Bitmap.createBitmap(mapView.drawingCache)
        mapView.isDrawingCacheEnabled = false
        return bitmap
    }
    private fun saveBitmapToFile(bitmap: Bitmap, filename: String): Uri? {
        var uri: Uri? = null
        try {
            val file = File(context?.externalCacheDir, filename)
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            uri = Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uri
    }
    private fun observeViewModelonWalk() {
        viewModel.usersOnWalk.observe(viewLifecycleOwner, Observer { users ->
            users.forEach { user ->
                user.nickname?.let { Log.d("전체유저", it) }
            }
            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        val currentLocation = Location("currentLocation").apply {
                            latitude = it.latitude ?: 0.0
                            longitude = it.longitude ?: 0.0
                        }



                        nearbyUsers = users.filter { user ->
                            val userLocation = Location("userLocation").apply {
                                latitude = user.location?.get("latitude") ?: 0.0
                                longitude = user.location?.get("longitude") ?: 0.0
                            }
                            val filterddistance=if(distanceFilterValue==null){
                                2000.0
                            }else{
                                distanceFilterValue
                            }
                            user.uid != userId && currentLocation.distanceTo(userLocation) <= filterddistance!!
                        }

                        val newNearbyUserIds = nearbyUsers!!.map { it.uid }.toSet()
                        val usersToRemove = userMarkers.keys.filter { it !in newNearbyUserIds }

                        usersToRemove.forEach { userIdToRemove ->
                            val markerToRemove = userMarkers[userIdToRemove]
                            if (markerToRemove != null) {
                                fragmentWalkBinding.mapView.removePOIItem(markerToRemove)
                                userMarkers.remove(userIdToRemove)
                            }
                        }

                        for (user in nearbyUsers!!) {
                            val mapPoint = MapPoint.mapPointWithGeoCoord(
                                user.location?.get("latitude") ?: 0.0, user.location?.get("longitude") ?: 0.0)

                            if (userMarkers.containsKey(user.uid)) {
                                val existingMarker = userMarkers[user.uid]
                                existingMarker?.mapPoint = mapPoint
                            } else {
                                val poiItem = MapPOIItem().apply {
                                    itemName = user.nickname
                                    tag = user.nickname.hashCode()
                                    this.mapPoint = mapPoint
                                    markerType = MapPOIItem.MarkerType.CustomImage
                                    customImageResourceId = R.drawable.location
                                    isCustomImageAutoscale = true
                                    setCustomImageAnchor(0.5f, 1.0f)
                                }
                                fragmentWalkBinding.mapView.addPOIItem(poiItem)
                                userMarkers[user.uid!!] = poiItem
                            }
                        }
                    }
                }
            }
        })
    }
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
                    customImageResourceId = R.drawable.paw
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

    //메인화면 진입시 권한 요청/LastKnownLocation의 값에 따라 검색/마킹
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
    private fun requestLocationPermissionIfNeededOnWalk() {
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
            getCurrentLocationOnWalk()

        }
    }
    private fun getCurrentLocationOnWalk() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    LastKnownLocation.latitude=it.latitude
                    LastKnownLocation.longitude=it.longitude
                    viewModel.observeUsersOnWalk()
                    val mapPoint = MapPoint.mapPointWithGeoCoord(it.latitude, it.longitude)
                    //showUserLocationOnMap(it)
                    fragmentWalkBinding.mapView.setMapCenterPoint(mapPoint, true)
                }
            }
        }
    }
    private fun getCurrentLocationOnWalk2(){
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    LastKnownLocation.latitude=it.latitude
                    LastKnownLocation.longitude=it.longitude
                    location123?.latitude=it.latitude
                    location123?.longitude=it.longitude
                    viewModel.observeUsersOnWalk()
                    //val mapPoint = MapPoint.mapPointWithGeoCoord(it.latitude, it.longitude)
                    showUserLocationOnMap(it)
                }
            }
        }
    }

    //내 현재 위치에서 검색/마킹
    private fun getCurrentLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    LastKnownLocation.latitude=it.latitude
                    LastKnownLocation.longitude=it.longitude
                    viewModel.searchPlacesByKeyword(it.latitude, it.longitude, "동물")
                    //showUserLocationOnMap1(location)
                    val mapPoint = MapPoint.mapPointWithGeoCoord(it.latitude, it.longitude)
                    fragmentWalkBinding.mapView.setMapCenterPoint(mapPoint, true)
                }
            }
        }
    }
    private fun getLastLocationOnWalk() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            LastKnownLocation.let {
                LastKnownLocation.latitude?.let { it1 ->
                    LastKnownLocation.longitude?.let { it2 ->
                        viewModel.observeUsersOnWalk()
                    }
                }
                val mapPoint = LastKnownLocation.latitude?.let { it1 ->
                    LastKnownLocation.longitude?.let { it2 ->
                        MapPoint.mapPointWithGeoCoord(it1, it2)
                    }
                }
                val location = Location("providerName")
                location.latitude = LastKnownLocation.latitude!!
                location.longitude = LastKnownLocation.longitude!!
                //showUserLocationOnMap(location)
                fragmentWalkBinding.mapView.setMapCenterPoint(mapPoint, true)

                for (marker in currentMarkers) {
                    fragmentWalkBinding.mapView.removePOIItem(marker)
                }
                currentMarkers.clear()
            }
        }
    }


    private fun checkPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun showUserLocationOnMap(location: Location) {
        //fragmentWalkBinding.mapView.removePOIItem(userLocationMarker)
        userLocationMarker?.let {
            fragmentWalkBinding.mapView.removePOIItem(it)
        }
        userLocationMarker = MapPOIItem().apply {
            itemName = "나"
            mapPoint = MapPoint.mapPointWithGeoCoord(location.latitude, location.longitude)
            //custom으로하면 로드가 안되서 깨지는 현상
            markerType = MapPOIItem.MarkerType.RedPin
            //customImageResourceId = R.drawable.paw_pin
            isCustomImageAutoscale = true
            setCustomImageAnchor(0.5f, 1.0f)

        }
        fragmentWalkBinding.mapView.addPOIItem(userLocationMarker)
//        fragmentWalkBinding.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(location.latitude, location.longitude),true)
    }
    private fun showUserLocationOnMap1(location: Location) {
        //fragmentWalkBinding.mapView.removePOIItem(userLocationMarker)
        userLocationMarker?.let {
            fragmentWalkBinding.mapView.removePOIItem(it)
        }
        userLocationMarker = MapPOIItem().apply {
            itemName = "나"
            mapPoint = MapPoint.mapPointWithGeoCoord(location.latitude, location.longitude)
            markerType = MapPOIItem.MarkerType.CustomImage
            customImageResourceId = R.drawable.paw_pin
            isCustomImageAutoscale = true
            setCustomImageAnchor(0.5f, 1.0f)

        }
        fragmentWalkBinding.mapView.addPOIItem(userLocationMarker)
//        fragmentWalkBinding.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(location.latitude, location.longitude),true)
    }
    private fun updateCurrentLocationOnce() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val mapPoint = MapPoint.mapPointWithGeoCoord(it.latitude, it.longitude)
                    fragmentWalkBinding.mapView.setMapCenterPoint(mapPoint, true)
                }
            }
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            p0  ?: return
            for (location in p0.locations) {
                // 정확도 체크
                if (location.accuracy <= 5) {
                    //showUserLocationOnMap(location)
                    locationList.add(location)
                    lastLocation = location
                    lastLocation?.let { location ->
                        viewModel.updateUserLocation(userId, location.latitude, location.longitude)
                        //viewModel.updateLocationIfOnWalk(userId,location.latitude,location.longitude)
                    }
                    Log.d("infooooo1",lastLocation.toString())
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            val locationRequest = LocationRequest.create().apply {
                interval = 2000 // 2초마다 갱신
                fastestInterval = 5000 // 타앱
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        totalDistance=0.0f
        fragmentWalkBinding.textViewWalkDistance.text=totalDistance.toString()
    }


    //LastKnownLocation의 위치에서 검색 마킹(원래 있던 마커들 지우고)
    private fun getLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    //showUserLocationOnMap1(location)
                }
            }
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


                fragmentWalkBinding.mapView.setMapCenterPoint(mapPoint,true)


                for (marker in currentMarkers) {
                    fragmentWalkBinding.mapView.removePOIItem(marker)
                }
                currentMarkers.clear()
            }
        }
    }

    //거리 필터의 값에 따라 ㄱ
    private fun getLastLocationFilter(radius:Int) {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            LastKnownLocation.let {
                LastKnownLocation.latitude?.let { it1 ->
                    LastKnownLocation.longitude?.let { it2 ->
                        viewModel.searchPlacesByKeywordFilter(it1, it2, "동물",radius)
                    }
                }
                val mapPoint = LastKnownLocation.latitude?.let { it1 ->
                    LastKnownLocation.longitude?.let { it2 -> MapPoint.mapPointWithGeoCoord(it1, it2)
                    }
                }
                fragmentWalkBinding.mapView.setMapCenterPoint(mapPoint, true)

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
    private fun startCountdown() {

        val blockingDialog = BlockingDialogFragment()
        blockingDialog.show(childFragmentManager, "BLOCKING_DIALOG")

        countDownTimer = object : CountDownTimer((countdownValue * 1000).toLong(), countDownInterval.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                fragmentWalkBinding.textViewWalkCountdown.text = countdownValue.toString()
                countdownValue--
            }

            override fun onFinish() {
                    blockingDialog.dismiss()
                // 카운트다운이 끝나면 시작 작업을 수행하고 다이얼로그를 숨깁니다.
                toggleVisibility(fragmentWalkBinding.LinearLayoutOnWalk, fragmentWalkBinding.LinearLayoutOffWalk)
                fragmentWalkBinding.imageViewWalkToggle.setImageResource(R.drawable.dog_walk)
                viewModel.distanceMoved.value=0.0f
                viewModel.startTimer()
                viewModel.setOnWalkStatusTrue(userId)
                location123?.latitude?.let {
                    location123?.longitude?.let { it1 ->
                        viewModel.updateUserLocation(userId, it, it1)
                    }
                }

                hideProgress()
            }
        }.start()
    }

//    private fun startCountdown() {
//        countDownTimer = object : CountDownTimer((countdownValue * 1000).toLong(), countDownInterval.toLong()) {
//            override fun onTick(millisUntilFinished: Long) {
////                // 카운트다운 값을 텍스트뷰에 표시합니다.
//                fragmentWalkBinding.textViewWalkCountdown.text = countdownValue.toString()
//                countdownValue--
//            }
//            override fun onFinish() {
//                // 카운트다운이 끝나면 시작 작업을 수행하고 다이얼로그를 숨깁니다.
//                toggleVisibility(fragmentWalkBinding.LinearLayoutOnWalk, fragmentWalkBinding.LinearLayoutOffWalk)
//                fragmentWalkBinding.imageViewWalkToggle.setImageResource(R.drawable.dog_walk)
//                viewModel.distanceMoved.value=0.0f
//                viewModel.startTimer()
//                viewModel.setOnWalkStatusTrue(userId)
//                location123?.latitude?.let {
//                    location123?.longitude?.let { it1 ->
//                        viewModel.updateUserLocation(userId,
//                            it, it1
//                        )
//                    }
//                }
//                //requestLocationPermissionIfNeededOnWalk()
//                //startLocationUpdates()
//                hideProgress()
//            }
//        }.start()
//    }
    fun showProgress() {
        fragmentWalkBinding.textViewWalkCountdown.visibility = View.VISIBLE
        //fragmentWalkBinding.progressBackgroundWalk1.visibility = View.VISIBLE
    }

    fun hideProgress() {
        fragmentWalkBinding.textViewWalkCountdown.visibility = View.GONE
        //fragmentWalkBinding.progressBackgroundWalk1.visibility = View.GONE
    }
    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String
    {
        val current = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd") // 년-월-일
        return formatter.format(current)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateAgeGroup(birthday: String): String {
        val birthYear = LocalDate.parse(birthday, DateTimeFormatter.ofPattern("yyyy-MM-dd")).year
        val currentYear = LocalDate.now().year
        val age = currentYear - birthYear

        return when {
            age in 0..9 -> "나이대 : 영유아 및 초등학생"
            age in 10..19 -> "나이대 : 10대"
            age in 20..29 -> "나이대 : 20대"
            age in 30..39 -> "나이대 : 30대"
            age in 40..49 -> "나이대 : 40대"
            age in 50..59 -> "나이대 : 50대"
            age in 60..69 -> "나이대 : 60대"
            age in 70..79 -> "나이대 : 70대"
            else -> "밝히지 않음"
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateAge(birthday: String): Int {
        val birthDate = LocalDate.parse(birthday, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val currentDate = LocalDate.now()

        var age = currentDate.year - birthDate.year

        if (birthDate.month > currentDate.month || (birthDate.month == currentDate.month && birthDate.dayOfMonth > currentDate.dayOfMonth)) {
            age--
        }

        return age
    }
    //맵의 마커 클릭시
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPOIItemSelected(p0: net.daum.mf.map.api.MapView?, p1: MapPOIItem?) {
        if (p1?.itemName == "나") {
            return
        }
        if (onWalk == false) {
            val selectedPlace = kakaoSearchResponse.documents.find { it.id.hashCode() == p1?.tag }
            val detailCardView = layoutInflater.inflate(R.layout.row_place_review, null)
            val detailDialog = BottomSheetDialog(requireActivity())
            val detailRating = detailCardView.findViewById<RatingBar>(R.id.placeReviewDetailRatingBar)
            val detailUserName = detailCardView.findViewById<TextView>(R.id.textViewPlaceReviewDetailUserName)
            val detailDate = detailCardView.findViewById<TextView>(R.id.textViewPlaceReviewDetailDate)
            val detailContent = detailCardView.findViewById<TextView>(R.id.textView4)
            val detailImage = detailCardView.findViewById<ImageView>(R.id.imageView11)
            val buttonsubmit = initialBottomSheetView.findViewById<Button>(R.id.buttonSubmitReview)
            val placeId = selectedPlace?.id
            //클릭한 맵의 placeId로 메서드 실행
            placeId?.let {
                viewModel.fetchFavoriteCount(it)
                viewModel.fetchReviewCount(it)
                viewModel.fetchLatestReviewsForPlace(it)
                viewModel.fetchPlaceInfoFromFirestore(it)
                viewModel.fetchIsPlaceFavoritedByUser(it, userId)  // Removed the collect operation
                viewModel.fetchAverageRatingForPlace(it)
            }

            lifecycleScope.launch {
                //바텀시트의 좋아요 상태에 따라 하트 이미지 변경
                viewModel.isPlaceFavorited.collect { isFavorited ->
                    isFavorited?.let {
                        if (it) {
                            initialBottomSheetView.findViewById<ImageView>(R.id.imageViewFavoirte).setImageResource(R.drawable.filled_heart)
                            isFavorited1 = true

                        } else {
                            initialBottomSheetView.findViewById<ImageView>(R.id.imageViewFavoirte).setImageResource(R.drawable.empty_heart)
                            isFavorited1 = false
                        }

                    }

                }

            }
            //place의 리뷰 별점 평균 표기
            viewModel.averageRatingForPlace.observe(viewLifecycleOwner) { avgRating ->
                val roundedAvgRating = String.format("%.1f", avgRating).toFloat()
                initialBottomSheetView.findViewById<RatingBar>(R.id.placeRatingBar).rating = avgRating
                initialBottomSheetView.findViewById<TextView>(R.id.textViewratingbar).text = roundedAvgRating.toString()
                avgRatingBundle.putFloat("avgRating", avgRating)
                Log.d("avgRating", avgRating.toString())
            }

            //favorite 서브컬렉션의 문서 개수 받아와서 실시간 반영
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.favoriteCount.collect { count ->
                    val favoriteCountTextView = initialBottomSheetView.findViewById<TextView>(R.id.textViewPlaceFavoriteCount)
                    favoriteCountTextView.text = "${count}명의 유저가 추천합니다."
                }
            }

            //place 정보 받아와서 db에 있는 place인지 확인 후 분기로 이름 입력
            viewModel.placeInfo.observe(viewLifecycleOwner) { placeInfo ->
                val textViewPlaceName = initialBottomSheetView.findViewById<TextView>(R.id.textView)
                val imageViewPlace = initialBottomSheetView.findViewById<ImageView>(R.id.ImageViewBottomRecommendPlace)
                textViewPlaceName.text = placeInfo?.get("name") as? String ?: "${selectedPlace?.place_name}"
                val imageUrl = placeInfo?.get("imageRes") as? String

                if (imageUrl != null) {
                    Glide.with(imageViewPlace.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.default_profile_image)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                return false
                            }

                            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

                                Handler(Looper.getMainLooper()).postDelayed({ bottomSheetDialog.show() }, 200)
                                return false
                            }
                        })
                        .into(imageViewPlace)
                } else {
                    imageViewPlace.setImageResource(R.drawable.default_profile_image)
                    Handler(Looper.getMainLooper()).postDelayed({ bottomSheetDialog.show() }, 200)
                }
            }

            //리뷰 개수 받아와서 출력
            viewModel.reviewCount.observe(viewLifecycleOwner) { reviewCount ->
                val reviewCountTextView = initialBottomSheetView.findViewById<TextView>(R.id.textViewPlaceReviewCount)
                reviewCountTextView.text = "${reviewCount}개의 리뷰가 있어요"
            }

            //최신순 리뷰 두개 받아와서 바텀시트에 1,2 출력
            viewModel.latestReviews.observe(viewLifecycleOwner) { reviews ->
                latestReviews = reviews
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

                    placeuserRating1.visibility = View.VISIBLE
                    placeuserReview1.visibility = View.VISIBLE


                    if (reviews.size > 1) {
                        val secondReview = reviews[1]
                        placeuserRating2.rating = secondReview.rating!!
                        placeuserReview2.text = secondReview.comment

                        placeuserRating2.visibility = View.VISIBLE
                        placeuserReview2.visibility = View.VISIBLE

                    } else {
                        placeuserRating2.visibility = View.GONE
                        placeuserReview2.visibility = View.GONE
                    }

                    initialBottomSheetView.findViewById<Chip>(R.id.chipViewAllReviews).visibility = View.VISIBLE
                    initialBottomSheetView.findViewById<TextView>(R.id.textViewNoReview).visibility = View.GONE
                } else {
                    placeuserRating1.visibility = View.GONE
                    placeuserRating2.visibility = View.GONE
                    placeuserReview1.visibility = View.GONE
                    placeuserReview2.visibility = View.GONE
                    initialBottomSheetView.findViewById<Chip>(R.id.chipViewAllReviews).visibility = View.GONE
                    initialBottomSheetView.findViewById<TextView>(R.id.textViewNoReview).visibility = View.VISIBLE
                }
            }



            //바텀시트 끌어 올렸을때 상세 리뷰 페이지로 이동
            bottomSheetDialog.behavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            avgRatingBundle.putString("place_name", selectedPlace?.place_name)
                            avgRatingBundle.putString("place_id", selectedPlace?.id)
                            avgRatingBundle.putString("phone", selectedPlace?.phone)
                            avgRatingBundle.putString(
                                "place_road_adress_name",
                                selectedPlace?.road_address_name
                            )
                            avgRatingBundle.putString(
                                "place_category",
                                selectedPlace?.category_group_name
                            )
                            mainActivity.navigate(R.id.action_mainFragment_to_placeReviewFragment, avgRatingBundle,)
                            bottomSheetDialog.dismiss()
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })

            //좋아요 버튼 눌렀을 때  userid로 favorite 서브 컬렉션에 문서 추가 / isFavorited 상태에 따라 이미지 교체,스넥바,count 출력
            initialBottomSheetView.findViewById<ImageView>(R.id.imageViewFavoirte).apply {
                val place = PlaceData(selectedPlace!!.id, selectedPlace.place_name, selectedPlace.category_group_name, (selectedPlace.x).toString(), (selectedPlace.y).toString(), selectedPlace.phone, selectedPlace.road_address_name)
                setOnClickListener {
                    if (!isFavorited1) {
                        isFavorited1 = true
                        setImageResource(R.drawable.filled_heart)
                        val favorite = Favorite(userId)
                        viewModel.addPlaceToFavorite(place, favorite)
                        val bottomplacelayout: CoordinatorLayout =
                            initialBottomSheetView.findViewById(R.id.bottom_place_layout)
                        Snackbar.make(bottomplacelayout, "반영되었습니다.", Snackbar.LENGTH_SHORT).show()
                    } else {
                        isFavorited1 = false
                        setImageResource(R.drawable.empty_heart)
                        viewModel.removeFavorite(placeId!!, userId)
                        val bottomplacelayout: CoordinatorLayout =
                            initialBottomSheetView.findViewById(R.id.bottom_place_layout)
                        Snackbar.make(bottomplacelayout, "반영되었습니다.", Snackbar.LENGTH_SHORT).show()
                    }


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
            //리뷰 입력 버튼
            buttonsubmit.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("place_name", selectedPlace?.place_name)
                bundle.putString("place_id", selectedPlace?.id)
                bundle.putString("place_category", selectedPlace?.category_group_name)
                bundle.putString("place_long", selectedPlace?.x.toString())
                bundle.putString("place_lat", selectedPlace?.y.toString())
                bundle.putString("phone", selectedPlace?.phone)
                bundle.putString("userNickname", userNickname)
                bundle.putString("place_road_adress_name", selectedPlace?.road_address_name)

                mainActivity.navigate(R.id.action_mainFragment_to_writePlaceReviewFragment, bundle)
                bottomSheetDialog.dismiss()
            }
            //최근리뷰 1 누르면 상세 카드뷰 출력
            initialBottomSheetView.findViewById<TextView>(R.id.placeUserReview1)
                .setOnClickListener {
                    detailDialog.setContentView(detailCardView)
                    detailRating.rating = latestReviews!![0].rating!!
                    detailUserName.text = latestReviews!![0].userNickname
                    detailDate.text = latestReviews!![0].date
                    detailContent.text = latestReviews!![0].comment
                    detailDialog.findViewById<TextView>(R.id.textViewPlaceReviewModify)?.visibility =
                        View.GONE
                    detailDialog.findViewById<TextView>(R.id.textViewPlaceReviewDelete)?.visibility =
                        View.GONE
                    latestReviews!![0].imageRes?.let { imageUrl ->
                        Glide.with(detailImage.context)
                            .load(imageUrl)
                            .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                    return false
                                }

                                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                    detailDialog.show()
                                    return false
                                }
                            })
                            .into(detailImage)
                    }
                }
            //최근리뷰 2 누르면 상세 카드뷰 출력
            initialBottomSheetView.findViewById<TextView>(R.id.placeUserReview2)
                .setOnClickListener {

                    detailRating.rating = latestReviews!![1].rating!!
                    detailUserName.text = latestReviews!![1].userNickname
                    detailDate.text = latestReviews!![1].date
                    detailContent.text = latestReviews!![1].comment
                    detailDialog.findViewById<TextView>(R.id.textViewPlaceReviewModify)?.visibility =
                        View.GONE
                    detailDialog.findViewById<TextView>(R.id.textViewPlaceReviewDelete)?.visibility =
                        View.GONE
                    latestReviews!![1].imageRes?.let { imageUrl ->
                        Glide.with(detailImage.context)
                            .load(imageUrl)
                            .listener(object : RequestListener<Drawable> { override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                detailDialog.show()
                                return false
                            }

                                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                    detailDialog.show()
                                    return false
                                }
                            })
                            .into(detailImage)
                    }
                }
            //상세리뷰 페이지
            initialBottomSheetView.findViewById<Chip>(R.id.chipViewAllReviews).setOnClickListener {
                avgRatingBundle.putString("place_name", selectedPlace?.place_name)
                avgRatingBundle.putString("place_id", selectedPlace?.id)
                avgRatingBundle.putString("phone", selectedPlace?.phone)
                avgRatingBundle.putString(
                    "place_road_adress_name",
                    selectedPlace?.road_address_name
                )
                avgRatingBundle.putString("place_category", selectedPlace?.category_group_name)
                avgRatingBundle.putString("userNickname", userNickname)
                bottomSheetDialog.dismiss()
                mainActivity.navigate(
                    R.id.action_mainFragment_to_placeReviewFragment,
                    avgRatingBundle
                )
            }
            //산책 on 유저마커 바텀시트
        } else {

            val selectedUserNicknameHash = p1?.tag

            val selectedUser = nearbyUsers?.find { it.nickname.hashCode() == selectedUserNicknameHash }
            selectedUser?.let { user ->
                user.uid?.let { viewModel.fetchMatchingWalkCount(it)
                    viewModel.fetchAverageRatingForUser(it)
                }
                val userNicknameTextView = onWalkBottomSheetView.findViewById<TextView>(R.id.textViewBottomUserNickname)
                userNicknameTextView.text = user.nickname
                val textViewBottomUserAgeRange=onWalkBottomSheetView.findViewById<TextView>(R.id.textViewBottomUserAgeRange)
                textViewBottomUserAgeRange.text=user.birthday?.let { calculateAgeGroup(it) }
                val imageViewUser=onWalkBottomSheetView.findViewById<ImageView>(R.id.ImageViewBottomUserProfileImage)
                val textViewBottomUserAvailability=onWalkBottomSheetView.findViewById<TextView>(R.id.textViewBottomUserAvailability)
                if(user.walkHoursStart?.isNotEmpty() == true) {
                    textViewBottomUserAvailability.text = "산책 가능 시간대 : ${user.walkHoursStart} ~ ${user.walkHoursEnd}"
                }else{
                    textViewBottomUserAvailability.text = "산책 가능 시간대 : 언제든지 가능해요"
                }
                val textViewBottomUserMathcingHistory=onWalkBottomSheetView.findViewById<TextView>(R.id.textViewBottomUserMathcingHistory)
                val textViewBottomPetName=onWalkBottomSheetView.findViewById<TextView>(R.id.textViewBottomPetName)
                val textViewBottomPetAge=onWalkBottomSheetView.findViewById<TextView>(R.id.textViewBottomPetAge)
                val textViewBottomPetBreed=onWalkBottomSheetView.findViewById<TextView>(R.id.textViewBottomPetBreed)
                val textViewBottomPetPropensity=onWalkBottomSheetView.findViewById<TextView>(R.id.textViewBottomPetPropensity)
                val textViewBottomPetGender=onWalkBottomSheetView.findViewById<TextView>(R.id.textViewBottomPetGender)
                val textViewBottomIsPetNeutered=onWalkBottomSheetView.findViewById<TextView>(R.id.textViewBottomIsPetNeutered)
                val imageViewBottomPetProfileImage=onWalkBottomSheetView.findViewById<ImageView>(R.id.ImageViewBottomPetProfileImage)
                val buttonBottomWalk=onWalkBottomSheetView.findViewById<Button>(R.id.buttonBottomWalk)
                val buttonBottomBlock=onWalkBottomSheetView.findViewById<Button>(R.id.buttonBottomBlock)
                val userRating= onWalkBottomSheetView.findViewById<RatingBar>(R.id.ratingBarUser2)

                buttonBottomWalk.setOnClickListener {
                    val bundle=Bundle()
                    bundle.putString("receiverId",user.uid.toString())
                    mainActivity.navigate(R.id.action_mainFragment_to_chat,bundle)
                    onWalkbottomSheetDialog.dismiss()
                }
                buttonBottomBlock.setOnClickListener {
                    user.uid?.let { viewModel.blockUser(userId, it) }
                    viewModel.isUserBlocked.observe(viewLifecycleOwner, Observer { isBlocked ->
                        if (isBlocked) {
                            Snackbar.make(buttonBottomBlock, "사용자가 차단되었습니다.",Snackbar.LENGTH_SHORT).show()
                        }
                        else {
                            Snackbar.make(buttonBottomBlock, "사용자 차단에 실패하였습니다.",Snackbar.LENGTH_SHORT).show()
                        }
                    })
                    onWalkbottomSheetDialog.dismiss()
                }
                user.pets.let { pets ->
                    val firstPet = pets.firstOrNull()
                    firstPet?.let { pet ->
                        val petSex=when((pet.petSex)?.toInt()){
                            0->{"남아"}
                            1->{"여아"}
                            else -> {"입력 안함"}
                        }
                        val isPetNeutered=if(pet.neutered==true){ "O" }else{ "X" }
                        val petAge= pet.birth?.let { calculateAge(it) }
                        textViewBottomPetName.text = "이름 : ${pet.name}"
                        textViewBottomPetAge.text = "나이 : ${petAge}세" //나중에계산해
                        textViewBottomPetBreed.text="견종 : ${pet.breed}"
                        textViewBottomPetPropensity.text="성향 : ${pet.character}"
                        textViewBottomPetGender.text="성별 : ${petSex}"
                        textViewBottomIsPetNeutered.text="중성화 여부 : ${isPetNeutered}"
                        if (pet.petImageUrl != null) {
                            val userImageRef = Firebase.storage.reference.child(pet.petImageUrl)
                            Log.d("펫이미지", pet.petImageUrl)
                            userImageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                                // 이미지 다운로드에 성공한 경우
                                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                imageViewBottomPetProfileImage.setImageBitmap(bitmap) // ImageView에 이미지 설정
                            }.addOnFailureListener {
                                // 이미지 다운로드에 실패한 경우
                                Log.e("FirebaseStorage", "이미지 다운로드 실패: $it")
                                imageViewBottomPetProfileImage.setImageResource(R.drawable.pets_24px)
                            }
                        }
//                        if(pet.imgaeURI !=null){
//                            Glide.with(imageViewBottomPetProfileImage.context)
//                                .load(pet.imgaeURI)
//                                .placeholder(R.drawable.pets_24px)
//                                .listener(object : RequestListener<Drawable> {
//                                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
//                                        return false
//                                    }
//
//                                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                                        return false
//                                    }
//                                })
//                                .into(imageViewBottomPetProfileImage)
//                        } else {
//                            imageViewBottomPetProfileImage.setImageResource(R.drawable.pets_24px)
//                        }
                    }
                }
                viewModel.averageRatingForUser.observe(this, Observer { ratings ->
                    userRating.rating= ratings.toFloat()
                    val roundedAvgRating = String.format("%.1f", ratings).toDouble()
                    Log.d("에바야viewㄹㅇ",roundedAvgRating.toString())
                    userRating.rating=roundedAvgRating.toFloat()
                })
                viewModel.walkMatchingCount.observe(viewLifecycleOwner, Observer { count ->
                    Log.d("매칭 기록",count.toString())
                    if(count!=null) {
                        textViewBottomUserMathcingHistory.text = "매칭 기록: ${count}회"
                    }else{
                        textViewBottomUserMathcingHistory.text = "매칭 기록: 0회"
                    }
                })
                val userImageRef = Firebase.storage.reference.child(user.userImage!!)
                Log.d("이미지",user.userImage)
                Log.d("이미지",userImageRef.toString())
//                userImageRef.downloadUrl.addOnCompleteListener {
//                    Log.d("asdfasdf", imageViewUser.context.toString())
//                    Glide.with(imageViewUser.context)
//                        .load(it)
//                        .placeholder(R.drawable.default_profile_image)
//                        .into(imageViewUser)
//                    Handler(Looper.getMainLooper()).postDelayed({
//                        onWalkbottomSheetDialog.show()
//                    }, 200)
//
//                            }.addOnFailureListener {
//                                Log.d("이미지실패",it.toString())
//                            }
                userImageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                    // 이미지 다운로드에 성공한 경우
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    imageViewUser.setImageBitmap(bitmap) // ImageView에 이미지 설정
                    onWalkbottomSheetDialog.show()
                }.addOnFailureListener {
                    // 이미지 다운로드에 실패한 경우
                    Log.e("FirebaseStorage", "이미지 다운로드 실패: $it")
                    onWalkbottomSheetDialog.show()
                }


            }


        }


    }


    //맵 드래그 -> 맵 중심의 좌표 이동하면 실행되는 메서드
    @Deprecated("Deprecated in Java")
    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
        // 새로운 중심에서 검색(onWalk == true면 user검색 false면 place검색)
        if(onWalk == false) {
            p1?.mapPointGeoCoord?.let {
                LastKnownLocation.latitude = it.latitude
                LastKnownLocation.longitude = it.longitude
                viewModel.searchPlacesByKeyword(it.latitude, it.longitude, "동물")

                Log.d("lastlocation", (LastKnownLocation.latitude).toString())
            }
        }else{
            p1?.mapPointGeoCoord?.let {
                viewModel.observeUsersOnWalk()
            }
        }
    }
    //맵의 줌 아웃 제한
    override fun onMapViewZoomLevelChanged(p0: MapView?, zoomLevel: Int) {
        val maxZoomLevel = 3
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
    override fun onCurrentLocationUpdate(mapView: net.daum.mf.map.api.MapView?, mapPoint: MapPoint?, v: Float) {
        val mapPointGeo = mapPoint?.mapPointGeoCoord
        Log.i("Location", "위도: ${mapPointGeo?.latitude}, 경도: ${mapPointGeo?.longitude}")

        // 기존의 현재 위치 마커 제거
        val existingMarker = mapView?.findPOIItemByTag(CURRENT_LOCATION_MARKER_TAG)
        if (existingMarker != null) {
            mapView.removePOIItem(existingMarker)
        }

        // 현재 위치에 새로운 마커 추가
        val marker = MapPOIItem()
        marker.itemName = "현재 위치"
        marker.tag = CURRENT_LOCATION_MARKER_TAG
        marker.mapPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo!!.latitude, mapPointGeo!!.longitude)
        marker.markerType = MapPOIItem.MarkerType.BluePin
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        mapView!!.addPOIItem(marker)
    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {}
    override fun onCurrentLocationUpdateFailed(p0: MapView?) {}
    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {}
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {}
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?) {}
    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {}
    override fun onMapViewInitialized(p0: MapView?) {

        p0?.setZoomLevel(2, true)}





    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        handler.postDelayed(periodicRunnable, delayMillis)
        //fragmentWalkBinding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
        viewModel.matchesLiveData.observe(viewLifecycleOwner) { matches ->
            matches1= matches as MutableList<Match>
            for(match in matches) {
                println("매치스: ${match.senderId}, Receiver ID: ${match.receiverId}, Timestamp: ${match.walkTimestamp}")
            }
            val currentTimestamp = com.google.firebase.Timestamp.now()
            val differences = matches.map { match ->
                kotlin.math.abs(currentTimestamp.seconds - match.walkTimestamp!!.seconds)
            }
            if(matches.isNotEmpty()) {
                val closestIndex = differences.indexOf(differences.minOrNull())
                walkWithUser = matches[closestIndex]
                println("매치스1: ${walkWithUser!!.senderId}, Receiver ID: ${walkWithUser!!.receiverId}, Timestamp: ${walkWithUser!!.walkTimestamp}")
                fragmentWalkBinding.buttonWalk.text = "같이 산책하기"
            }
        }

        viewModel.stopLocationUpdates()
        viewModel.startLocationUpdates()
        //updateCurrentLocationOnce()
        if(onWalk == true) {
            observeViewModelonWalk()
            requestLocationPermissionIfNeededOnWalk()
        }else{
            observeViewModel()
            requestLocationPermissionIfNeeded()
        }
        viewModel.elapsedTimeLiveData.observe(viewLifecycleOwner, Observer { elapsedTime1 ->
            val hours = elapsedTime1.toLong() / 3600
            val minutes = (elapsedTime1.toLong() % 3600) / 60
            val seconds = elapsedTime1.toLong() % 60
            elapsedTime=elapsedTime1.toLong()
            val formattedElapsedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            fragmentWalkBinding.textViewWalkTime.text = formattedElapsedTime
        })

        viewModel.distanceMoved.observe(viewLifecycleOwner, Observer { distance ->
            totalDistance=distance

            val formattedDistance = if (distance >= 1000) {
                val distanceInKilometers = distance / 1000.0
                String.format("%.1f KM", distanceInKilometers)
            } else {
                String.format("%.0f M", distance)
            }
            fragmentWalkBinding.textViewWalkDistance.text = formattedDistance
        })
        if (onWalk==true) {
            toggleVisibility(fragmentWalkBinding.LinearLayoutOnWalk, fragmentWalkBinding.LinearLayoutOffWalk)

        } else {
            toggleVisibility(fragmentWalkBinding.LinearLayoutOffWalk, fragmentWalkBinding.LinearLayoutOnWalk)
        }
    }
}


//뷰 모델 팩토리
class WalkViewModelFactory(private val repository: WalkRepository, private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WalkViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}