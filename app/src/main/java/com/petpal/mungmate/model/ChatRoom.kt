package com.petpal.mungmate.model

// UI용 임시 데이터 클래스
data class ChatRoom (
    val roomName: String,
    val lastMessageText: String,
    val lastMessageTime: String,
    val unReadCount: Long
)