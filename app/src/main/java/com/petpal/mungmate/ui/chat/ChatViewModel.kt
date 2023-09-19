package com.petpal.mungmate.ui.chat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.petpal.mungmate.model.Message
import com.petpal.mungmate.model.UserReport
import com.petpal.mungmate.model.Match

class ChatViewModel: ViewModel() {

    private val TAG = "CHAT_VIEW_MODEL"
    var chatRepository = ChatRepository()
    var savedMessages: MutableLiveData<List<Message>> = MutableLiveData()

    // 채팅방 Document에 메시지 저장
    fun saveMessage(chatRoomId: String, message: Message){
        chatRepository.saveMessage(chatRoomId, message).addOnFailureListener {
            Log.d(TAG, "메시지 저장 성공")
        }.addOnFailureListener { 
            Log.d(TAG, "메시지 저장 실패")
        }
    }
    
    // 채팅방 Document의 모든 메시지 로드
    fun getSavedMessages(chatRoomId: String): MutableLiveData<List<Message>> {
        chatRepository.getSavedMessages(chatRoomId).addSnapshotListener { value, error ->
            if (error != null) {
                Log.w(TAG, "실시간 리스너 실패", error)
                savedMessages.value = listOf()  // null
                return@addSnapshotListener
            }

            var savedMessageList: MutableList<Message> = mutableListOf()
            for (doc in value!!) {
                var message = doc.toObject(Message::class.java)
                savedMessageList.add(message)
            }
            savedMessages.value = savedMessageList
        }

        return savedMessages
    }

    // 산책 매칭 데이터 저장
    fun saveMatch(match: Match): Task<String> {
        return chatRepository.saveMatch(match)
            .continueWith { task ->
                if (task.isSuccessful) {
                    task.result?.id ?: throw Exception("Failed to get document key.")
                } else {
                    throw task.exception ?: Exception("Failed to add document.")
                }
            }
    }

    // 사용자 신고 데이터 저장
    fun saveReport(userReport: UserReport) {
        chatRepository.saveUserReport(userReport).addOnSuccessListener {
            Log.d(TAG, "사용자 신고 저장 성공")
        }.addOnFailureListener {
            Log.d(TAG, "사용자 신고 저장 성공")
        }
    }
}