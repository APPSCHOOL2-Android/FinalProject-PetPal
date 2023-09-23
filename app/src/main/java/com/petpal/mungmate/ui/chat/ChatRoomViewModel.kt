package com.petpal.mungmate.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.petpal.mungmate.model.FirestoreUserBasicInfoData
import com.petpal.mungmate.model.Message
import com.petpal.mungmate.model.UserReport
import com.petpal.mungmate.model.Match
import com.petpal.mungmate.model.PetData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "CHAT_ROOM_VIEW_MODEL"

class ChatRoomViewModel: ViewModel() {

    var chatRepository = ChatRepository()
    
    // 현재 채팅방 ID
    private val _currentChatRoomId = MutableLiveData<String>()
    val currentChatRoomId get() = _currentChatRoomId

    // 현재 채팅방의 메시지 목록
    private val _messages = MutableLiveData<List<Message>>()
    val messages : LiveData<List<Message>> get() = _messages

    // 두 사용자 간의 차단 상태 저장
    private val _isBlocked = MutableLiveData<Boolean>()
    val isBlocked: LiveData<Boolean> get() = _isBlocked

    // TODO 차단 각자 관리
    private val _isBlockedByMe = MutableLiveData<Boolean>()
    val isBlockedByMe: LiveData<Boolean> get() = _isBlockedByMe
    private val _isBlockedByReceiver = MutableLiveData<Boolean>()
    val isBlockedByReceiver: LiveData<Boolean> get() = _isBlockedByReceiver

    // 채팅 상대 정보
    private val _receiverUserId = MutableLiveData<String>()
    val receiverUserId: LiveData<String> get() = _receiverUserId

    private val _receiverUserInfo = MutableLiveData<FirestoreUserBasicInfoData>()
    val receiverUserInfo: LiveData<FirestoreUserBasicInfoData> get() = _receiverUserInfo

    private val _receiverPetInfo = MutableLiveData<PetData>()
    val receiverPetInfo: LiveData<PetData> get() = _receiverPetInfo

    // 현재 입장한 채팅방 id 설정
    fun getChatRoom(user1Id: String, user2Id: String) {
        viewModelScope.launch {
            val chatRoomId: String = withContext(Dispatchers.IO) {
                chatRepository.getOrCreateChatRoom(user1Id, user2Id)
            }
            _currentChatRoomId.value = chatRoomId
            Log.d(TAG, "currentRoomId updated")
        }
    }

    // 참여자 중 한명이라도 상대를 차단했는지 여부 체크
    fun checkBlockedStatus(myUserId: String, receiverId: String) {
        viewModelScope.launch {
            val result = chatRepository.checkBlockedStatus(myUserId, receiverId)
            _isBlocked.postValue(result)
        }
    }

    // 상대가 나를 차단했는지 여부 체크 - 채팅방에서 내가 차단을 풀었어도 상대가 안풀었으면 차단 유지
    fun checkBlockStatus(myBlocked: Boolean, myUserId: String, receiverId: String) {
        viewModelScope.launch {
            val isBlockedByReceiver = chatRepository.checkBlockedByReceiver(myUserId, receiverId)
            _isBlocked.postValue(myBlocked || isBlockedByReceiver)
        }
    }

    // 채팅방 Document에 메시지 저장
    fun sendMessage(chatRoomId: String, message: Message){
        chatRepository.saveMessage(chatRoomId, message)
    }

    fun getMessages(chatRoomId: String) {
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
    fun getMatchByKey(matchKey: String, onComplete: (DocumentSnapshot?) -> Unit) {
        viewModelScope.launch {
            val document = withContext(Dispatchers.IO){
                chatRepository.getMatchById(matchKey)
            }
            onComplete(document)
        }
    }

    fun setReceiverUser(userId: String){
        _receiverUserId.value = userId
    }

    // 채팅 상대 기본 정보 가져오기
    fun getReceiverInfoById(userId: String) {
        viewModelScope.launch {
            val userBasicInfoData = chatRepository.getUserBasicInfoById(userId)
            if (userBasicInfoData != null) {
                _receiverUserInfo.value = userBasicInfoData!!
                Log.d(TAG, "ReceiverUserInfo updated: ${userBasicInfoData.nickname}")
            } else {
                // 오류 처리
                Log.d(TAG, "ReceiverUserInfo failed")
            }
        }
    }
    
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

    // 사용자를 차단 목록에 추가
    fun blockUser(myUserId: String, blockedUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.addUserToBlockList(myUserId, blockedUserId)
            checkBlockStatus(true, myUserId, blockedUserId)
        }
    }
    
    // 사용자를 차단 목록에서 제거
    fun unblockUser(myUserId: String, unblockedUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.removeUserFromBlockList(myUserId, unblockedUserId)
            checkBlockStatus(false, myUserId, unblockedUserId)
        }
    }
}
