package com.petpal.mungmate.ui.placereview

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentWritePlaceReviewBinding
import java.text.SimpleDateFormat
import java.util.Date

class WritePlaceReviewFragment : Fragment() {
    lateinit var fragmentWritePlaceReviewBinding: FragmentWritePlaceReviewBinding
    lateinit var mainActivity: MainActivity
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentWritePlaceReviewBinding = FragmentWritePlaceReviewBinding.inflate(layoutInflater)

        val placeName = arguments?.getString("place_name")
        val placeId = arguments?.getString("place_id")
        val placeCategory = arguments?.getString("place_category")
        val placeLong = arguments?.getString("place_long")
        val placeLat = arguments?.getString("place_lat")
        val placePhone = arguments?.getString("phone")
        val placeAddress = arguments?.getString("place_road_adress_name")

        fragmentWritePlaceReviewBinding.run {
            textViewplaceReviewName.text = placeName

            buttonPlaceReviewSubmit.setOnClickListener {
                val rating = placeRatingBar.rating
                val writeid = "writeid"  // 실제로는 사용자 ID나 다른 고유 식별자를 사용해야 합니다.
                val review = editTextReviewContent.text
                val timestamp = getCurrentDate()

                // Firestore에 리뷰 데이터 저장
                if (review != null) {
                    addReview(placeName,placeCategory,placeLong,placeLat,placePhone, placeAddress, placeId,rating, writeid, review, timestamp)
                }

                // 리뷰 등록 후 Navigation 이동
                mainActivity.navigate(R.id.action_writePlaceReviewFragment_to_mainFragment)
            }
        }

        return fragmentWritePlaceReviewBinding.root
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        val current = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd") // 년-월-일 형식으로 출력
        return formatter.format(current)
    }

    fun addReview(placeName: String?, placeCategory:String?,placeLong:String?,placeLat:String?,placePhone:String?,placeAddress:String?,placeId: String?, rating: Float, writeid: String, review: CharSequence, timestamp: String) {
        val placesRef = db.collection("places")
        val placeDocument = placesRef.document(placeId!!)

        placeDocument.get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    // Place가 Firestore에 없는 경우
                    val place = hashMapOf(
                        "name" to placeName,
                        "category" to placeCategory,
                        "longitude" to placeLong,
                        "latitude" to placeLat,
                        "phone" to placePhone,
                        "address" to placeAddress
                    )
                    placeDocument.set(place)
                        .addOnSuccessListener {
                            // Place 추가 성공 후 review 추가
                            addPlaceReview(placeId, rating, writeid, review, timestamp)
                        }
                } else {
                    // Place가 이미 Firestore에 있는 경우
                    addPlaceReview(placeId, rating, writeid, review, timestamp)
                }
            }
    }

    fun addPlaceReview(placeId: String, rating: Float, writeid: String, review: CharSequence, timestamp: String) {
        val reviewRef = db.collection("places").document(placeId).collection("reviews")
        val reviewData = hashMapOf(
            "rating" to rating,
            "writeid" to writeid,
            "review" to review.toString(),
            "timestamp" to timestamp
        )
        reviewRef.add(reviewData)
    }


}