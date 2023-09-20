package com.petpal.mungmate.ui.placereview

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentPlaceReviewModifyBinding
import java.io.ByteArrayOutputStream
import java.util.UUID


class PlaceReviewModifyFragment : Fragment() {
    lateinit var fragmentPlaceReviewModifyBinding: FragmentPlaceReviewModifyBinding
    lateinit var mainActivity: MainActivity
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val IMAGE_PICK_CODE = 1001
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPlaceReviewModifyBinding= FragmentPlaceReviewModifyBinding.inflate(layoutInflater)
        mainActivity=activity as MainActivity

            val reviewUserId = arguments?.getString("reviewUserId")
            val reviewDate = arguments?.getString("reviewDate")
            val placeId=arguments?.getString("placeId")


            fragmentPlaceReviewModifyBinding.imageViewPlaceReviewModify.setOnClickListener {
                selectImageFromGallery()
            }

        fragmentPlaceReviewModifyBinding.buttonPlaceReviewSubmitModify.setOnClickListener {
            val updatedContent = fragmentPlaceReviewModifyBinding.editTextReviewContentModify.text.toString()
            val updatedRating = fragmentPlaceReviewModifyBinding.placeRatingBarModify.rating
            val updatedImg = selectedImageUri

            if (updatedImg != null) {
                uploadImageToStorage(updatedImg) { imageUrl ->
                    // 이미지 업로드 성공 후 Firestore 데이터 업데이트
                    val reviewId = "${reviewDate}_${reviewUserId}"
                    val reviewRef = db.collection("places").document(placeId!!).collection("reviews").document(reviewId)

                    reviewRef.update("comment", updatedContent, "rating", updatedRating, "imageRes", imageUrl)
                        .addOnSuccessListener {
                            val bundle = Bundle()
                            bundle.putString("place_id", placeId)
                            Log.d("placeididid",placeId)
                            mainActivity.navigate(R.id.action_placeReviewModifyFragment_to_placeReviewFragment,bundle)
                            showSnackbar("리뷰가 수정되었습니다.")
                        }
                        .addOnFailureListener { e ->
                            showSnackbar("오류가 발생되었습니다.")
                        }
                }
            } else {
                // 이미지가 선택되지 않았을 경우 (기존 로직으로 동작)
                val reviewId = "${reviewDate}_${reviewUserId}"
                val reviewRef = db.collection("places").document(placeId!!).collection("reviews").document(reviewId)

                reviewRef.update("comment", updatedContent, "rating", updatedRating)
                    .addOnSuccessListener {
                        showSnackbar("리뷰가 수정되었습니다.")
                        val bundle = Bundle()
                        bundle.putString("place_id", placeId)
                        mainActivity.navigate(R.id.action_placeReviewModifyFragment_to_placeReviewFragment,bundle)
                    }
                    .addOnFailureListener { e ->
                        showSnackbar("오류가 발생되었습니다.")
                    }
            }
        }
        return fragmentPlaceReviewModifyBinding.root
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
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
                                showSnackbar("리뷰가 수정되었습니다.")
                            }
                        }
                        .addOnFailureListener {
                            fragmentPlaceReviewModifyBinding.progressBarModify.visibility = View.GONE
                            showSnackbar("이미지 업로드 실패.")
                        }
                }
            })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            fragmentPlaceReviewModifyBinding.imageViewPlaceReviewModify.setImageURI(selectedImageUri)
        }
    }
    fun showProgress() {
        fragmentPlaceReviewModifyBinding.progressBarModify.visibility = View.VISIBLE
        fragmentPlaceReviewModifyBinding.progressBackgroundModify.visibility = View.VISIBLE
    }

    fun hideProgress() {
        fragmentPlaceReviewModifyBinding.progressBarModify.visibility = View.GONE
        fragmentPlaceReviewModifyBinding.progressBackgroundModify.visibility = View.GONE
    }
    private fun showSnackbar(message: String) {
        Snackbar.make(fragmentPlaceReviewModifyBinding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}