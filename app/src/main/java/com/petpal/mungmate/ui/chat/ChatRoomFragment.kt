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
import com.petpal.mungmate.ui.pet.PetSex
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChatRoomFragment : Fragment() {
    private var _fragmentChatRoomBinding : FragmentChatRoomBinding? = null
    private val fragmentChatRoomBinding get() = _fragmentChatRoomBinding!!

    private lateinit var chatViewModel: ChatViewModel

    private lateinit var messageAdapter: MessageAdapter

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()  // 현재 사용자 id
    lateinit var receiverId: String     // 채팅 상대 id
    lateinit var chatRoomId: String     // 채팅방 id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 채팅 -> 채팅방 : 채팅 목록 화면에서 Bundle로 전달받은 현재 채팅방 id
        // chatRoomId = arguments?.getString("chatRoomId")!!

        // 사용자 프로필 -> 채팅방 : 사용자 프로필 시트에서 Bundle로 전달받은 상대 user id
        receiverId = arguments?.getString("receiverId")!!

        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
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

        // Observer
        chatViewModel.run {
            // 채팅방 세팅
            currentChatRoomId.observe(viewLifecycleOwner) { currentChatRoomId ->
                chatRoomId = currentChatRoomId

                // 메세지 목록 로드
                chatViewModel.loadMessages(chatRoomId)
                // 사용자 프로필 표시
                chatViewModel.getReceiverInfoById(receiverId)

                // 반려견 정보 표시
                chatViewModel.getReceiverPetInfoByUserId(receiverId)
            }

            receiverUserInfo.observe(viewLifecycleOwner) { userBasicInfoData ->
                fragmentChatRoomBinding.textViewUserNickName.text = userBasicInfoData.nickname
                //

//                fragmentChatRoomBinding.imageViewUserProfile.setImageBitmap()
            }

            receiverPetInfo.observe(viewLifecycleOwner) { petData ->
                val petGender = when(petData.petSex) {
                    PetSex.MALE.ordinal -> "남"
                    else -> "여"
                }
                // 생일 -> 현재 나이 계산
                val petAge = calculateAgeFromBirthDay(petData.birth)
                fragmentChatRoomBinding.textViewUserDogInfo.text = "${petData.name}(${petData.breed}, ${petGender}), $petAge"
            }

            messages.observe(viewLifecycleOwner) { messages ->
                messageAdapter.setMessages(messages)
                fragmentChatRoomBinding.recyclerViewMessage.scrollToPosition(messages.size - 1)
            }
        }

        // 두 사용자 정보로 채팅방 찾아서 정보 로드
        chatViewModel.getChatRoom(currentUserId, receiverId)

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
                            val action = ChatRoomFragmentDirections.actionChatRoomFragmentToReportUserFragment(receiverId, chatViewModel.receiverUserInfo.value?.nickname!!)
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
                val action = ChatRoomFragmentDirections.actionChatRoomFragmentToWalkMateRequestFragment(currentUserId, receiverId, chatRoomId)
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
            editTextMessage.run {
                // 공백 또는 엔터만 입력시에는 전송 버튼 비활성화
                addTextChangedListener {
                    buttonSendMessage.isEnabled = it.toString().trim().isNotEmpty()
                }
                // EditText에 포커스가 주어지면 RecyclerView를 스크롤하여 키보드 위로 밀기
                setOnFocusChangeListener { v, hasFocus ->
                    recyclerViewMessage.post {
                        recyclerViewMessage.scrollToPosition(messageAdapter.itemCount - 1)
                    }
                }
            }

            buttonSendMessage.setOnClickListener {
                sendTextMessage()
            }
        }
    }

    // 생일(yyyy-MM-dd)로 나이 계산 : n세, n개월
    private fun calculateAgeFromBirthDay(birthday: String): String {
        // String -> Date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val birthDate = dateFormat.parse(birthday) ?: return "Invalid Date"

        // Date -> Calendar
        val currentCalender = Calendar.getInstance()
        val birthCalendar = Calendar.getInstance()
        birthCalendar.time = birthDate

        // 현재 날짜, 생년월일 날짜 비교
        var ageYear = currentCalender.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
        var ageMonth = currentCalender.get(Calendar.MONTH) - birthCalendar.get(Calendar.MONTH)

        // 만 나이 계산시 현재 날짜의 일(day)와 생년월일의 일(day)을 비교해 생일이 지났는지 체크
        // 오늘 시점 생일이 지나지 않았을 시 개월 수 감소 
        if (currentCalender.get(Calendar.DAY_OF_MONTH) < birthCalendar.get(Calendar.DAY_OF_MONTH)) {
            ageMonth--
        }
        
        // 월이 음수인 경우 연도를 내려서 보정
        if(ageMonth < 0) {
            ageYear--
            ageMonth += 12
        }
        
        // 1년 미만일 경우 개월 수
        if (ageYear == 0){
            return "${ageMonth}개월"
        }
        
        // 1년 이상일 경우 나이
        return "${ageYear}세"
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
            false,
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
            false,
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