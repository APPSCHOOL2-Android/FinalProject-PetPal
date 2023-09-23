package com.petpal.mungmate.model

import com.google.firebase.Timestamp

// 채팅방 메시지
data class Message(
    var id: String,
    val senderId: String,
    val content: String,
    val timestamp: Timestamp,
    val receiverIsRead: Boolean,
    val type: Int,
    val visible: Int
) {
    constructor(): this("", "", "", Timestamp.now(), true, MessageType.TEXT.code, MessageVisibility.ALL.code)
}

// 각기 다른 뷰를 가지는 메시지 유형
enum class MessageType(val code: Int) {
    TEXT(0),                // 채팅 텍스트
    DATE(1),                // 날짜
    WALK_MATE_REQUEST(2),   // 산책 메이트 요청
    WALK_MATE_ACCEPT(3),    // 산책 메이트 수락
    WALK_MATE_REJECT(4)     // 산책 메이트 거절
}

// 메시지 표시 여부 : 해당 메시지를 볼 수 있는 사용자
enum class MessageVisibility(val code: Int){
    ALL(0),             // 전체
    ONLY_SENDER(1),     // SENDER 사용자
    ONLY_RECEIVER(2),   // RECEIVER 사용자
    NONE(3)             // 표시 안 함
}