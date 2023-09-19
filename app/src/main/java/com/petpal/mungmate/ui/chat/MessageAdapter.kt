package com.petpal.mungmate.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.petpal.mungmate.databinding.RowChatReceiveMessageBinding
import com.petpal.mungmate.databinding.RowChatSendMessageBinding
import com.petpal.mungmate.model.Message
import java.lang.IllegalArgumentException

// Recyceler.ViewHolder를 상속받는 자식 클래스 ViewHolder들로 이루어진 리스트를 하나의 RecyclerView로 표시
class MessageAdapter(options: FirestoreRecyclerOptions<Message>): FirestoreRecyclerAdapter<Message, RecyclerView.ViewHolder>(options) {
    private val loginUserId = "user1"

    // 메시지 뷰 식별용 상수
    companion object {
        const val SEND_MESSAGE_VIEW_TYPE = 1
        const val RECEIVE_MESSAGE_VIEW_TYPE = 2
        const val DATE_VIEW_TYPE = 3
        const val WALK_MATE_REQUEST_VIEW_TYPE = 4
        const val WALK_MATE_ACCEPT_VIEW_TYPE = 5
        const val WALK_MATE_REJECT_VIEW_TYPE = 6
    }

    // 보낸 메시지
    inner class SendMessageViewHolder(private val RowChatSendMessageBinding: RowChatSendMessageBinding): RecyclerView.ViewHolder(RowChatSendMessageBinding.root){
        fun bindSendMessage(message: Message){
            RowChatSendMessageBinding.run {
                textViewMessage.text = message.content
                textViewTime.text = message.timestamp.toString()
                // TODO 이전것과 시간이 변경 없을 경우 TextViewTime visibility GONE
            }
        }
    }
    // 받은 메시지
    inner class ReceiveMessageViewHolder(private val RowChatReceiveMessageBinding: RowChatReceiveMessageBinding): RecyclerView.ViewHolder(RowChatReceiveMessageBinding.root){
        fun bindReceiveMessage(message: Message) {
            RowChatReceiveMessageBinding.run {
                textViewMessage.text = message.content
                textViewTime.text = message.timestamp.toString()
                // TODO 이전것과 시간이 변경 없을 경우 TextViewTime visibility GONE
            }
        }
    }

    // viewType에 따라 다른 ViewHolder 생성, 반환 타입은 부모 클래스 ViewHolder로 고정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SEND_MESSAGE_VIEW_TYPE -> {
                val rowBinding = RowChatSendMessageBinding.inflate(LayoutInflater.from(parent.context))

                rowBinding.root.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                SendMessageViewHolder(rowBinding)
            }
            RECEIVE_MESSAGE_VIEW_TYPE -> {
                val rowBinding = RowChatReceiveMessageBinding.inflate(LayoutInflater.from(parent.context))

                rowBinding.root.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                ReceiveMessageViewHolder(rowBinding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Message) {
        val message = getItem(position)
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
        val message = getItem(position)

        /*
        return when(message.type){
            SEND_MESSAGE_VIEW_TYPE -> {
                if (loginUserId == message.senderId) {
                    SEND_MESSAGE_VIEW_TYPE
                } else {

                }
            }
        }
        if (message.type == SEND_MESSAGE_VIEW_TYPE) {
            // 내가 보낸 메시지
            return SEND_MESSAGE_VIEW_TYPE
        }else if (){
            return RECEIVE_MESSAGE_VIEW_TYPE
        }

         */
        return 0
    }
}