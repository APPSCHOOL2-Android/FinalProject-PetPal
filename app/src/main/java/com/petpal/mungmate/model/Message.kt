package com.petpal.mungmate.model

import com.google.firebase.Timestamp

// 채팅방 메시지
data class Message(
    val senderId: String? = null,
    val content: String? = null,
    val timestamp: Timestamp? = null,
    @field:JvmField
    var isRead: Boolean? = null,
    val type: Int? = null,
    var visibility: Int? = null
)

// 각기 다른 뷰를 가지는 메시지 유형
enum class MessageType {
    TEXT,
    DATE,
    WALK_MATE_REQUEST,
    WALK_MATE_ACCEPT,
    WALK_MATE_REJECT
}
