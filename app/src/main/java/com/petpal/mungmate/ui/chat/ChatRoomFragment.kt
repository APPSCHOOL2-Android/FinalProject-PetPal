package com.petpal.mungmate.ui.chat

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentChatRoomBinding
import com.petpal.mungmate.model.Message
import com.petpal.mungmate.model.MessageType
import com.petpal.mungmate.model.MessageVisibility
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatRoomFragment : Fragment() {
    private var _fragmentChatRoomBinding : FragmentChatRoomBinding? = null
    private val fragmentChatRoomBinding get() = _fragmentChatRoomBinding!!

    private lateinit var chatViewModel: ChatViewModel
    private lateinit var messageAdapter: MessageAdapter

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    lateinit var chatRoomId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 채팅 목록 화면에서 Bundle로 전달받은 현재 채팅방 id
        chatRoomId = arguments?.getString("chatRoomId")!!

        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        chatViewModel.setCurrentChatRoomId(chatRoomId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentChatRoomBinding = FragmentChatRoomBinding.inflate(inflater)
        return fragmentChatRoomBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // TODO 날짜가 바뀌면 자동으로 DATE 타입 메시지 저장

        fragmentChatRoomBinding.run {

            toolbarChatRoom.run {
                setNavigationOnClickListener {
                    findNavController().popBackStack()
                }

                // toolbar 메뉴 이벤트 리스너
                setOnMenuItemClickListener { menuItem ->
                    when(menuItem.itemId) {
                        // 차단 상태가 아닐때만 보이는 메뉴
                        R.id.menu_item_block -> {
                            blockUser()
                            true
                        }
                        // 차단 상태에서만 보이는 메뉴
                        R.id.menu_item_unblock -> {
                            unblockUser()
                            true
                        }
                        R.id.menu_item_report -> {
                            // 신고하기 화면 이동 (채팅 상대 UID 전달)
                            val action = ChatRoomFragmentDirections.actionChatRoomFragmentToReportUserFragment("user1")
                            findNavController().navigate(action)
                            true
                        }
                        R.id.menu_item_exit -> {
                            exitChatRoom()
                            true
                        }
                        else -> false
                    }
                }
            }

            // 산책 메이트 요청
            buttonRequestWalkMate.setOnClickListener {
                val action = ChatRoomFragmentDirections.actionChatRoomFragmentToWalkMateRequestFragment(currentUserId, "user1", chatRoomId)
                findNavController().navigate(action)
            }

            messageAdapter = MessageAdapter(chatViewModel)

            // 채팅방 메시지 목록
            recyclerViewMessage.apply {
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(TopMarginItemDecoration(8))
                adapter = messageAdapter
            }

            // 메시지 입력, 전송
            editTextMessage.addTextChangedListener {
                buttonSendMessage.isEnabled = it.toString().trim().isNotEmpty()
            }
            buttonSendMessage.setOnClickListener {
                sendTextMessage()
            }

            // Observer
            chatViewModel.run {
                messages.observe(viewLifecycleOwner) { messages ->
                    messageAdapter.setMessages(messages)
                    recyclerViewMessage.scrollToPosition(messages.size - 1)
                }
            }
        }

        chatViewModel.loadMessages(chatRoomId)
    }

    // 시스템 날짜 메시지 저장 
    // todo 텍스트 메시지를 보내는 시점 : 가장 마지막 메시지의 날짜와 다를 경우 날짜 메시지 전송 후 텍스트 메시지 전송하기
    private fun saveDateMessage() {
//        val currentDate = Date()
//        val dateFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
//        val formattedDate = dateFormat.format(currentDate)

        val message = Message(
            currentUserId,
            null,
            Timestamp.now(),
            null,
            MessageType.TEXT.code,
            MessageVisibility.ALL.code
        )
        chatViewModel.saveMessage(chatRoomId, message)
    }
    
    // 메시지 전송
    private fun sendTextMessage(){
        val content = fragmentChatRoomBinding.editTextMessage.text.toString()
        fragmentChatRoomBinding.editTextMessage.text.clear()
        val message = Message(
            currentUserId,
            content,
            Timestamp.now(),
            null,
            MessageType.TEXT.code,
            MessageVisibility.ALL.code
        )
        chatViewModel.saveMessage(chatRoomId, message)
    }

    private fun exitChatRoom() {
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("채팅방 나가기")
            .setMessage("채팅방을 나가면 대화내용이 모두 삭제되고 채팅목록에서도 삭제됩니다.")
            .setPositiveButton("나가기"){ dialogInterface: DialogInterface, i: Int ->
                // TODO 채팅방 나가기
                findNavController().popBackStack()
            }
            .setNegativeButton("취소", null)
            .create()
        builder.show()
    }

    private fun unblockUser() {
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("차단해제")
            .setMessage("차단을 해제하면 다시 채팅을 주고받을 수 있으며 산책 메이트 요청이 가능해집니다.")
            .setPositiveButton("해제하기"){ dialogInterface: DialogInterface, i: Int ->
                // TODO 사용자 차단 해제
                fragmentChatRoomBinding.run {
                    buttonRequestWalkMate.isEnabled = true
                    buttonSendMessage.isEnabled = true
                    editTextMessage.hint = "메시지를 입력하세요."
                    editTextMessage.isEnabled = true
                }
            }
            .setNegativeButton("취소", null)
            .create()
        builder.show()
    }

    private fun blockUser() {
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("차단하기")
            .setMessage("차단한 사용자와는 채팅을 할 수 없으며 산책 메이트를 요청할 수 없습니다.")
            .setPositiveButton("차단하기"){ dialogInterface: DialogInterface, i: Int ->
                // TODO 사용자 차단 -> 채팅 상대 차단 상태 정보 가져와서 적용하도록 수정 예정
                // 산책 메이트 요청, 채팅 불가
                fragmentChatRoomBinding.run {
                    buttonRequestWalkMate.isEnabled = false
                    buttonSendMessage.isEnabled = false
                    editTextMessage.hint = "차단한 사용자와는 채팅할 수 없습니다."
                    editTextMessage.isEnabled = false
                }
            }
            .setNegativeButton("취소", null)
            .create()
        builder.show()
    }

}