package com.petpal.mungmate.ui.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowChatRoomBinding
import com.petpal.mungmate.model.ChatRoom
import com.petpal.mungmate.model.FirestoreUserBasicInfoData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChatAdapter(private val chatViewModel: ChatViewModel, private val activity: MainActivity) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    val TAG = "CHAT_ADAPTER"
    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val chatRooms = mutableListOf<ChatRoom>()

    inner class ViewHolder(private val rowBinding: RowChatRoomBinding) : RecyclerView.ViewHolder(rowBinding.root) {
        fun bind(chatRoom: ChatRoom) {
            rowBinding.run {
                // 채팅 참여자중 내가 아닌 상대의 정보 가져오기
                val otherUserId = if (currentUserUid == chatRoom.senderId) chatRoom.receiverId else chatRoom.senderId
                chatViewModel.getUserInfoById(otherUserId) { document ->
                    if (document != null && document.exists()) {
                        val otherUserInfo = document.toObject(FirestoreUserBasicInfoData::class.java)
                        if (otherUserInfo != null) {
                            // 채팅방 제목
                            textViewRoomName.text = otherUserInfo?.nickname
                            // 채팅방 사진
                            val userImageRef = Firebase.storage.reference.child(otherUserInfo.userImage)
                            userImageRef.downloadUrl.addOnSuccessListener { uri ->
                                Glide.with(itemView)
                                    .load(uri)
                                    .into(imageViewUserProfile)
                            }.addOnFailureListener {
                                Log.d(TAG, "chatroom user image load failed")
                            }

                        } else {
                            // 사진 못 불러올 경우 임시로 기본 프사 지정
                            textViewRoomName.text = "이름 없음"
                            Glide.with(itemView)
                                .load(R.drawable.default_profile_image)
                                .into(imageViewUserProfile)
                        }
                    } else {
                        // Document를 찾지 못하거나 오류가 난 경우
                    }
                }

                // 마지막 메시지 TODO 메시지 보낼때마다 실시간 반영하기
                textViewLastMessageText.text = chatRoom.lastMessage

                // 마지막 메시지 시간 TODO 메시지 보낼때마다 실시간 반영하기
                // timestamp 오늘, 어제, MM월 dd일 표시
                val timestamp = chatRoom.lastMessageTime
                val currentTime = Calendar.getInstance().time
                val timeDifferenceMillis = currentTime.time - timestamp?.toDate()?.time!!

                // 밀리초를 날짜로 변환
                val daysAgo = timeDifferenceMillis / (1000 * 60 * 60 * 24)
                // 오늘, 어제, 날짜로 표시 분기
                val formatter = SimpleDateFormat("MM월 dd일", Locale.getDefault())
                textViewLastMessageTime.text = when(daysAgo) {
                    0L -> "오늘"
                    1L -> "어제"
                    else -> formatter.format(timestamp.toDate())
                }

                // 아직 안읽은 메시지 수
                if (currentUserUid == chatRoom.senderId) {
                    // 현재 로그인한 사용자가 해당 채팅방에 대해 senderId일 경우
                    if (chatRoom.senderUnReadCount?.takeIf { it > 1 } != null) {
                        textViewUnreadCount.text = chatRoom.senderUnReadCount.toString()
                    } else {
                        textViewUnreadCount.visibility = View.GONE
                    }
                } else {
                    // 현재 로그인한 사용자가 해당 채팅방에 대해 receiverId일 경우
                    if (chatRoom.receiverUnReadCount?.takeIf { it > 1 } != null) {
                        textViewUnreadCount.text = chatRoom.receiverUnReadCount.toString()
                    } else {
                        textViewUnreadCount.visibility = View.GONE
                    }
                }

                // TODO 채팅방 참가를 participants로 추가, 삭제해서 참가, 퇴장? 둘 다 나가면 채팅방 소멸?
                
            }

            rowBinding.root.setOnClickListener {
                // 채팅방 이동
                // 채팅방 정보의 참여자 중에 내가 아닌 다른 상대방의 id 전달
                if (currentUserUid == chatRoom.senderId) {
                    activity.navigate(R.id.action_mainFragment_to_chat, bundleOf("receiverId" to chatRoom.receiverId))
                } else {
                    activity.navigate(R.id.action_mainFragment_to_chat, bundleOf("receiverId" to chatRoom.senderId))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowBinding = RowChatRoomBinding.inflate(LayoutInflater.from(parent.context))

        rowBinding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return ViewHolder(rowBinding)
    }

    override fun getItemCount(): Int {
        return chatRooms.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chatRooms[position])
    }

    // ViewModel의 observer에서 호출하는 메서드 -> 데이터 세팅
    fun setChatRooms(newChatRooms: List<ChatRoom>) {
        // 내가 참여중인 채팅방만 필터링
        val myChatRooms = newChatRooms.filter { it.senderId == currentUserUid || it.receiverId == currentUserUid }
        chatRooms.clear()
        chatRooms.addAll(myChatRooms)
        notifyDataSetChanged()
    }
}