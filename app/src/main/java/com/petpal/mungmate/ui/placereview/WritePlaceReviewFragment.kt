package com.petpal.mungmate.ui.placereview


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentWritePlaceReviewBinding
import com.petpal.mungmate.model.PlaceData
import com.petpal.mungmate.model.Review
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
class WritePlaceReviewFragment : Fragment() {

    lateinit var fragmentWritePlaceReviewBinding: FragmentWritePlaceReviewBinding
    lateinit var mainActivity: MainActivity
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var selectedImageUri: Uri? = null
    private val auth = Firebase.auth
    private lateinit var userId:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentWritePlaceReviewBinding = FragmentWritePlaceReviewBinding.inflate(layoutInflater)

        val place = createPlaceFromArguments(arguments)
        val user=auth.currentUser
        val userNickname=arguments?.getString("userNickname")
        userId= user?.uid.toString()
        fragmentWritePlaceReviewBinding.run {

            materialPlaceReviewToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            textViewplaceReviewName.text = place?.name

            imageViewPlaceReview.setOnClickListener {
                selectImageFromGallery()
            }

            buttonPlaceReviewSubmit.setOnClickListener {
                val rating = placeRatingBar.rating
                val userid = userId  // 이 부분을 실제 사용자 ID로 업데이트해야 합니다.
                val comment = editTextReviewContent.text.toString()
                val date = getCurrentDate()

                val imageUri = selectedImageUri
                if (imageUri != null) {
                    uploadImageToStorage(imageUri) { imageUrl ->
                        val review = Review(userid, userNickname, date, rating, comment, imageUrl)
                        if (place != null) {
                            addReview(place, review)
                        }
                        // 리뷰 등록 후 Navigation 이동
                        mainActivity.navigate(
                            R.id.action_writePlaceReviewFragment_to_mainFragment
                        )
                    }
                } else {
                    // 이미지를 선택하지 않은 경우 처리
                    Toast.makeText(context, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return fragmentWritePlaceReviewBinding.root
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            selectedImageUri = data?.data
            fragmentWritePlaceReviewBinding.imageViewPlaceReview.setImageURI(selectedImageUri)
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        val current = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") //년-월-일 시:분:초
        return formatter.format(current)
    }

    private fun uploadImageToStorage(uri: Uri, onSuccess: (String) -> Unit) {
        showProgress()

        Glide.with(this)
            .asBitmap()
            .load(uri)
            .override(400, 400)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    resource.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                    val byteArray = byteArrayOutputStream.toByteArray()

                    val ref = storage.reference.child("reviews/${UUID.randomUUID()}.jpg")
                    ref.putBytes(byteArray)
                        .addOnSuccessListener {
                            ref.downloadUrl.addOnSuccessListener {
                                hideProgress()
                                onSuccess(it.toString())
                                showSnackbar("리뷰가 성공적으로 등록되었습니다.")
                            }
                        }
                        .addOnFailureListener {
                            fragmentWritePlaceReviewBinding.progressBar.visibility = View.GONE
                            Toast.makeText(context, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                        }
                }
            })
    }
    private fun showSnackbar(message: String) {
        Snackbar.make(fragmentWritePlaceReviewBinding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    fun showProgress() {
        fragmentWritePlaceReviewBinding.progressBar.visibility = View.VISIBLE
        fragmentWritePlaceReviewBinding.progressBackground.visibility = View.VISIBLE
    }

    fun hideProgress() {
        fragmentWritePlaceReviewBinding.progressBar.visibility = View.GONE
        fragmentWritePlaceReviewBinding.progressBackground.visibility = View.GONE
    }
    fun addReview(placeData: PlaceData, review: Review) {
        val placesRef = db.collection("places")
        val placeDocument = placesRef.document(placeData.id)

        placeDocument.get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    // Place가 Firestore에 없는 경우
                    placeDocument.set(placeData)
                        .addOnSuccessListener {
                            // PlaceData 추가 성공 후 review 추가
                            addPlaceReview(placeData.id, review)
                        }
                } else {
                    // Place가 이미 Firestore에 있는 경우
                    addPlaceReview(placeData.id, review)
                }
            }
    }

    private fun addPlaceReview(placeId: String, review: Review) {
        val documentId = "${review.date}_${review.userid}"
        val reviewRef = db.collection("places").document(placeId).collection("reviews").document(documentId)
        reviewRef.set(review)
    }

    private fun createPlaceFromArguments(arguments: Bundle?): PlaceData? {
        return arguments?.let {
            PlaceData(
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

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}