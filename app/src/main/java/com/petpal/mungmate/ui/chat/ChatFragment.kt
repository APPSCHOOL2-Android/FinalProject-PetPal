package com.petpal.mungmate.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentChatBinding
import com.petpal.mungmate.model.ChatRoom

class ChatFragment : Fragment() {

    private var _fragmentChatBinding: FragmentChatBinding? = null
    private val fragmentChatBinding get() = _fragmentChatBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentChatBinding = FragmentChatBinding.inflate(inflater)
        return fragmentChatBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentChatBinding.run {
            recyclerViewChatRoom.run {
                adapter = ChatRoomAdapter(getSampleData(), activity as MainActivity)
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    MaterialDividerItemDecoration(
                        context,
                        MaterialDividerItemDecoration.VERTICAL
                    )
                )
            }
        }
    }

    // UI 표시용 샘플 데이터 셋
    private fun getSampleData(): MutableList<ChatRoom> {
        return mutableListOf(
            ChatRoom(
                "오늘도멍멍", "멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍멍", "1시간 전", 1
            ),
            ChatRoom(
                "마루언니", "몇시에 볼까요?", "1일 전", 2
            ),
            ChatRoom(
                "몽실이네", "안녕하세요 같이 산책할래요? 강아지 공원에서 봅시다", "4일 전", 0
            )
        )
    }
}