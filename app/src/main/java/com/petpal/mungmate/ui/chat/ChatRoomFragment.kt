package com.petpal.mungmate.ui.chat

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentChatRoomBinding
import com.petpal.mungmate.model.Message

class ChatRoomFragment : Fragment() {
    private var _fragmentChatRoomBinding : FragmentChatRoomBinding? = null
    private val fragmentChatRoomBinding get() = _fragmentChatRoomBinding!!

    private var messageList = mutableListOf<Message>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentChatRoomBinding = FragmentChatRoomBinding.inflate(inflater)
        return fragmentChatRoomBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 임시 데이터 세팅
        setSampleData()

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
                            // TODO 신고하기 화면 이동 (채팅 상대 UID 전달)
                            findNavController().navigate(R.id.action_chatRoomFragment_to_reportUserFragment)
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
                findNavController().navigate(R.id.action_chatRoomFragment_to_walkMateRequestFragment)

            }

            // 채팅방 메시지 목록
            recyclerViewMessage.run {
                adapter = ChatMessageAdapter(messageList)
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
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
                    textInputLayoutMessage.endIconDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.send_24px)
                    textInputEditTextMessage.hint = "메시지를 입력하세요."
                    textInputEditTextMessage.isEnabled = true
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
                    textInputLayoutMessage.endIconDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.person_off_24px)
                    textInputEditTextMessage.hint = "차단한 사용자와는 채팅할 수 없습니다."
                    textInputEditTextMessage.isEnabled = false
                }
            }
            .setNegativeButton("취소", null)
            .create()
        builder.show()
    }

    private fun setSampleData() {
        messageList = mutableListOf(
            Message("안녕하세요!", "오후 10:00", false),
            Message("멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍장문테스트멍멍멍멍멍멍멍멍멍멍", "오후 10:01", true),
            Message("같이 산책하실래요?", "오후 10:02", false),
            Message("좋아요ㅎㅎ", "오후 10:03", true),
            Message("그럼 몇시에 만날까요?", "오후 10:04", true),
            Message("테스트메시지입니다.", "오후 10:05", false),
            Message("테스트메시지입니다.", "오후 10:06", false)
        )
    }
}