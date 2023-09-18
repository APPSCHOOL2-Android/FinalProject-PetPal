package com.petpal.mungmate.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowChatRoomBinding
import com.petpal.mungmate.model.ChatRoom
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChatAdapter(options: FirestoreRecyclerOptions<ChatRoom>, private val activity: MainActivity,
) : FirestoreRecyclerAdapter<ChatRoom, ChatAdapter.ViewHolder>(options) {
    // todo Firebase.Auth로 가져오기
    private val loginUserId = "user1"

    inner class ViewHolder(private val rowBinding: RowChatRoomBinding) : RecyclerView.ViewHolder(rowBinding.root) {
        fun bind(chatRoom: ChatRoom) {
            rowBinding.run {
                // todo receiverId로 User 데이터 조인해서 닉네임으로 설정하기
                textViewReceiverName.text = chatRoom.receiverId
                textViewLastMessageText.text = chatRoom.lastMessage

                // timestamp 오늘, 어제, MM월 dd일 표시
                val timestamp = chatRoom.lastMessageTime
                if (timestamp != null) {
                    val currentTime = Calendar.getInstance().time
                    val timeDifferenceMillis = currentTime.time - timestamp?.toDate()?.time!!

                    // 밀리초를 날짜로 변환
                    val daysAgo = timeDifferenceMillis / (1000 * 60 * 60 * 24)

                    val formatter = SimpleDateFormat("MM월 dd일", Locale.getDefault())
                    textViewLastMessageTime.text = when(daysAgo) {
                        0L -> "오늘"
                        1L -> "어제"
                        else -> formatter.format(timestamp.toDate())
                    }
                }

                // 현재 로그인한 사용자가 해당 채팅방에 대해 senderId일 경우
                if (loginUserId == chatRoom.senderId) {
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

                Glide.with(itemView)
                    .load(R.drawable.default_profile_image)
                    .into(imageViewUserProfile)
            }

            rowBinding.root.setOnClickListener {
                // 채팅방 이동 (roomId 전달 필요)
                activity.navigate(R.id.action_mainFragment_to_chat, bundleOf("chatRoomId" to "${chatRoom.senderId}_${chatRoom.receiverId}"))
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: ChatRoom) {
        holder.bind(model)
    }
}