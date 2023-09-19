package com.petpal.mungmate.ui.community


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.SyncStateContract.Constants
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.ProductRegistrationAdapter
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentCommunityWritingBinding
import com.petpal.mungmate.model.Image
import com.petpal.mungmate.model.Post
import com.petpal.mungmate.model.PostImage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class CommunityWritingFragment : Fragment() {

    lateinit var communityWritingBinding: FragmentCommunityWritingBinding
    lateinit var mainActivity: MainActivity

    // 이미지 최대 5개
    private var mainImageList = mutableListOf<Pair<Image, Bitmap>>()

    // 갤러리 실행
    lateinit var mainGalleryLauncher: ActivityResultLauncher<Intent>
    private var photoSelectedUri: Uri? = null
    private val postImagesList: MutableList<PostImage> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        communityWritingBinding = FragmentCommunityWritingBinding.inflate(inflater)
        bottomNavigationViewGone()
        mainGalleryLauncher = mainImageGallerySetting()
        mainActivity = activity as MainActivity

        communityWritingBinding.run {
            toolbar()

            communityWritingImageButton.setOnClickListener {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                        type = "image/*"
                        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
                    }
                mainGalleryLauncher.launch(galleryIntent)
            }

            communityWritingPostImageHorizontalRecyclerView.run {
                adapter = ProductRegistrationAdapter(mainImageList, communityWritingImageButton)
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }


        }

        return communityWritingBinding.root
    }


    private fun bottomNavigationViewGone() {
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.visibility = View.GONE
    }

    private fun FragmentCommunityWritingBinding.toolbar() {
        communityWritingToolbar.run {
            inflateMenu(R.menu.community_writing_menu)
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener {
                it.findNavController()
                    .popBackStack()
            }
            setOnMenuItemClickListener {

                when (it?.itemId) {

                    R.id.item_complete -> {

                        val post = Post(
                            "",
                            0,
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            emptyList(),
                            "",
                            0,
                            emptyList()
                        )
                        if (
                            communityPostWritingTitleTextInputEditText.text.toString().isEmpty() ||
                            categoryItem.text.toString().isEmpty() ||
                            communityPostWritingContentTextInputEditText.text.toString().isEmpty()
                        ) {
                            Snackbar.make(
                                communityWritingBinding.communityPostWritingTitleTextInputEditText,
                                "제목, 카테고리, 내용을 입력해주세요!",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else {
                            saveFirestore(post)
                        }

                    }

                }
                false
            }
        }
    }

    private fun mainImageGallerySetting(): ActivityResultLauncher<Intent> {
        val galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                // 메인 이미지 최대 5개까지만 첨부 가능
                if (mainImageList.count() < 5) {
                    if (it.resultCode == AppCompatActivity.RESULT_OK && it.data?.data != null) {
                        photoSelectedUri = it.data?.data!!
                        var bitmap: Bitmap? = null

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val source =
                                ImageDecoder.createSource(
                                    mainActivity.contentResolver,
                                    photoSelectedUri!!
                                )
                            bitmap = ImageDecoder.decodeBitmap(source)
                        } else {
                            val cursor =
                                mainActivity.contentResolver.query(
                                    photoSelectedUri!!,
                                    null,
                                    null,
                                    null,
                                    null
                                )
                            if (cursor != null) {
                                cursor.moveToNext()
                                val colIdx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                                val source = cursor.getString(colIdx)
                                bitmap = BitmapFactory.decodeFile(source)
                            }
                        }
                        // 메인 이미지 리스트에 추가
                        val pairImageInfo = Image(photoSelectedUri.toString(), "") to bitmap!!
                        mainImageList.add(pairImageInfo)
                        communityWritingBinding.communityWritingImageButton.text =
                            "${mainImageList.size}/5"
                        communityWritingBinding.communityWritingPostImageHorizontalRecyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }
        return galleryLauncher
    }

    private fun saveFirestore(post: Post) {
        val db = FirebaseFirestore.getInstance()
        communityWritingBinding.progressBar.visibility = View.VISIBLE

        // 사진 유무 확인
        val hasImages = mainImageList.isNotEmpty()

        db.collection("Post")
            .add(post)
            .addOnSuccessListener { documentReference ->
                val generatedDocId = documentReference.id
                val currentDateTime = Date()
                val formattedDateTime = formatDateTimeToNewFormat(currentDateTime)

                if (hasImages) {
                    // 사진이 있을 때
                    uploadImage { eventPost ->
                        val postImage = PostImage(eventPost.photoUrl)
                        postImagesList.add(postImage)

                        if (eventPost.isSuccess) {
                            val updatedData = Post(
                                generatedDocId,
                                0,
                                "https://cotieshop.co.kr/wp-content/uploads/2021/09/%ED%8E%AB%EC%86%8C%EC%8B%9C%ED%81%AC_Tiny-dog-collar%EA%B0%95%EC%95%84%EC%A7%80%EB%B0%98%EB%8B%A4%EB%82%98-Mimi-Mini_thumb002.jpg",
                                "데이터 없음",
                                "데이터 없음",
                                communityWritingBinding.communityPostWritingTitleTextInputEditText.text.toString(),
                                communityWritingBinding.categoryItem.text.toString(),
                                formattedDateTime,
                                postImagesList,
                                communityWritingBinding.communityPostWritingTitleTextInputEditText.text.toString(),
                                0,
                                emptyList()
                            )

                            val documentRef = db.collection("Post").document(generatedDocId)
                            documentRef.set(updatedData)
                                .addOnSuccessListener {
                                    Snackbar.make(
                                        communityWritingBinding.communityPostWritingTitleTextInputEditText,
                                        "게시글 등록 성공",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener {
                                    Snackbar.make(
                                        communityWritingBinding.communityPostWritingTitleTextInputEditText,
                                        "게시글 등록 실패",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnCompleteListener {
                                    val navController = findNavController()
                                    navController.popBackStack()
                                }
                        } else {
                            Snackbar.make(
                                communityWritingBinding.communityPostWritingTitleTextInputEditText,
                                "사진 업로드 실패", // 실패 메시지를 원하는 내용으로 변경
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    // 사진이 없을 때
                    val updatedData = Post(
                        generatedDocId,
                        0,
                        "https://cotieshop.co.kr/wp-content/uploads/2021/09/%ED%8E%AB%EC%86%8C%EC%8B%9C%ED%81%AC_Tiny-dog-collar%EA%B0%95%EC%95%84%EC%A7%80%EB%B0%98%EB%8B%A4%EB%82%98-Mimi-Mini_thumb002.jpg",
                        "데이터 없음",
                        "데이터 없음",
                        communityWritingBinding.communityPostWritingTitleTextInputEditText.text.toString(),
                        communityWritingBinding.categoryItem.text.toString(),
                        formattedDateTime,
                        postImagesList,
                        communityWritingBinding.communityPostWritingTitleTextInputEditText.text.toString(),
                        0,
                        emptyList()
                    )

                    val documentRef = db.collection("Post").document(generatedDocId)
                    documentRef.set(updatedData)
                        .addOnSuccessListener {
                            Snackbar.make(
                                communityWritingBinding.communityPostWritingTitleTextInputEditText,
                                "게시글 등록 성공",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener {
                            Snackbar.make(
                                communityWritingBinding.communityPostWritingTitleTextInputEditText,
                                "게시글 등록 실패",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        .addOnCompleteListener {
                            val navController = findNavController()
                            navController.popBackStack()
                        }
                }
            }
            .addOnFailureListener {
                Snackbar.make(
                    communityWritingBinding.communityPostWritingTitleTextInputEditText,
                    "게시글 등록 실패",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
    }

    private fun uploadImage(callback: (EventPost) -> Unit) {

        val storageRef = FirebaseStorage.getInstance().reference.child("post")
        val eventPost = EventPost()
        eventPost.documentId = FirebaseFirestore.getInstance().collection("Post").document().id

        val folderRef = storageRef.child("${eventPost.documentId!!}")

        for ((index, pair) in mainImageList.withIndex()) {
            val (image, bitmap) = pair

            val photoRef = folderRef.child("$index.jpg")

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            photoRef.putBytes(byteArray)
                .addOnSuccessListener {
                    it.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                        Log.i("URL 정보", downloadUri.toString())
//                        val imageUrl = downloadUri.toString()
//                        val postImage = PostImage(imageUrl)
//                        postImagesList = postImagesList + postImage
//                        Log.i("URL 정보", postImagesList.toString())
                        eventPost.isSuccess = true
                        eventPost.photoUrl = downloadUri.toString()
                        callback(eventPost)
                    }
                }
                .addOnFailureListener {
                    eventPost.isSuccess = false
                    callback(eventPost)
                }
        }
    }

    private fun formatDateTimeToNewFormat(date: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }
}