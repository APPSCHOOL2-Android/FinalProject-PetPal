package com.petpal.mungmate.ui.chat

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentChatRoomBinding
import com.petpal.mungmate.model.FirestoreUserBasicInfoData
import com.petpal.mungmate.model.Message
import com.petpal.mungmate.model.MessageType
import com.petpal.mungmate.model.MessageVisibility
import com.petpal.mungmate.ui.pet.PetSex
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val TAG = "CHAT_ROOM"

class ChatRoomFragment : Fragment() {

    private var _fragmentChatRoomBinding : FragmentChatRoomBinding? = null
    private val fragmentChatRoomBinding get() = _fragmentChatRoomBinding!!

    private lateinit var chatRoomViewModel: ChatRoomViewModel
    private lateinit var messageAdapter: MessageAdapter

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()  // 현재 사용자 id
    lateinit var receiverId: String     // 채팅 상대 id
    lateinit var chatRoomId: String     // 채팅방 id

    private var currentUserInfo: FirestoreUserBasicInfoData? = null
    private var receiverUserInfo: FirestoreUserBasicInfoData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 사용자 프로필 -> 채팅방 : 사용자 프로필 시트에서 Bundle로 전달받은 상대 user id
        receiverId = arguments?.getString("receiverId")!!

        chatRoomViewModel = ViewModelProvider(this)[ChatRoomViewModel::class.java]
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
        chatRoomViewModel.run {
            // 채팅방 세팅
            currentChatRoomId.observe(viewLifecycleOwner) { currentChatRoomId ->
                // 현재 채팅방 id 저장
                chatRoomId = currentChatRoomId

                // 메세지 목록 로드
                chatRoomViewModel.startObservingMessages(chatRoomId)
            }

            currentUserInfoData.observe(viewLifecycleOwner) { userInfoData ->
                currentUserInfo = userInfoData
                // 상호 차단 여부에 따라 ui 변경
                updateUIBasedOnBlockStatus()
            }

            receiverUserInfoData.observe(viewLifecycleOwner) { userInfoData ->
                receiverUserInfo = userInfoData

                // 닉네임
                fragmentChatRoomBinding.textViewUserNickName.text = userInfoData.nickname
                // 채팅 상대 프로필 이미지
                val userImageRef = Firebase.storage.reference.child(userInfoData.userImage)
                userImageRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(requireContext())
                        .load(uri)
                        .into(fragmentChatRoomBinding.imageViewUserProfile)
                }.addOnFailureListener {
                    Log.d(TAG, "load user image failed")
                }

                // 반려견 정보 표시
                chatRoomViewModel.getReceiverPetInfoByUserId(receiverId)

                // 상호 차단 여부에 따라 ui 변경
                updateUIBasedOnBlockStatus()
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
                // RecyclerView 데이터 세팅
                messageAdapter.setMessages(messages)
                fragmentChatRoomBinding.recyclerViewMessage.scrollToPosition(messages.size - 1)
            }
        }

        // 사용자 정보 데이터 실시간 감시
        chatRoomViewModel.startObservingCurrentUserInfo(currentUserId)
        chatRoomViewModel.startObservingReceiverUserInfo(receiverId)

        // 두 사용자 정보로 채팅방 찾아서 정보 로드
        chatRoomViewModel.getChatRoom(currentUserId, receiverId)

        // TODO 날짜가 바뀌면 자동으로 DATE 타입 메시지 저장

        fragmentChatRoomBinding.run {

            toolbarChatRoom.run {
                setNavigationOnClickListener {
                    findNavController().popBackStack()
                }

                // toolbar 메뉴 이벤트 리스너
                setOnMenuItemClickListener { menuItem ->
                    when(menuItem.itemId) {
                        R.id.item_chat_toggle_block -> {
                            toggleBlockStatus()
                            true
                        }
                        R.id.item_chat_report -> {
                            // 신고하기 화면 이동 (채팅 상대 UID 전달)
                            val action = ChatRoomFragmentDirections.actionChatRoomFragmentToReportUserFragment(receiverId, chatRoomViewModel.receiverUserInfoData.value?.nickname!!)
                            findNavController().navigate(action)
                            true
                        }
                        R.id.item_chat_exit -> {
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

            messageAdapter = MessageAdapter(chatRoomViewModel)

            // 채팅방 메시지 목록
            recyclerViewMessage.run {
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(TopMarginItemDecoration(8))
                adapter = messageAdapter

                addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                    // 소프트 키보드가 올라올 때 RecyclerView 마지막으로 스크롤
                    if(bottom < oldBottom) {
                        postDelayed({
                            scrollToPosition(messageAdapter.itemCount - 1)
                        }, 100)
                    }
                }
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
        return "${ageYear}살"
    }
    
    // 메시지 전송
    private fun sendTextMessage(){
        val content = fragmentChatRoomBinding.editTextMessage.text.toString()
        fragmentChatRoomBinding.editTextMessage.text.clear()
        val message = Message(
            "",
            currentUserId,
            content,
            Timestamp.now(),
            false,
            MessageType.TEXT.code,
            MessageVisibility.ALL.code
        )
        chatRoomViewModel.sendMessage(chatRoomId, message)
    }
    
    // 차단 상태 반전
    private fun toggleBlockStatus() {
        // 차단 여부 체크
        val userBlockList = currentUserInfo?.blockUserList.orEmpty()
        val isBlocked = userBlockList.contains(receiverId)
        if (isBlocked) {
            // 내가 이미 차단한 경우
            showUnblockDialog()
        } else {
            // 내가 아직 차단하지 않은 경우
            showBlockDialog()
        }
    }
    
    // 차단
    private fun showBlockDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("차단하기")
            .setMessage("차단한 사용자와는 채팅을 할 수 없으며 산책 메이트를 요청할 수 없습니다.")
            .setPositiveButton("차단하기"){ dialogInterface: DialogInterface, i: Int ->
                chatRoomViewModel.blockUser(currentUserId, receiverId)
            }
            .setNegativeButton("취소", null)
            .create()
        builder.show()
    }

    // 차단 해제
    private fun showUnblockDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("차단해제")
            .setMessage("차단을 해제하면 다시 채팅을 주고받을 수 있으며 산책 메이트 요청이 가능해집니다.")
            .setPositiveButton("해제하기"){ dialogInterface: DialogInterface, i: Int ->
                chatRoomViewModel.unblockUser(currentUserId, receiverId)
            }
            .setNegativeButton("취소", null)
            .create()
        builder.show()
    }

    // 차단 여부에 따라 UI 변경
    private fun updateUIBasedOnBlockStatus() {
        var isBlockedByMe = false
        var isBlockedByReceiver = false

        // 내가 상대를 차단했는지 여부
        if (currentUserInfo != null) {
            val myBlockList = currentUserInfo!!.blockUserList.orEmpty()
            isBlockedByMe = myBlockList.contains(receiverId)
        }

        // 상대가 나를 차단했는지 여부
        if (receiverUserInfo != null) {
            val receiverBlockList = receiverUserInfo!!.blockUserList.orEmpty()
            isBlockedByReceiver = receiverBlockList.contains(currentUserId)
        }

        if (isBlockedByMe) {
            // 차단 상태 : 산책 메이트 요청 및 채팅 불가, 차단해제 메뉴 표시
            fragmentChatRoomBinding.run {
                buttonRequestWalkMate.isEnabled = false
                buttonSendMessage.isEnabled = false
                editTextMessage.isEnabled = false
                toolbarChatRoom.menu.run {
                    findItem(R.id.item_chat_toggle_block).title = "차단해제"
                }
                editTextMessage.hint = "차단한 사용자와는 채팅할 수 없습니다."
            }
        } else if (isBlockedByReceiver) {
            fragmentChatRoomBinding.run {
                buttonRequestWalkMate.isEnabled = false
                buttonSendMessage.isEnabled = false
                editTextMessage.isEnabled = false
                toolbarChatRoom.menu.run {
                    findItem(R.id.item_chat_toggle_block).title = "차단하기"
                }
                editTextMessage.hint = "상대방으로 부터 차단되어 채팅할 수 없습니다."
            }
        } else {
            // 차단해제 상태 : 산책 메이트 요청 및 채팅 가능, 차단하기 메뉴 표시
            fragmentChatRoomBinding.run {
                buttonRequestWalkMate.isEnabled = true
                buttonSendMessage.isEnabled = true
                editTextMessage.hint = "메시지를 입력하세요."
                editTextMessage.isEnabled = true
                toolbarChatRoom.menu.run {
                    findItem(R.id.item_chat_toggle_block).title = "차단하기"
                }
            }
        }
    }

    // 채팅방 나가기
    private fun exitChatRoom() {
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("채팅방 나가기")
            .setMessage("채팅방을 나가면 대화내용이 모두 삭제되고 채팅목록에서도 삭제됩니다.")
            .setPositiveButton("나가기"){ dialogInterface: DialogInterface, i: Int ->
                // TODO 채팅방 나가기 로직 구현
                findNavController().popBackStack()
            }
            .setNegativeButton("취소", null)
            .create()
        builder.show()
    }
}