package com.petpal.mungmate.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
    var uriString: String? = null,
    var fileName: String? = null
): Parcelable
