package com.petpal.mungmate.ui

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
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.petpal.mungmate.MainActivity

import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentWalkReviewWriteBinding
import com.petpal.mungmate.model.WalkRecord
import com.petpal.mungmate.model.WalkReview
import java.io.ByteArrayOutputStream
import java.util.UUID


class WalkReviewWriteFragment : Fragment() {
    private lateinit var fragmentWalkReviewWriteBinding: FragmentWalkReviewWriteBinding
    private lateinit var mainActivity: MainActivity
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var selectedImageUri: Uri? = null
    private val auth = Firebase.auth
    private lateinit var userId:String




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentWalkReviewWriteBinding= FragmentWalkReviewWriteBinding.inflate(layoutInflater)
        mainActivity=activity as MainActivity

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

        }

        val user=auth.currentUser
        userId=user!!.uid
        val walkRecorduid=arguments?.getString("walkRecorduid")
        val walkRecordDate=arguments?.getString("walkRecordDate")
        val walkRecordStartTime=arguments?.getString("walkRecordStartTime")
        val walkRecordEndTime=arguments?.getString("walkRecordEndTime")
        val walkDuration=arguments?.getLong("walkDuration") //초단위로 올라감 1분40초 ->walkDuration:100
        val walkDistance=arguments?.getString("walkDistance")
        val walkMatchingId=arguments?.getString("walkMatchingId")
        val walkRecordId=arguments?.getString("walkMatchingRecorId")
        val walkMateNickname=arguments?.getString("mateNickname")
        val walkMatchingSender=arguments?.getString("walkMatchingSender")
        val walkMatchingReceiver=arguments?.getString("walkMatchingReceiver")
        val capturedImageUri = arguments?.getParcelable<Uri>("capturedImageUri")
        Log.d("이미지",capturedImageUri.toString())
        if (walkRecordId != null) {
            Log.d("매칭아이디",walkRecordId)
        }
        if (capturedImageUri != null) {
            uploadOriginalImageToStorage(capturedImageUri) { imageUrl ->
            }
        }

        if(walkMateNickname==null){
            fragmentWalkReviewWriteBinding.imageViewWalk.visibility=View.VISIBLE
            fragmentWalkReviewWriteBinding.userRatingBar.visibility=View.GONE

        }else{
            fragmentWalkReviewWriteBinding.buttonWalkReviewSubmit.text="후기 보내기"
            fragmentWalkReviewWriteBinding.textViewWalkReviewUser1.text="남긴 평점은 상대방의 발바닥 점수에 반영됩니다."
            fragmentWalkReviewWriteBinding.textViewWalkReviewUser.text="$walkMateNickname 님과의 산책은 어떠셨나요?"
        }
        fragmentWalkReviewWriteBinding.imageViewWalk.setOnClickListener {
            selectImageFromGallery()
        }

        fragmentWalkReviewWriteBinding.buttonWalkReviewSubmit.setOnClickListener {
            val walkMemo=fragmentWalkReviewWriteBinding.editTextWalkContent.text.toString()
            val walkPhoto=selectedImageUri
            if(walkMatchingId==null) {
                if (walkPhoto != null) {
                    uploadImageToStorage(walkPhoto) { walkPhoto ->
                        val walkReview = WalkRecord(
                            walkRecorduid!!,
                            walkRecordDate!!,
                            walkRecordStartTime!!,
                            walkRecordEndTime!!,
                            walkDuration!!,
                            walkDistance!!.toDouble(),
                            null,
                            walkMemo,
                            walkPhoto
                        )
                        addWalkReview(userId, walkReview)
                        // 리뷰 등록 후 Navigation 이동
                        mainActivity.navigate(R.id.action_WriteWalkReviewFragment_to_mainFragment)
                    }
                } else {
                    val walkReview = WalkRecord(
                        walkRecorduid!!,
                        walkRecordDate!!,
                        walkRecordStartTime!!,
                        walkRecordEndTime!!,
                        walkDuration!!,
                        walkDistance!!.toDouble(),
                        null,
                        walkMemo
                    )
                    addWalkReview(userId, walkReview)
                    mainActivity.navigate(R.id.action_WriteWalkReviewFragment_to_mainFragment)
                }
            }else{//sender
                if(userId!=walkMatchingId) {
                    if (userId == walkMatchingSender) {
                    val walkWithReview = WalkReview(
                        fragmentWalkReviewWriteBinding.userRatingBar.rating,
                        walkMemo,
                        Timestamp.now()
                    )
                    val myReview = WalkRecord(
                        walkRecorduid!!,
                        walkRecordDate!!,
                        walkRecordStartTime!!,
                        walkRecordEndTime!!,
                        walkDuration!!,
                        walkDistance!!.toDouble(),
                        walkMatchingId,
                        walkMemo
                    )
                    //내 산책기록에 올린다
                    addWalkReview(userId, myReview)
                    //matches의 문서에 sender의 리뷰로 올린다
                    addWalkwithReviewSender(userId, walkRecordId!!, walkWithReview)
                    //매칭상대방의 walkmatereview에 올려준다
                    addWalkReviewToMate(walkMatchingId, walkWithReview)
                    mainActivity.navigate(R.id.action_WriteWalkReviewFragment_to_mainFragment)
                }else {
                        //receiver
                        val myReview=WalkRecord( walkRecorduid!!, walkRecordDate!!, walkRecordStartTime!!, walkRecordEndTime!!, walkDuration!!, walkDistance!!.toDouble(), walkMatchingId, walkMemo)
                        //내 산책기록에 올린다
                        addWalkReview(walkRecordId!!,myReview)
                        val walkWithReview = WalkReview(fragmentWalkReviewWriteBinding.userRatingBar.rating, walkMemo, Timestamp.now())
                        //matches에 receiver의 리뷰로 올린다
                        addWalkwithReviewReceiver(userId,walkRecordId!!,walkWithReview)
                        //매칭상대방의 walkmatereview에 올려준다
                        addWalkReviewToMate(walkMatchingId,walkWithReview)
                        mainActivity.navigate(R.id.action_WriteWalkReviewFragment_to_mainFragment)
                    }
                }
            }
        }


        return fragmentWalkReviewWriteBinding.root
    }

    private fun uploadOriginalImageToStorage(uri: Uri, onSuccess: (String) -> Unit) {
        showProgress()

        val ref = storage.reference.child("captured_images/${UUID.randomUUID()}.jpg")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    hideProgress()
                    onSuccess(it.toString())
                }
            }
            .addOnFailureListener {
                fragmentWalkReviewWriteBinding.progressBarWalk.visibility = View.GONE
                Toast.makeText(context, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
            }
    }
    fun addWalkReview(userId: String, walkReview: WalkRecord) {
        val userRef = db.collection("users").document(userId)

        userRef.update("walkRecordList", FieldValue.arrayUnion(walkReview))
            .addOnSuccessListener {
                Toast.makeText(context, "리뷰가 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->

                Toast.makeText(context, "리뷰 등록 실패 .", Toast.LENGTH_SHORT).show()
            }
    }
    fun addWalkReviewToMate(walkMatchingId: String, walkWithReview: WalkReview) {
        val userRef = db.collection("users").document(walkMatchingId)

        userRef.update("walkMateReview", FieldValue.arrayUnion(walkWithReview))
            .addOnSuccessListener {
                Toast.makeText(context, "리뷰가 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->

                Toast.makeText(context, "리뷰 등록 실패 .", Toast.LENGTH_SHORT).show()
            }
    }
    private fun uploadCapturedImageToStorage(uri: Uri, onSuccess: (String) -> Unit) {
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

                    val ref = storage.reference.child("captured_images/${UUID.randomUUID()}.jpg")
                    ref.putBytes(byteArray)
                        .addOnSuccessListener {
                            ref.downloadUrl.addOnSuccessListener {
                                hideProgress()
                                onSuccess(it.toString())
                            }
                        }
                        .addOnFailureListener {
                            fragmentWalkReviewWriteBinding.progressBarWalk.visibility = View.GONE
                            Toast.makeText(context, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                        }
                }
            })
    }
    fun addWalkwithReviewSender(userId: String,walkRecordId:String, walkWithReview: WalkReview) {
        val userRef = db.collection("matches").document(walkRecordId)
        userRef.update("senderWalkReview", FieldValue.arrayUnion(walkWithReview))
            .addOnSuccessListener {
                Toast.makeText(context, "리뷰가 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->

                Toast.makeText(context, "리뷰 등록 실패 .", Toast.LENGTH_SHORT).show()
            }
    }
    fun addWalkwithReviewReceiver(userId: String,walkRecordId:String, walkWithReview: WalkReview) {
        val userRef = db.collection("matches").document(walkRecordId)

        userRef.update("receiverWalkReview", FieldValue.arrayUnion(walkWithReview))
            .addOnSuccessListener {
                Toast.makeText(context, "리뷰가 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->

                Toast.makeText(context, "리뷰 등록 실패 .", Toast.LENGTH_SHORT).show()
            }
    }



    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE1)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE1) {
            selectedImageUri = data?.data
            fragmentWalkReviewWriteBinding.imageViewWalk.setImageURI(selectedImageUri)
        }
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
                                //showSnackbar("리뷰가 성공적으로 등록되었습니다.")
                                //Toast.makeText(context, "리뷰가 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener {
                            fragmentWalkReviewWriteBinding.progressBarWalk.visibility = View.GONE
                            Toast.makeText(context, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                        }
                }
            })
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(fragmentWalkReviewWriteBinding.root, message, Snackbar.LENGTH_SHORT).show()
    }
    fun showProgress() {
        fragmentWalkReviewWriteBinding.progressBarWalk.visibility = View.VISIBLE
        fragmentWalkReviewWriteBinding.progressBackgroundWalk.visibility = View.VISIBLE
    }

    fun hideProgress() {
        fragmentWalkReviewWriteBinding.progressBarWalk.visibility = View.GONE
        fragmentWalkReviewWriteBinding.progressBackgroundWalk.visibility = View.GONE
    }
    companion object {
        private const val IMAGE_PICK_CODE1 = 1000
    }

}
