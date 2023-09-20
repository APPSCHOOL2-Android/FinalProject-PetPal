package com.petpal.mungmate.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

// 이미지를 로드하고 크기를 조정하여 ImageView에 설정하는 함수
fun loadAndResizeImage(
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