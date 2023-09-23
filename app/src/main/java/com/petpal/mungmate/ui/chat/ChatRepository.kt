package com.petpal.mungmate.ui.chat

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.petpal.mungmate.model.ChatRoom
import com.petpal.mungmate.model.FirestoreUserBasicInfoData
import com.petpal.mungmate.model.Message
import com.petpal.mungmate.model.UserReport
import com.petpal.mungmate.model.Match
import com.petpal.mungmate.model.MessageType
import com.petpal.mungmate.model.MessageVisibility
import com.petpal.mungmate.model.PetData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "CHAT_REPOSITORY"

class ChatRepository {
    companion object {
        // collection 이름 : 오타 방지, 재사용
        const val CHAT_ROOMS_NAME = "chatRooms"
        const val MESSAGES_NAME = "messages"
        const val MATCHES_NAME = "matches"
        const val REPORTS_NAME = "reports"
        const val USERS_NAME = "users"
        const val PETS_NAME = "pets"

        const val BLOCK_USER_LIST = "blockUserList"

        const val TIMESTAMP = "timestamp"
        const val CHAT_PAGE_SIZE = 100L
    }
    var db = Firebase.firestore

    // todo await() 추가해서 리턴값들 DocumentReference로 통일하기
    // 채팅방에 메시지 추가 = 메시지 전송
    fun saveMessage(chatRoomId: String, message: Message) {
        var messageDocRef = db.collection(CHAT_ROOMS_NAME)
            .document(chatRoomId)
            .collection(MESSAGES_NAME)
            .document()
        message.id = messageDocRef.id
        messageDocRef.set(message).addOnSuccessListener {
            Log.d(TAG, "save message completed")
        }.addOnFailureListener {
            Log.d(TAG, "save message failed")
        }
    }

    // 메시지 목록 실시간 로드
    fun getMessages(chatRoomId: String): Flow<List<Message>> = callbackFlow {
        // 메시지 리스트
        val messages = mutableListOf<Message>()
        // 채팅방 Collection > 채팅방 Document > 채팅방 내 메시지 Collection 참조
        val messagesRef = db.collection(CHAT_ROOMS_NAME)
            .document(chatRoomId)
            .collection(MESSAGES_NAME)
            .orderBy(TIMESTAMP)

        val listenerRegistration = messagesRef.addSnapshotListener { value, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            messages.clear()
            value?.documents?.forEach { document ->
                val message = document.toObject(Message::class.java)
                message?.let { messages.add(it) }
            }
            // Flow 로 값 보내기
            trySend(messages)
        }

        // Flow 종료 시 리스너 등록 해제
        awaitClose { listenerRegistration.remove() }
    }

    // 채팅방 목록 실시간 로드
    fun getChatRooms(userId: String): Flow<List<ChatRoom>> = callbackFlow {
        val chatRooms = mutableListOf<ChatRoom>()

        // 추가, 삭제 감지를 위해 리스너는 chatRooms 컬렉션 전체에 등록
        val chatRoomsRef = db.collection(CHAT_ROOMS_NAME)

        // 콜백 리스너 등록
        val listenerRegistration = chatRoomsRef.addSnapshotListener { value, error ->
            if (error != null) {
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


    // 산책 매칭 저장
    fun saveMatch(match: Match): Task<DocumentReference> {
        val matchesCollection = db.collection(MATCHES_NAME)
        return matchesCollection.add(match)
    }

    // Document Key 값으로 산책 매칭 데이터 가져오기
    suspend fun getMatchById(matchId: String): DocumentSnapshot? {
        return try {
            db.collection(MATCHES_NAME)
                .document(matchId)
                .get()
                .await()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserInfoById(userId: String): DocumentSnapshot? {
        return try {
            db.collection(USERS_NAME)
                .document(userId)
                .get()
                .await()
        } catch (e: Exception) {
            null
        }
    }

    // 사용자 id로 사용자 Document 가져오기
    suspend fun getUserBasicInfoById(userId: String): FirestoreUserBasicInfoData? {
        return try {
            db.collection(USERS_NAME).document(userId).get().await().toObject(FirestoreUserBasicInfoData::class.java)
        } catch (e: Exception) {
            Log.d(TAG, "getUserBasicInfo failed : ${e.printStackTrace()}")
            null
        }
    }

    suspend fun getMainPetInfoByUserId(userId: String): PetData? {
        return try {
            // 대표 반려견으로 첫 번째 Document 가져오기
            val collectionRef = db.collection(USERS_NAME).document(userId).collection(PETS_NAME)
            val petDocument = collectionRef.limit(1).get().await().documents.firstOrNull()
            petDocument?.toObject(PetData::class.java)
        }catch (e: Exception) {
            Log.d(TAG, "getMainPetInfoByUserId failed : ${e.printStackTrace()}")
            null
        }
    }

    // 사용자 신고
    fun saveUserReport(userReport: UserReport): Task<DocumentReference> {
        val userReportsCollection = db.collection(REPORTS_NAME)
        return userReportsCollection.add(userReport)
    }

    // matches 컬렉션 내 문서의 특정 필드 업데이트
    suspend fun updateFieldInMatchDocument(matchKey: String, fieldName: String, updateValue: Any) {
        val matchDocumentRef = db.collection(MATCHES_NAME).document(matchKey)
        val updateData = hashMapOf<String, Any>()
        updateData[fieldName] = updateValue

        try {
            matchDocumentRef.update(updateData).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getOrCreateChatRoom(myUserId: String, receiverId: String): String {
        // 사용자 ID 조합으로 chatroom Key 생성
        val chatRoomKey = listOf(myUserId, receiverId).sorted().joinToString("_")

        // chatroom 존재 확인
        val chatRoomDocRef = db.collection(CHAT_ROOMS_NAME).document(chatRoomKey)
        val chatRoomDocSnapshot = chatRoomDocRef.get().await()

        if (chatRoomDocSnapshot.exists()) {
            // 채팅방이 이미 존재하는 경우, 기존 채팅방의 ID를 반환
            Log.d(TAG, "load chatroom completed")
            return chatRoomKey
        } else {
            // 채팅방이 존재하지 않는 경우, 새로운 채팅방을 생성하고 ID를 반환
            val newChatRoom = ChatRoom(
                myUserId,
                receiverId,
                "",
                Timestamp.now(),
                false,
                false,
                0,
                0
            )
            chatRoomDocRef.set(newChatRoom).await()
            Log.d(TAG, "create chatroom completed")

            // 최초 메시지로 오늘 DATE 전송
            // TODO 채팅 보낼 때 lastMessageTime이 오늘이 아닐 경우 보내는 걸로 나중에 수정하기
            val dateContent = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
                .format(Date())
            
            val message = Message(
                "",
                myUserId,
                dateContent,
                Timestamp.now(),
                true,
                MessageType.DATE.code,
                MessageVisibility.ALL.code
            )
            saveMessage(chatRoomKey, message)
            Log.d(TAG, "send date message completed")

            return chatRoomKey
        }
    }

    // 상대방 차단 상태 반전
    suspend fun toggleBlockStatus(currentUserId: String, receiverId: String) {
        val userRef = db.collection(USERS_NAME).document(currentUserId)
        val blockedUserList = mutableListOf<String>()

        // 현재 차단 목록 가져오기
        val snapshot = userRef.get().await()
        if (snapshot.exists()) {
            val existBlockList = snapshot.get(BLOCK_USER_LIST) as? List<String>
            if (existBlockList != null) {
                blockedUserList.addAll(existBlockList)
            }
        }

        // 이미 차단되어 있는 사용자인지 확인
        if (blockedUserList.contains(receiverId)) {
            // 차단 목록에 추가
            blockedUserList.add(receiverId)
        } else {
            // 차단 목록에서 제거
            blockedUserList.remove(receiverId)
        }

        // 업데이트된 차단 목록을 DB에 저장
        userRef.update(BLOCK_USER_LIST, blockedUserList).await()
    }

    // Firestore 차단 상태 관련 작업을 처리
    suspend fun observeBlockStatus(currentUserId: String, receiverId: String): Flow<BlockStatus> = flow {
        // Firestore에서 사용자의 blockUserList 필드를 실시간으로 감시
        val currentUserDocRef = db.collection(USERS_NAME).document(currentUserId)
        val receiverUserDocRef = db.collection(USERS_NAME).document(receiverId)
        
        // 내 차단 목록 실시간 반영
        val currentUserSnapshot = currentUserDocRef.addSnapshotListener { currentUserSnapshot, error ->
            // 내가 차단 목록에 상대가 있는지 체크, "blockUserList" 필드가 없어서 null인 경우 빈 리스트로 대체
            val currentUserBlockList = currentUserSnapshot?.get(BLOCK_USER_LIST) as? List<String>
            val isBlockedByMe = receiverId in currentUserBlockList.orEmpty()
            
            // 상대의 차단 목록에 내가 있는지 체크
            val receiverUserSnapshotTask = receiverUserDocRef.get()
            val receiverUserSnapshot = Tasks.await(receiverUserSnapshotTask)
            val receiverUserBlockList = receiverUserSnapshot?.get(BLOCK_USER_LIST) as? List<String>

            val isBlockedByReceiver = currentUserId in receiverUserBlockList.orEmpty()

            // BlockStatus 업데이트
            val blockStatus = when {
                isBlockedByMe && isBlockedByReceiver -> BlockStatus.ALL
                isBlockedByMe && !isBlockedByReceiver -> BlockStatus.BLOCKED_BY_ME
                !isBlockedByMe && isBlockedByReceiver -> BlockStatus.BLOCKED_BY_RECEIVER
                else -> BlockStatus.NONE
            }

            emit(blockStatus)
        }

        // 상대 차단 목록 실시간 반영
        val receiverUserSnapshot = receiverUserDocRef.addSnapshotListener { snapshot, error ->
            // 상대의 차단 목록에 내가 있는지 체크
            val receiverUserBlockList = snapshot?.get(BLOCK_USER_LIST) as? List<String>
            val isBlockedByReceiver = currentUserId in receiverUserBlockList.orEmpty()

            // 내 차단 목록에 상대가 있는지 체크
            val currentUserSnapshotTask = currentUserDocRef.get()
            val currentUserSnapshot = Tasks.await(currentUserSnapshotTask)
            val currentUserBlockList = currentUserSnapshot?.get(BLOCK_USER_LIST) as? List<String>

            val isBlockedByMe = receiverId in currentUserBlockList.orEmpty()

            // BlockStatus 업데이트
            val blockStatus = when {
                isBlockedByMe && isBlockedByReceiver -> BlockStatus.ALL
                isBlockedByMe && !isBlockedByReceiver -> BlockStatus.BLOCKED_BY_ME
                !isBlockedByMe && isBlockedByReceiver -> BlockStatus.BLOCKED_BY_RECEIVER
                else -> BlockStatus.NONE
            }

            emit(blockStatus)
        }

        // 더 이상 감시하지 않을 때 listener 제거
        awaitClose {
            currentUserSnapshot.remove()
            receiverUserSnapshot.remove()
        }
    }


    // 현재 시간보다 이전 메시지를 N개 가져오는데 사용, Paging으로 자르기
//    suspend fun receiveMessages(chatRoomId: String, lastTime: Timestamp): List<Message> {
//        return db.collection(CHAT_ROOMS_NAME)
//            .document(chatRoomId)
//            .collection(MESSAGES_NAME)
//            .whereLessThan(TIMESTAMP, lastTime)
//            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
//            .limit(CHAT_PAGE_SIZE)
//            .get()
//            .await()
//            .documents
//            .map { document ->
//                document.let {
//                    document.toObject(Message::class.java)
//                }?: kotlin.run {
//                    Message()
//                }
//            }
//    }
}