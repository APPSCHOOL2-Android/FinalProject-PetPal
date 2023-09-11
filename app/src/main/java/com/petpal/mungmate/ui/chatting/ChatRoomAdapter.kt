package com.petpal.mungmate.ui.chatting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowChatRoomBinding
import com.petpal.mungmate.model.ChatRoom

class ChatRoomAdapter(private val chatRoomList: List<ChatRoom>):RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>() {

    inner class ViewHolder(private val rowBinding: RowChatRoomBinding): RecyclerView.ViewHolder(rowBinding.root){
        fun bind(chatRoom: ChatRoom) {
            rowBinding.run {
                textViewRoomName.text = chatRoom.roomName
                textViewLastMessageText.text= chatRoom.lastMessageText
                textViewLastMessageTime.text = chatRoom.lastMessageTime
                if (chatRoom.unReadCount < 1) {
                    textViewUnreadCount.visibility = View.GONE
                } else {
                    textViewUnreadCount.text = chatRoom.unReadCount.toString()
                }

                Glide.with(itemView)
                    .load(R.drawable.default_profile_image)
                    .into(imageViewUserProfile)
            }

            rowBinding.root.setOnClickListener {
                // 채팅방 이동 (roomId 전달 필요)
                val navController = Navigation.findNavController(itemView)
                navController!!.navigate(R.id.action_item_chat_to_item_chat_room)
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
        return chatRoomList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chatRoomList[position])
    }

}