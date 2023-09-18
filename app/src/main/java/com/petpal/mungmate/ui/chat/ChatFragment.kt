package com.petpal.mungmate.ui.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.firebase.Timestamp
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

    private val senderId = "user1"

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
                // FirestoreRecyclerOptions : DB 데이터 변경되면 RecyclerView 실시간 업데이트
                val query = Firebase.firestore.collection("chatRooms")
                    .orderBy("lastMessageTime", Query.Direction.DESCENDING)
                val options = FirestoreRecyclerOptions.Builder<ChatRoom>()
                    .setQuery(query, ChatRoom::class.java)
                    .build()

                adapter = ChatAdapter(options, mainActivity)
                (adapter as ChatAdapter).startListening()

                layoutManager = LinearLayoutManager(requireContext())

                addItemDecoration(
                    MaterialDividerItemDecoration(
                        context,
                        MaterialDividerItemDecoration.VERTICAL
                    )
                )
            }

            // 특정 사용자에게 채팅 보내기 테스트
            buttonSendMessage.setOnClickListener {
                val user2Id = editTextReceiverId.text.toString()
                createChatRoom(user2Id)
            }
        }
    }

    // 새로운 상대에게 메시지 보낼시 채팅방 생성 (아직 메시지 전송x)
    private fun createChatRoom(receiverId: String) {
        // 처음 채팅방 생성하고 메시지 보내기 전까지는 상태 채팅목록에는 표시X, sender만 입장한 상태
        val db = Firebase.firestore

        val chatRoom = ChatRoom(
            senderId,
            receiverId,
            null,
            Timestamp.now(),
            false,
            true,
            0,
            0
        )

        db.collection("chatRooms")
            .document("${senderId}_${receiverId}")
            .set(chatRoom)
            .addOnSuccessListener {
                // 새 채팅방으로 이동
                mainActivity.navigate(R.id.action_mainFragment_to_chat, bundleOf("chatRoomId" to "${senderId}_${receiverId}"))
            }
            .addOnFailureListener {
                Log.d("hhl", "채팅방 생성 실패")
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _fragmentChatBinding = null
    }
}