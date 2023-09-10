package com.petpal.mungmate.ui.chatting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.petpal.mungmate.databinding.RowReceiveMessageBinding
import com.petpal.mungmate.databinding.RowSendMessageBinding
import com.petpal.mungmate.model.Message
import java.lang.IllegalArgumentException

// Recyceler.ViewHolder를 상속받는 자식 클래스 ViewHolder들로 이루어진 리스트를 하나의 RecyclerView로 표시
class ChatMessageAdapter(private val messageList: List<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // 메시지 뷰 식별용 상수
    companion object {
        const val SEND_MESSAGE_VIEW_TYPE = 1
        const val RECEIVE_MESSAGE_VIEW_TYPE = 2
    }

    // 보낸 메시지
    inner class SendMessageViewHolder(private val rowSendMessageBinding: RowSendMessageBinding): RecyclerView.ViewHolder(rowSendMessageBinding.root){
        fun bindSendMessage(message: Message){
            rowSendMessageBinding.run {
                textViewMessage.text = message.messageText
                textViewTime.text = message.messageTime
                // TODO 이전것과 시간이 변경 없을 경우 TextViewTime visibility GONE
            }
        }
    }
    // 받은 메시지
    inner class ReceiveMessageViewHolder(private val rowReceiveMessageBinding: RowReceiveMessageBinding): RecyclerView.ViewHolder(rowReceiveMessageBinding.root){
        fun bindReceiveMessage(message: Message) {
            rowReceiveMessageBinding.run {
                textViewMessage.text = message.messageText
                textViewTime.text = message.messageTime
                // TODO 이전것과 시간이 변경 없을 경우 TextViewTime visibility GONE
            }
        }
    }

    // viewType에 따라 다른 ViewHolder 생성, 반환 타입은 부모 클래스 ViewHolder로 고정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SEND_MESSAGE_VIEW_TYPE -> {
                val rowBinding = RowSendMessageBinding.inflate(LayoutInflater.from(parent.context))

                rowBinding.root.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                SendMessageViewHolder(rowBinding)
            }
            RECEIVE_MESSAGE_VIEW_TYPE -> {
                val rowBinding = RowReceiveMessageBinding.inflate(LayoutInflater.from(parent.context))

                rowBinding.root.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                ReceiveMessageViewHolder(rowBinding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        // ViewHolder 타입에 따라 형변환해야 각 자식 클래스의 bind 메서드 호출 가능 
        when(holder){
            is SendMessageViewHolder -> {
                holder.bindSendMessage(message)
            }
            is ReceiveMessageViewHolder -> {
                holder.bindReceiveMessage(message)
            }
        }
    }

    // 객체 멤버변수 값에 따라 ViewHolder 타입 구분
    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.isSendMessage) {
            SEND_MESSAGE_VIEW_TYPE
        } else {
            RECEIVE_MESSAGE_VIEW_TYPE
        }
    }
}