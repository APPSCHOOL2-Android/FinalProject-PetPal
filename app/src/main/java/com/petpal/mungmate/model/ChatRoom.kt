package com.petpal.mungmate.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

// 채팅방
data class ChatRoom (
    val participants: List<String>,
    val senderId: String,
    val receiverId: String,
    val lastMessage: String?,
    val lastMessageTime: Timestamp?,
    val senderIsExit: Boolean,
    val receiverIsExit: Boolean,
    val senderUnReadCount: Long?,
    val receiverUnReadCount: Long?
) {
    constructor() : this(listOf(), "", "", null, null, false, false, null, null)
}