package com.petpal.mungmate.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

// 이미지를 로드하고 크기를 조정하여 ImageView에 설정하는 함수
fun loadAndResizeImageFromUri(
    context: Context,
    uri: Uri,
    targetImageView: ImageView,
    targetWidth: Int,
    targetHeight: Int,
) {
    Glide.with(context)
        .asDrawable()
        .load(uri)
        .override(targetWidth, targetHeight) // 원하는 크기로 조정
        .into(object : CustomTarget<Drawable>() {
            override fun onLoadCleared(placeholder: Drawable?) {
                // 이미지가 제거될 때 호출되는 부분
            }

            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                // 이미지 로드가 완료되면 호출되는 부분
                targetImageView.setImageDrawable(resource)
            }
        })
}

fun resizeAndCropBitmap(inputBitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
//    val originalWidth = inputBitmap.width
//    val originalHeight = inputBitmap.height
//
//    // 원본 이미지 비율 계산
//    val scaleWidth = newWidth.toFloat() / originalWidth
//    val scaleHeight = newHeight.toFloat() / originalHeight
//    val scaleFactor = if (scaleWidth < scaleHeight) scaleWidth else scaleHeight
//
//    // 비율을 유지하면서 크기 조정
//    val finalWidth = (originalWidth * scaleFactor).toInt()
//    val finalHeight = (originalHeight * scaleFactor).toInt()
//
//    val matrix = Matrix()
//    matrix.postScale(scaleFactor, scaleFactor)
//
//    // 크기 조정
//    val resizedBitmap = Bitmap.createBitmap(inputBitmap, 0, 0, originalWidth, originalHeight, matrix, true)
//
//    // 이미지를 중앙에서 자르기
//    val startX = maxOf((finalWidth - newWidth) / 2, 0)
//    val startY = maxOf((finalHeight - newHeight) / 2, 0)
//
//    // 자를 영역의 폭과 높이를 조정
//    val cropWidth = minOf(newWidth, finalWidth)
//    val cropHeight = minOf(newHeight, finalHeight)

//    return Bitmap.createBitmap(resizedBitmap, startX, startY, cropWidth, cropHeight)
    return Bitmap.createScaledBitmap(inputBitmap, newWidth, newHeight, true)
}

////갤러리에서 사진 불러오는 함수: bitmap, uri를 이용한 후작업은 callback에서 처리
//fun Fragment.loadImageFromGallery(callback: (Bitmap, Uri) -> Unit) {
//
//    val galleryLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//
//            if (it.resultCode == AppCompatActivity.RESULT_OK && it.data?.data != null) {
//                val uri = it.data?.data!!
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    val source =
//                        ImageDecoder.createSource(requireActivity().contentResolver, uri)
//
//                    val bitmap = ImageDecoder.decodeBitmap(source)
//                    callback(bitmap, uri)
//                } else {
//                    val cursor =
//                        requireActivity().contentResolver.query(uri, null, null, null, null)
//                    if (cursor != null) {
//                        cursor.moveToNext()
//
//                        val colIdx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
//                        val source = cursor.getString(colIdx)
//
//                        val bitmap = BitmapFactory.decodeFile(source)
//                        callback(bitmap, uri)
//                    }
//                }
//            }
//
//        }
//
//    //앨범에서 사진을 선택할 수 있는 액티비티 실행
//    val galleryIntent =
//        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
//            type = "image/*"
//            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
//        }
//
//    galleryLauncher.launch(galleryIntent)
//}

//launcher 세팅
fun Fragment.gallerySetting(callback: (Bitmap, Uri) -> Unit): ActivityResultLauncher<Intent> {
    return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        if (it.resultCode == AppCompatActivity.RESULT_OK && it.data?.data != null) {
            val uri = it.data?.data!!

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val source =
                    ImageDecoder.createSource(requireActivity().contentResolver, uri)

                val bitmap = ImageDecoder.decodeBitmap(source)
                callback(bitmap, uri)
            } else {
                val cursor =
                    requireActivity().contentResolver.query(uri, null, null, null, null)
                if (cursor != null) {
                    cursor.moveToNext()

                    val colIdx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                    val source = cursor.getString(colIdx)

                    val bitmap = BitmapFactory.decodeFile(source)
                    callback(bitmap, uri)
                }
            }
        }

    }
}

//갤러리 실행해서 사진 받아오기
fun launchGallery(galleryLauncher: ActivityResultLauncher<Intent>) {
    //앨범에서 사진을 선택할 수 있는 액티비티 실행
    val galleryIntent =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
        }

    galleryLauncher.launch(galleryIntent)
}