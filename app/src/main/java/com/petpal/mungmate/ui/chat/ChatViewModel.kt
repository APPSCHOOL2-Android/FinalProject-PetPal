package com.petpal.mungmate.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.petpal.mungmate.model.Message
import com.petpal.mungmate.model.UserReport
import com.petpal.mungmate.model.Match
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel: ViewModel() {

    private val TAG = "CHAT_VIEW_MODEL"
    var chatRepository = ChatRepository()
    // var savedMessages: MutableLiveData<List<Message>> = MutableLiveData()

    private val _messages = MutableLiveData<List<Message>>()
    val messages : LiveData<List<Message>> get() = _messages

    // 채팅방 Document에 메시지 저장
    fun saveMessage(chatRoomId: String, message: Message){
        chatRepository.saveMessage(chatRoomId, message).addOnFailureListener {
            Log.d(TAG, "메시지 저장 성공")
        }.addOnFailureListener { 
            Log.d(TAG, "메시지 저장 실패")
        }
    }

    fun loadMessages(chatRoomId: String) {
        viewModelScope.launch {
            chatRepository.getMessages(chatRoomId)
                .collect { messageList ->
                    _messages.value = messageList
                }
        }
    }

    // 산책 매칭 데이터 저장 후 Key 반환
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

    // Document Key 값으로 산책 매칭 데이터 가져오기
    fun getMatchByKey(matchKey: String, onComplete: (DocumentSnapshot?) -> Unit) {
        viewModelScope.launch {
            val document = withContext(Dispatchers.IO){
                chatRepository.getMatchByKey(matchKey)
            }
            onComplete(document)
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

    // 매칭 데이터 업데이트
    fun updateFieldInMatchDocument(matchKey: String, fieldName: String, updatedValue: Any) {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.updateFieldInMatchDocument(matchKey, fieldName, updatedValue)
        }
    }

    // 채팅방 Document의 모든 메시지 로드
//    fun getSavedMessages(chatRoomId: String): MutableLiveData<List<Message>> {
//        chatRepository.getSavedMessages(chatRoomId).addSnapshotListener { value, error ->
//            if (error != null) {
//                Log.w(TAG, "실시간 리스너 실패", error)
//                savedMessages.value = listOf()  // null
//                return@addSnapshotListener
//            }
//
//            var savedMessageList: MutableList<Message> = mutableListOf()
//            for (doc in value!!) {
//                var message = doc.toObject(Message::class.java)
//                savedMessageList.add(message)
//            }
//            savedMessages.value = savedMessageList
//        }
//
//        return savedMessages
//    }

}