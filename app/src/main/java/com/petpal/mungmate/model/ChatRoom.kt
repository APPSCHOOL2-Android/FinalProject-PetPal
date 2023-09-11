package com.petpal.mungmate.model

// test
data class ChatRoom (
    val roomName: String,
    val lastMessageText: String,
    val lastMessageTime: String,
    val unReadCount: Long
)