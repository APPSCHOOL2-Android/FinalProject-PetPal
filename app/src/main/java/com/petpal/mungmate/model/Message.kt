package com.petpal.mungmate.model

import com.google.firebase.Timestamp

// 채팅방 메시지
data class Message(
    val senderId: String? = null,
    val text: String? = null,
    val timestamp: Timestamp? = null,
    @field:JvmField
    var isRead: Boolean? = null,
    val type: String? = null
)