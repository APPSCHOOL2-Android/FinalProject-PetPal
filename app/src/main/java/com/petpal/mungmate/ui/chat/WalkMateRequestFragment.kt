package com.petpal.mungmate.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.Timestamp
import com.petpal.mungmate.databinding.FragmentWalkMateRequestBinding
import com.petpal.mungmate.model.Message
import com.petpal.mungmate.model.MessageType
import com.petpal.mungmate.model.Match
import com.petpal.mungmate.model.MatchStatus
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WalkMateRequestFragment : Fragment() {
    private var _fragmentWalkMateRequestBinding : FragmentWalkMateRequestBinding? = null
    private val fragmentWalkMateRequestBinding get() = _fragmentWalkMateRequestBinding!!

    // private lateinit var walkMateRequestViewModel: WalkMateRequestViewModel
    private lateinit var chatViewModel: ChatViewModel

    lateinit var chatRoomId: String
    lateinit var senderId: String
    lateinit var receiverId: String

    private var selectedDate: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // walkMateRequestViewModel = ViewModelProvider(this)[WalkMateRequestViewModel::class.java]
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentWalkMateRequestBinding = FragmentWalkMateRequestBinding.inflate(inflater)

        // 채팅창에서 전달받은 산책 메이트 요청 송신자 -> 수신자 id
        val args = WalkMateRequestFragmentArgs.fromBundle(requireArguments())
        chatRoomId = args.chatRoomId
        senderId = args.senderId
        receiverId = args.receiverId

        return fragmentWalkMateRequestBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentWalkMateRequestBinding.run {
            toolbarWalkMateRequest.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            buttonRequest.setOnClickListener {
                // 유효성 검사, 에러 문구 표시하기
                if (isDateValid() && isTimeValid() && isPlaceValid()) {
                    // 1. 산책 매칭 데이터 저장
                    saveMatch()
                }
            }

            // 텍스트 변경 후 유효성 검사, Dare Time Picker 표시
            textInputEditTextDate.run {
                setOnClickListener {
                    showDatePicker()
                }
                doAfterTextChanged {
                    isDateValid()
                }
            }

            textInputEditTextTime.run {
                setOnClickListener {
                    showTimePicker()
                }
                doAfterTextChanged {
                    isTimeValid()
                }
            }

            textInputEditTextPlace.doAfterTextChanged {
                isPlaceValid()
            }
        }
    }

    private fun isDateValid(): Boolean {
        fragmentWalkMateRequestBinding.run {
            // 공란 체크
            if (!textInputEditTextDate.text.isNullOrBlank()) {
                // 오늘 보다 이전 날짜로 약속 잡기 불가
                val currentDate = Calendar.getInstance().timeInMillis
                if (selectedDate < currentDate) {
                    textInputLayoutDate.error = "오늘에서 이미 지난 날짜는 선택할 수 없습니다."
                    return false
                } else {
                    textInputLayoutDate.error = null
                    return true
                }
            } else {
                textInputLayoutDate.error = "약속 날짜를 선택해주세요."
                return false
            }
        }
    }

    private fun isTimeValid(): Boolean {
        fragmentWalkMateRequestBinding.run {
            val inputDate = textInputEditTextDate.text.toString()
            val inputTime = textInputEditTextTime.text.toString()
            // 시간 공란 체크
            if (inputTime.isBlank()) {
                textInputLayoutTime.error = "약속 시간을 선택해주세요."
                return false
            } else {
                // 날짜를 아직 선택 안했을 경우
                if (inputDate.isBlank()) {
                    textInputLayoutTime.error = null
                    return true
                } else {
                    // 날짜를 이미 선택했을 경우 -> 오늘 날짜에서 이미 지난 시간 선택 불가
                    val inputDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse("$inputDate $inputTime")
                    val currentDateTime = Date()
                    if (inputDateTime.before(currentDateTime)) {
                        textInputLayoutTime.error = "이미 지난 시간은 선택할 수 없습니다."
                        return false
                    } else {
                        textInputLayoutTime.error = null
                        return true
                    }
                }
            }
        }
    }

    private fun isPlaceValid(): Boolean {
        fragmentWalkMateRequestBinding.run {
            if (textInputEditTextPlace.text.isNullOrBlank()) {
                textInputLayoutPlace.error = "약속 장소를 입력해주세요"
                return false
            } else {
                textInputLayoutPlace.error = null
                return true
            }
        }
    }

    private fun showDatePicker() {
        // DatePicker 기본값 오늘로 설정
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("날짜 선택")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selectedDateInMillis ->
            selectedDate = selectedDateInMillis
            val selectDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Date(selectedDateInMillis))
            fragmentWalkMateRequestBinding.textInputEditTextDate.setText(selectDate)
        }
        datePicker.show(parentFragmentManager, "tag")
    }

    private fun showTimePicker() {
        val timePicker = MaterialTimePicker.Builder()
            .setTitleText("시간 선택")
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .build()
        timePicker.addOnPositiveButtonClickListener {
            val selectedTime = String.format("%02d:%02d", timePicker.hour, timePicker.minute)
            fragmentWalkMateRequestBinding.textInputEditTextTime.setText(selectedTime)
        }
        timePicker.show(parentFragmentManager, "tag")
    }

    // 1. 산책 매칭 데이터 저장
    private fun saveMatch() {
        val walkDate = fragmentWalkMateRequestBinding.textInputEditTextDate.text.toString()
        val walkTime = fragmentWalkMateRequestBinding.textInputEditTextTime.text.toString()
        val walkPlace = fragmentWalkMateRequestBinding.textInputEditTextPlace.text.toString()

        val walkTimestamp = parseStringToTimeStamp("$walkDate $walkTime")

        val match = Match(
            senderId,
            receiverId,
            walkTimestamp,
            walkPlace,
            Timestamp.now(),
            MatchStatus.REQUESTED.code,
            null,
            null,
            null
        )

        chatViewModel.saveMatch(match).addOnSuccessListener { matchDocumentKey ->
            // 매칭 데이터 저장된 후에 산책 매칭 메시지 저장(전송)
            sendMatchMessage(matchDocumentKey)
        }
    }

    // 날짜 시간 문자열을 timestamp 타입으로 형변환
    private fun parseStringToTimeStamp(dateTimeString: String): Timestamp? {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = dateFormat.parse(dateTimeString)

            if (date != null) {
                return Timestamp(date)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // 파싱 실패 시 null 반환
        return null
    }

    // 2. 산책 매칭 메시지 저장
    private fun sendMatchMessage(matchDocumentKey: String) {
        // content에 walkmatching id 저장 -> RecyclerView ViewHolder에서 데이터 가져와서 사용
        val message = Message(
            senderId,
            matchDocumentKey,
            Timestamp.now(),
            null,
            MessageType.WALK_MATE_REQUEST.code,
            null
        )
        chatViewModel.saveMessage(chatRoomId, message)
        Snackbar.make(requireView(), "산책 메이트 요청 메시지를 전송했습니다.", Snackbar.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }
}