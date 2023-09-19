package com.petpal.mungmate.ui.chat

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.petpal.mungmate.model.Message
import com.petpal.mungmate.model.UserReport
import com.petpal.mungmate.model.Match

class ChatRepository {
    companion object {
        const val CHAT_ROOMS_NAME = "chatRooms"
        const val MESSAGES_NAME = "messages"
        const val MATCHES_NAME = "matches"
        const val USER_REPORTS_NAME = "userReports"
    }

    val TAG = "CHAT_REPOSITORY"
    var db = Firebase.firestore
    var currentUserId = FirebaseAuth.getInstance().currentUser

    // 채팅방에 메시지 추가
    fun saveMessage(chatRoomId: String, message: Message): Task<DocumentReference> {
        var messagesCollection = db.collection(CHAT_ROOMS_NAME)
            .document(chatRoomId)
            .collection(MESSAGES_NAME)
        return messagesCollection.add(message)
    }

    // 채팅방의 모든 메시지 로드
    fun getSavedMessages(chatRoomId: String): CollectionReference {
        val messagesCollection = db.collection("${CHAT_ROOMS_NAME}/${chatRoomId}/${MESSAGES_NAME}")
        return messagesCollection
    }

    // 산책 매칭 저장
    fun saveMatch(match: Match): Task<DocumentReference> {
        val matchesCollection = db.collection(MATCHES_NAME)
        return matchesCollection.add(match)
    }

    // 사용자 신고
    fun saveUserReport(userReport: UserReport): Task<DocumentReference> {
        val userReportsCollection = db.collection(USER_REPORTS_NAME)
        return userReportsCollection.add(userReport)
    }

    fun subscribeMessages(){

    }
}