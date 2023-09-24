package com.petpal.mungmate.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentChatBinding
import com.petpal.mungmate.model.ChatRoom

class ChatFragment : Fragment() {

    private var _fragmentChatBinding: FragmentChatBinding? = null
    private val fragmentChatBinding get() = _fragmentChatBinding!!

    lateinit var mainActivity: MainActivity

    lateinit var chatViewModel: ChatViewModel
    private lateinit var chatAdapter: ChatAdapter

    private var currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentChatBinding = FragmentChatBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        return fragmentChatBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatViewModel.run {
            chatRooms.observe(viewLifecycleOwner) { chatRooms ->
                // 채팅방 데이터로 RecyclerView Adapter 데이터 세팅
                chatAdapter.setChatRooms(chatRooms)
                fragmentChatBinding.recyclerViewChatRoom.scrollToPosition(0)
            }
        }

        // 현재 로그인 사용자가 참여한 채팅방 가져와서 ViewModel에 세팅
        chatViewModel.getChatRooms(currentUserId)
        // RecyclerView Adapter 생성
        chatAdapter = ChatAdapter(chatViewModel, mainActivity)

        fragmentChatBinding.run {
            recyclerViewChatRoom.run {
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(MaterialDividerItemDecoration(context, MaterialDividerItemDecoration.VERTICAL))
                adapter = chatAdapter
            }

            // 특정 사용자에게 채팅 보내기 테스트용
            buttonTestSendMessage.setOnClickListener {
                val receiverId = editTextReceiverId.text.toString()
                mainActivity.navigate(R.id.action_mainFragment_to_chat, bundleOf("receiverId" to receiverId))
            }

            toolbarChat.setOnClickListener {
                if (chatTestLayout.visibility == View.VISIBLE) {
                    chatTestLayout.visibility = View.GONE
                } else {
                    chatTestLayout.visibility == View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _fragmentChatBinding = null
    }
}