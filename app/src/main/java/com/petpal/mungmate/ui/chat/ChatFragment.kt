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

        fragmentChatBinding.run {
            recyclerViewChatRoom.run {
                // firebaseUI 라이브러리 사용해서 firestore를 RecyclerView에 바인딩
                // 로그인 유저가 참여하고 있는 채팅방, 최신순 정렬
                val query = Firebase.firestore
                    .collection("chatRooms")
                    .whereArrayContains("participants", currentUserId)
                    .orderBy("lastMessageTime", Query.Direction.DESCENDING)

                // FirestoreRecyclerOptions : DB 데이터 변경되면 RecyclerView 실시간 업데이트
                val options = FirestoreRecyclerOptions.Builder<ChatRoom>()
                    .setQuery(query, ChatRoom::class.java)
                    .build()

                adapter = ChatAdapter(options, mainActivity)
                (adapter as ChatAdapter).startListening()

                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(MaterialDividerItemDecoration(context, MaterialDividerItemDecoration.VERTICAL))
            }

            // 특정 사용자에게 채팅 보내기 테스트용
            buttonTestSendMessage.setOnClickListener {
                val receiverId = editTextReceiverId.text.toString()
                mainActivity.navigate(R.id.action_mainFragment_to_chat, bundleOf("receiverId" to receiverId))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _fragmentChatBinding = null
    }
}