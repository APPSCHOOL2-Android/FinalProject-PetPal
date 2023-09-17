package com.petpal.mungmate.ui.placereview

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentWritePlaceReviewBinding
import com.petpal.mungmate.model.Place
import com.petpal.mungmate.model.Review
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

        val place = createPlaceFromArguments(arguments)

        fragmentWritePlaceReviewBinding.run {
            textViewplaceReviewName.text = place?.name

            buttonPlaceReviewSubmit.setOnClickListener {
                val rating = placeRatingBar.rating
                val writeid = "writeid"  // 실제로는 사용자 ID나 다른 고유 식별자를 사용해야 합니다.
                val reviewText = editTextReviewContent.text.toString()
                val timestamp = getCurrentDate()
                val review = Review(rating, writeid, reviewText, timestamp)

                // Firestore에 리뷰 데이터 저장
                if (place != null) {
                    addReview(place, review)
                }
                // 리뷰 등록 후 Navigation 이동
                mainActivity.navigate(
                    R.id.action_writePlaceReviewFragment_to_mainFragment,

                )
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

    fun addReview(place: Place, review: Review) {
        val placesRef = db.collection("places")
        val placeDocument = placesRef.document(place.id)

        placeDocument.get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    // Place가 Firestore에 없는 경우
                    placeDocument.set(place)
                        .addOnSuccessListener {
                            // Place 추가 성공 후 review 추가
                            addPlaceReview(place.id, review)
                        }
                } else {
                    // Place가 이미 Firestore에 있는 경우
                    addPlaceReview(place.id, review)
                }
            }
    }

    private fun addPlaceReview(placeId: String, review: Review) {
        val reviewRef = db.collection("places").document(placeId).collection("reviews")
        reviewRef.add(review)
    }

    private fun createPlaceFromArguments(arguments: Bundle?): Place? {
        return arguments?.let {
            Place(
                id = it.getString("place_id") ?: "",
                name = it.getString("place_name") ?: "",
                category = it.getString("place_category") ?: "",
                longitude = it.getString("place_long") ?: "",
                latitude = it.getString("place_lat") ?: "",
                phone = it.getString("phone") ?: "",
                address = it.getString("place_road_adress_name") ?: ""
            )
        }
    }
}