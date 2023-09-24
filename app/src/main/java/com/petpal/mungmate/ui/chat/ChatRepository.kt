package com.petpal.mungmate.ui.chat

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.petpal.mungmate.model.ChatRoom
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepository {

    // 채팅방 목록 실시간 로드
    fun startChatRoomListener(userId: String): Flow<List<ChatRoom>> = callbackFlow {
        val chatRooms = mutableListOf<ChatRoom>()

        // 추가, 삭제 감지를 위해 리스너는 chatRooms 컬렉션 전체에 등록
        val chatRoomsRef = Firebase.firestore.collection(ChatRoomRepository.CHAT_ROOMS_NAME)

        // 콜백 리스너 등록
        val listenerRegistration = chatRoomsRef.addSnapshotListener { value, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (value == null || value.isEmpty) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            chatRooms.clear()
            value?.documents?.forEach { document ->
                val chatRoom = document.toObject(ChatRoom::class.java)
                chatRoom?.let { chatRooms.add(it) }
            }

            trySend(chatRooms)
        }

        // Flow가 완료될 때 콜백을 취소
        awaitClose { listenerRegistration.remove() }
    }

    // 사용자 id로 정보 객체 가져오기
    suspend fun getUserInfoById(userId: String): DocumentSnapshot? {
        return try {
            Firebase.firestore.collection(ChatRoomRepository.USERS_NAME)
                .document(userId)
                .get()
                .await()
        } catch (e: Exception) {
            null
        }
    }

}