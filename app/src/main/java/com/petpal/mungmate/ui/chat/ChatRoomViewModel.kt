package com.petpal.mungmate.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.petpal.mungmate.model.ChatRoom
import com.petpal.mungmate.model.FirestoreUserBasicInfoData
import com.petpal.mungmate.model.Message
import com.petpal.mungmate.model.UserReport
import com.petpal.mungmate.model.Match
import com.petpal.mungmate.model.MessageVisibility
import com.petpal.mungmate.model.PetData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "CHAT_ROOM_VIEW_MODEL"

class ChatRoomViewModel: ViewModel() {

    var chatRepository = ChatRepository()
    
    // 현재 채팅방 ID
//    private val _currentChatRoomId = MutableLiveData<String>()
//    val currentChatRoomId get() = _currentChatRoomId

    private val _currentChatRoom = MutableLiveData<ChatRoom>()
    val currentChatRoom: LiveData<ChatRoom> get() = _currentChatRoom

    // 현재 채팅방의 메시지 목록
    private val _messages = MutableLiveData<List<Message>>()
    val messages : LiveData<List<Message>> get() = _messages

    // 사용자 정보
    private val _currentUserInfo = MutableLiveData<FirestoreUserBasicInfoData>()
    val currentUserInfoData: LiveData<FirestoreUserBasicInfoData> get() = _currentUserInfo

    private val _receiverUserInfo = MutableLiveData<FirestoreUserBasicInfoData>()
    val receiverUserInfoData: LiveData<FirestoreUserBasicInfoData> get() = _receiverUserInfo

    private val _receiverPetInfo = MutableLiveData<PetData>()
    val receiverPetInfo: LiveData<PetData> get() = _receiverPetInfo

    // 현재 입장한 채팅방 id 설정
    fun getOrCreateChatRoom(user1Id: String, user2Id: String) {
        viewModelScope.launch {
            val chatRoom = withContext(Dispatchers.IO) {
                chatRepository.getOrCreateChatRoom(user1Id, user2Id)
            }
            _currentChatRoom.value = chatRoom
            Log.d(TAG, "currentChatRoom updated")
        }
    }

    // 채팅방 Document에 메시지 저장
    fun sendMessage(chatRoomId: String, message: Message){
        chatRepository.saveMessage(chatRoomId, message)
    }

    // 채팅방 메시지 가져오기
    fun startObservingMessages(chatRoomId: String) {
        viewModelScope.launch {
            chatRepository.getMessages(chatRoomId)
                .collect { messageList ->
                    _messages.value = messageList
                    Log.d(TAG, "loadMessages completed")
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
    fun getMatchById(matchKey: String, onComplete: (DocumentSnapshot?) -> Unit) {
        viewModelScope.launch {
            val document = withContext(Dispatchers.IO){
                chatRepository.getMatchById(matchKey)
            }
            onComplete(document)
        }
    }

//    fun getChatRoomById(chatRoomId: String, onComplete: (DocumentSnapshot?) -> Unit) {
//        viewModelScope.launch {
//            val document = withContext(Dispatchers.IO) {
//                chatRepository.getChatRoomById(chatRoomId)
//            }
//            onComplete(document)
//        }
//    }

    // 채팅 상대 대표 반려견 정보 가져오기
    fun getReceiverPetInfoByUserId(userId: String) {
         viewModelScope.launch {
             val petData = chatRepository.getMainPetInfoByUserId(userId)
             if (petData != null) {
                 _receiverPetInfo.value = petData!!
                 Log.d(TAG, "ReceiverPetInfo updated: ${petData.name}")
             } else {
                 // 오류 처리
                 Log.d(TAG, "ReceiverPetInfo failed")
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

    // 매칭 데이터 업데이트
    fun updateFieldInMatchDocument(matchKey: String, fieldName: String, updatedValue: Any) {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.updateFieldInMatchDocument(matchKey, fieldName, updatedValue)
        }
    }

    // 상대 차단 상태 전환 (차단 -> 해제 / 해제 -> 차단)
//    fun toggleBlockStatus(currentUserId: String, receiverId: String) {
//        viewModelScope.launch {
//            chatRepository.toggleBlockStatus(currentUserId, receiverId)
//        }
//    }

    fun blockUser(currentUserId: String, receiverId: String) {
        viewModelScope.launch {
            chatRepository.addUserToBlockList(currentUserId, receiverId)
        }
    }

    fun unblockUser(currentUserId: String, receiverId: String) {
        viewModelScope.launch {
            chatRepository.removeUserFromBlockList(currentUserId, receiverId)
        }
    }

    // 실시간 차단 및 프로필 변경 감시용
    fun startObservingReceiverUserInfo(userId: String) {
        viewModelScope.launch {
            chatRepository.getUserBasicInfo(userId)
                .collect { userInfo ->
                   _receiverUserInfo.value = userInfo
                }
        }
    }
    // 실시간 차단 감시용
    fun startObservingCurrentUserInfo(userId: String) {
        viewModelScope.launch { 
            chatRepository.getUserBasicInfo(userId)
                .collect { userInfo ->
                    _currentUserInfo.value = userInfo
                }
        }
    }

    fun hideMessage(messageId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.updateFieldInMessageDocument(messageId, "visible", MessageVisibility.NONE.code)
        }

    }
}

// 차단 상태
enum class BlockStatus {
    ALL,                    // 상호 차단
    BLOCKED_BY_ME,          // 나만 상대를 차단
    BLOCKED_BY_RECEIVER,    // 상대만 나를 차단
    NONE                    // 상호 차단 안함
}