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
import java.io.InputStream


fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    var inputStream: InputStream? = null
    try {
        // URI에서 InputStream을 열기
        inputStream = context.contentResolver.openInputStream(uri)

        // InputStream을 사용하여 비트맵 디코딩
        return BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        inputStream?.close()
    }
    return null
}


//launcher 세팅
fun Fragment.gallerySetting(callback: (Bitmap) -> Unit): ActivityResultLauncher<Intent> {
    return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        if (it.resultCode == AppCompatActivity.RESULT_OK && it.data?.data != null) {
            val uri = it.data?.data!!

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val source =
                    ImageDecoder.createSource(requireActivity().contentResolver, uri)

                val bitmap = ImageDecoder.decodeBitmap(source)
                callback(bitmap)
            } else {
                val cursor =
                    requireActivity().contentResolver.query(uri, null, null, null, null)
                if (cursor != null) {
                    cursor.moveToNext()

                    val colIdx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                    val source = cursor.getString(colIdx)

                    val bitmap = BitmapFactory.decodeFile(source)
                    callback(bitmap)
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