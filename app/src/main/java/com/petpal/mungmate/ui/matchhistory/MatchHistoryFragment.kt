package com.petpal.mungmate.ui.matchhistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.petpal.mungmate.databinding.FragmentMatchHistoryBinding


class MatchHistoryFragment : Fragment() {

    private lateinit var _fragmentMatchHistoryFragment: FragmentMatchHistoryBinding
    private val fragmentMatchHistoryBinding get() = _fragmentMatchHistoryFragment
    private lateinit var petFilterAdapter: PetFilterAdapter
    private lateinit var matchHistoryAdapter: MatchHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentMatchHistoryFragment = FragmentMatchHistoryBinding.inflate(layoutInflater)
        petFilterAdapter = PetFilterAdapter()
        matchHistoryAdapter = MatchHistoryAdapter()

        fragmentMatchHistoryBinding.run {
            recyclerViewMatchHistory.run {
                adapter = matchHistoryAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            //매칭 기록 데이터 넣어주기
            matchHistoryAdapter.submitList(getMatchHistorySampleData())


            recyclerViewPetFilter.run {
                adapter = petFilterAdapter
            }

            //테스트 데이터 넣어주기
            petFilterAdapter.submitList(getPetFilterSampleData())

            toolbarManageHistory.run {
                setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
            }
        }
        return fragmentMatchHistoryBinding.root
    }

    private fun getPetFilterSampleData(): List<PetFilterUiState> {
        return listOf(
            PetFilterUiState("image", "이름"),
            PetFilterUiState("image", "이름"),
            PetFilterUiState("image", "이름"),
            PetFilterUiState("image", "이름")
        )
    }

    private fun getMatchHistorySampleData(): List<MatchHistoryUiState> {
        return listOf(
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
            MatchHistoryUiState("image","닉네임","초롱이","2023.09.12 오후 7:00", "강아지 공원"),
        )
    }

}