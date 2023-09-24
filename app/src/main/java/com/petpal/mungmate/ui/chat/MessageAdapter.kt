package com.petpal.mungmate.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.petpal.mungmate.databinding.RowChatDateBinding
import com.petpal.mungmate.databinding.RowChatReceiveMessageBinding
import com.petpal.mungmate.databinding.RowChatSendMessageBinding
import com.petpal.mungmate.databinding.RowChatWalkMateAcceptBinding
import com.petpal.mungmate.databinding.RowChatWalkMateRejectBinding
import com.petpal.mungmate.databinding.RowChatWalkMateRequestReceiveBinding
import com.petpal.mungmate.databinding.RowChatWalkMateRequestSendBinding
import com.petpal.mungmate.model.Match
import com.petpal.mungmate.model.MatchStatus
import com.petpal.mungmate.model.Message
import com.petpal.mungmate.model.MessageType
import com.petpal.mungmate.model.MessageVisibility
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.Locale

// Recyceler.ViewHolder를 상속받는 자식 클래스 ViewHolder들로 이루어진 리스트를 하나의 RecyclerView로 표시
class MessageAdapter(private val chatRoomViewModel: ChatRoomViewModel): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!
    private val messages = mutableListOf<Message>()

    companion object {
        const val VIEW_TYPE_SEND_TEXT = 0
        const val VIEW_TYPE_RECEIVE_TEXT = 1
        const val VIEW_TYPE_DATE = 2
        const val VIEW_TYPE_WALK_MATE_REQUEST_SEND = 3
        const val VIEW_TYPE_WALK_MATE_REQUEST_RECEIVE = 4
        const val VIEW_TYPE_WALK_MATE_ACCEPT = 5
        const val VIEW_TYPE_WALK_MATE_REJECT = 6
    }

    // viewType에 따라 다른 ViewHolder 생성, 반환 타입은 부모 클래스 ViewHolder로 고정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SEND_TEXT -> {
                val rowBinding = RowChatSendMessageBinding.inflate(LayoutInflater.from(parent.context))
                rowBinding.root.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                SendTextViewHolder(rowBinding)
            }
            VIEW_TYPE_RECEIVE_TEXT -> {
                val rowBinding = RowChatReceiveMessageBinding.inflate(LayoutInflater.from(parent.context))
                rowBinding.root.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                ReceiveTextViewHolder(rowBinding)
            }
            VIEW_TYPE_DATE -> {
                val rowBinding = RowChatDateBinding.inflate(LayoutInflater.from(parent.context))
                rowBinding.root.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                DateViewHolder(rowBinding)
            }
            VIEW_TYPE_WALK_MATE_REQUEST_SEND -> {
                val rowBinding = RowChatWalkMateRequestSendBinding.inflate(LayoutInflater.from(parent.context))
                rowBinding.root.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                WalkMateRequestSendViewHolder(rowBinding)
            }
            VIEW_TYPE_WALK_MATE_REQUEST_RECEIVE -> {
                val rowBinding = RowChatWalkMateRequestReceiveBinding.inflate(LayoutInflater.from(parent.context))
                rowBinding.root.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                WalkMateRequestReceiveViewHolder(rowBinding)
            }
            VIEW_TYPE_WALK_MATE_ACCEPT -> {
                val rowBinding = RowChatWalkMateAcceptBinding.inflate(LayoutInflater.from(parent.context))
                rowBinding.root.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                WalkMateAcceptViewHolder(rowBinding)
            }
            VIEW_TYPE_WALK_MATE_REJECT -> {
                val rowBinding = RowChatWalkMateRejectBinding.inflate(LayoutInflater.from(parent.context))
                rowBinding.root.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                WalkMateRejectViewHolder(rowBinding)
            }
            else -> throw IllegalArgumentException("Unkown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when(holder) {
            is SendTextViewHolder -> holder.bind(message)
            is ReceiveTextViewHolder -> holder.bind(message)
            is DateViewHolder -> holder.bind(message)
            is WalkMateRequestSendViewHolder -> holder.bind(message)
            is WalkMateRequestReceiveViewHolder -> holder.bind(message)
            is WalkMateAcceptViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    // 메시지 타입에 따라 ViewHolder 타입 구분
    override fun getItemViewType(position: Int): Int {
        val message = messages[position]

        return when (message.type) {
            MessageType.TEXT.code -> {
                if (message.senderId == currentUserId) {
                    VIEW_TYPE_SEND_TEXT
                } else {
                    VIEW_TYPE_RECEIVE_TEXT
                }
            }
            MessageType.DATE.code -> VIEW_TYPE_DATE
            MessageType.WALK_MATE_REQUEST_SEND.code -> VIEW_TYPE_WALK_MATE_REQUEST_SEND
            MessageType.WALK_MATE_REQUEST_RECEIVE.code -> VIEW_TYPE_WALK_MATE_REQUEST_RECEIVE
            MessageType.WALK_MATE_ACCEPT.code -> VIEW_TYPE_WALK_MATE_ACCEPT
            MessageType.WALK_MATE_REJECT.code -> VIEW_TYPE_WALK_MATE_REJECT
            else -> 0
        }
    }

    // ViewModel에서 messages.value가 바뀌면 이걸 감지한 observer에서 setMessages() 호출해서 RecyclerView DataSet 변경
    fun setMessages(newMessages: List<Message>) {
        messages.clear()
        // 채팅방에서 내가 어느 사용자인지에 따라 보일 메시지 제한
        val senderId = chatRoomViewModel.currentChatRoom.value?.senderId!!
        val messageVisibility = if (currentUserId == senderId) {
            MessageVisibility.ONLY_SENDER
        }else  {
            MessageVisibility.ONLY_RECEIVER
        }

        // 모두 OR 내가 볼 수 있는 메시지만 표시
        val myMessages = newMessages
            .filter { it.visible == MessageVisibility.ALL.code || it.visible == messageVisibility.code }
        messages.addAll(myMessages)
        notifyDataSetChanged()
    }

    // 보낸 메시지
    inner class SendTextViewHolder(private val rowChatSendMessageBinding: RowChatSendMessageBinding): RecyclerView.ViewHolder(rowChatSendMessageBinding.root){
        fun bind(message: Message){
            rowChatSendMessageBinding.run {
                textViewMessage.text = message.content
                textViewTime.text = formatFirebaseTimestamp(message.timestamp!!, "a hh:mm")
            }
        }
    }
    // 받은 메시지
    inner class ReceiveTextViewHolder(private val rowChatReceiveMessageBinding: RowChatReceiveMessageBinding): RecyclerView.ViewHolder(rowChatReceiveMessageBinding.root){
        fun bind(message: Message) {
            rowChatReceiveMessageBinding.run {
                textViewMessage.text = message.content
                textViewTime.text = formatFirebaseTimestamp(message.timestamp!!, "a hh:mm")
            }
        }
    }

    // Firebase Timestamp 타입을 포맷 패턴의 문자열로 변환
    private fun formatFirebaseTimestamp(timestamp: Timestamp, format: String): String {
        val date = timestamp.toDate()
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(date)
    }

    // 날짜
    inner class DateViewHolder(private val rowChatDateBinding: RowChatDateBinding): RecyclerView.ViewHolder(rowChatDateBinding.root) {
        fun bind(message: Message) {
            rowChatDateBinding.run {
                val date = message.timestamp?.toDate()
                val sdf = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
                textViewDate.text = sdf.format(date)
            }
        }
    }
    
    // 산책 요청 송신
    inner class WalkMateRequestSendViewHolder(private val rowBinding: RowChatWalkMateRequestSendBinding): RecyclerView.ViewHolder(rowBinding.root){
        fun bind(message: Message){
            // 산책 일정 표시
            val contents = message.content.split("|")
            val matchDateTime = contents[0]
            val matchPlace = contents[1]

            rowBinding.run {
                textViewRequestDateTime.text = matchDateTime
                textViewRequestPlace.text = matchPlace
            }
        }
    }

    // 산책 요청 수신
    inner class WalkMateRequestReceiveViewHolder(private val rowBinding: RowChatWalkMateRequestReceiveBinding): RecyclerView.ViewHolder(rowBinding.root){
        fun bind(message: Message) {
            rowBinding.run {
                // 산책 일정 표시
                val contents = message.content.split("|")
                val matchDateTime = contents[0]
                val matchPlace = contents[1]

                rowBinding.run {
                    textViewRequestDateTime.text = matchDateTime
                    textViewRequestPlace.text = matchPlace
                }

                val chatRoomId = chatRoomViewModel.currentChatRoom.value?.id!!
                val matchId = message.matchId!!

                buttonAccept.setOnClickListener {
                    // 매칭 상태 변경 -> 수락
                    chatRoomViewModel.updateFieldInMatchDocument(matchId, "status", MatchStatus.ACCEPTED.code)

                    // 산책 메이트 요청 메시지 숨기기
                    chatRoomViewModel.hideMessage(chatRoomId, message.id)

                    // 산책 메이트 수락 메시지 전송
                    val message = Message(
                        "",
                        currentUserId,
                        message.content,
                        Timestamp.now(),
                        false,
                        MessageType.WALK_MATE_ACCEPT.code,
                        MessageVisibility.ALL.code,
                    )
                    val chatRoomId = chatRoomViewModel.currentChatRoom.value?.id!!
                    chatRoomViewModel.sendMessage(chatRoomId, message)
                }

                buttonReject.setOnClickListener {
                    // 매칭 상태 변경 -> 거절
                    chatRoomViewModel.updateFieldInMatchDocument(matchId, "status", MatchStatus.REJECTED.code)

                    // 산책 메이트 요청 메시지 숨기기
                    chatRoomViewModel.hideMessage(chatRoomId, message.id)
                    
                    // 산책 메이트 거절 메시지 전송
                    val message = Message(
                        "",
                        currentUserId,
                        message.content,
                        Timestamp.now(),
                        false,
                        MessageType.WALK_MATE_REJECT.code,
                        MessageVisibility.ALL.code
                    )
                    val chatRoomId = chatRoomViewModel.currentChatRoom.value?.id!!
                    chatRoomViewModel.sendMessage(chatRoomId, message)
                }
            }
        }
    }

    // 산책 수락
    inner class WalkMateAcceptViewHolder(private val rowBinding: RowChatWalkMateAcceptBinding): RecyclerView.ViewHolder(rowBinding.root) {
        fun bind(message: Message) {
            rowBinding.run {
                val contents = message.content.split("|")
                val matchDateTime = contents[0]
                val matchPlace = contents[1]

                rowBinding.run {
                    textViewAcceptDate.text = matchDateTime
                    textViewAcceptPlace.text = matchPlace
                }
            }
        }
    }

    // 산책 거절
    inner class WalkMateRejectViewHolder(private val rowBinding: RowChatWalkMateRejectBinding): RecyclerView.ViewHolder(rowBinding.root){ }
}