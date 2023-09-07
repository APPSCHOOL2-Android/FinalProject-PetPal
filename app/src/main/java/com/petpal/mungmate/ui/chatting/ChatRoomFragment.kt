package com.petpal.mungmate.ui.chatting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentChatRoomBinding
import com.petpal.mungmate.model.Message

class ChatRoomFragment : Fragment() {
    private var _fragmentChatRoomBinding : FragmentChatRoomBinding? = null
    private val fragmentChatRoomBinding get() = _fragmentChatRoomBinding!!

    val messageList = mutableListOf<Message>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentChatRoomBinding = FragmentChatRoomBinding.inflate(inflater)
        return fragmentChatRoomBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSampleData()

        fragmentChatRoomBinding.run {
            toolbarChatRoom.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            // 산책 메이트 요청
            buttonRequestWalkMate.setOnClickListener {
                findNavController().navigate(R.id.action_item_chat_room_to_item_walk_mate_request)
            }

            recyclerViewMessage.run {
                adapter = MessageListAdapter(messageList)
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    private fun setSampleData() {
        messageList.addAll(arrayOf(
            Message("안녕하세요!", "오후 10:00", false),
            Message("멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍장문테스트멍멍멍멍멍멍멍멍멍멍", "오후 10:01", true),
            Message("같이 산책하실래요?", "오후 10:02", false),
            Message("좋아요ㅎㅎ", "오후 10:03", true),
            Message("그럼 몇시에 만날까요?", "오후 10:04", true),
            Message("테스트메시지입니다.", "오후 10:05", false),
            Message("테스트메시지입니다.", "오후 10:06", false)
        ))
    }
}