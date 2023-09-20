package com.petpal.mungmate.ui.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentChatBinding
import com.petpal.mungmate.model.ChatRoom
import com.petpal.mungmate.model.Message
import com.petpal.mungmate.model.MessageType
import com.petpal.mungmate.model.MessageVisibility
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                // FirestoreRecyclerOptions : DB 데이터 변경되면 RecyclerView 실시간 업데이트
                // 로그인 유저가 참여하고 있는 채팅방, 최신순 정렬
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

            // 특정 사용자에게 채팅 보내기 테스트용
            buttonTestSendMessage.setOnClickListener {
                val receiverUserId = editTextReceiverId.text.toString()
                enterChatRoom(receiverUserId)
            }
        }
    }

    // 채팅방 입장
    // 새로운 상대에게 메시지 보낼시 채팅방 생성 (아직 메시지 전송x)
    private fun enterChatRoom(receiverId: String) {
        // 처음 채팅방 생성하고 메시지 보내기 전까지는 상태 채팅목록에는 표시X, sender만 입장한 상태
        val db = Firebase.firestore

        val chatRoomId = listOf<String>(currentUserId, receiverId).sorted().joinToString("_")
        db.collection("chatRoom").document(chatRoomId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // 문서가 존재하는 경우
                    mainActivity.navigate(R.id.action_mainFragment_to_chat, bundleOf("chatRoomId" to chatRoomId))
                } else {
                    // 문서가 존재하지 않는 경우
                    val chatRoom = ChatRoom(
                        currentUserId,
                        receiverId,
                        null,
                        Timestamp.now(),
                        false,
                        true,
                        0,
                        0
                    )
                    // TODO 이후 chatRoomId 난수 생성 키로 사용하는 걸로 변경하기
                    db.collection("chatRooms")
                        .document(chatRoomId)
                        .set(chatRoom)
                        .addOnSuccessListener {
                            // 새 채팅방으로 이동
                            mainActivity.navigate(R.id.action_mainFragment_to_chat, bundleOf("chatRoomId" to chatRoomId))
                        }
                        .addOnFailureListener {
                            Log.d("hhl", "채팅방 생성 실패")
                        }

                    // 기본으로 날짜 메시지 전송
                    sendDateMessage(chatRoomId)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("hhl", "Error getting document: $exception")
            }
    }

    private fun sendDateMessage(chatRoomId: String) {
        val date = Date()
        val sdf = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        val formattedDate = sdf.format(date)

        val message = Message(
            currentUserId,
            formattedDate,
            Timestamp.now(),
            true,
            MessageType.DATE.code,
            MessageVisibility.ALL.code
        )

        chatViewModel.saveMessage(chatRoomId, message)
    }

    override fun onDestroy() {
        super.onDestroy()
        _fragmentChatBinding = null
    }
}