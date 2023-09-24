package com.petpal.mungmate.utils

import android.content.Context
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.petpal.mungmate.R

//url로 storage에서 이미지 가져와서 imageView에 띄워주기
fun setUserImageFromStorage(
    context: Context,
    userImageUrl: String?,
    imageView: ImageView,
) {
    if (userImageUrl?.startsWith('/') == true) {
        val storageRef = Firebase.storage.reference
        val userImageReference = storageRef.child(userImageUrl)

        //storage 경로라면 storage에서 이미지 넣어주기
        Log.w("commentUserImage", "storage, ${userImageReference.path}")
        userImageReference.downloadUrl.addOnSuccessListener {
            Glide.with(context)
                .load(it)
                .fallback(R.drawable.default_profile_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(imageView)
        }
    } else {
        Log.w("commentUserImage", "url")
        Glide
            .with(context)
            .load(userImageUrl)
            .fallback(R.drawable.default_profile_image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .into(imageView)
    }
}