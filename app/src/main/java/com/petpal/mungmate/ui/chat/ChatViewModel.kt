package com.petpal.mungmate.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.petpal.mungmate.model.ChatRoom
import com.petpal.mungmate.model.FirestoreUserBasicInfoData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel: ViewModel() {
    private val TAG = "CHAT_VIEW_MODEL"
    private val chatRepository = ChatRepository()

    // 참여중인 채팅방 목록
    private val _chatRooms = MutableLiveData<List<ChatRoom>>()
    val chatRooms : LiveData<List<ChatRoom>> get() = _chatRooms

    // 참여중인 채팅방 목록 실시간 로드
    fun getChatRooms(userId: String) {
        viewModelScope.launch {
            chatRepository.getChatRooms(userId)
                .collect { chatRoomList ->
                    _chatRooms.value = chatRoomList
                    Log.d(TAG, "loadChatRooms completed")
                }
        }
    }

    fun getUserInfoById(otherUserId: String, onComplete: (DocumentSnapshot?) -> Unit) {
        viewModelScope.launch {
            val document = withContext(Dispatchers.IO){
                chatRepository.getUserInfoById(otherUserId)
            }
            onComplete(document)
        }
    }

}