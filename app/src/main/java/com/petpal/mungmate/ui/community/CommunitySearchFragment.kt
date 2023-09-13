package com.petpal.mungmate.ui.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentCommunitySearchBinding


class CommunitySearchFragment : Fragment() {

    lateinit var communitySearchBinding: FragmentCommunitySearchBinding
    private lateinit var communitySearchViewModel: CommunitySearchViewModel
    private lateinit var adapter: CommunityRecentSearchesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        communitySearchBinding = FragmentCommunitySearchBinding.inflate(inflater)

        communitySearchViewModel = ViewModelProvider(this)[CommunitySearchViewModel::class.java]

        communitySearchBinding.run {

            exbutton1.setOnClickListener {
                findNavController().navigate(R.id.action_communitySearchFragment_to_productRegistrationFragment)
            }

            communitySearchToolbar.run {
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    it.findNavController()
                        .popBackStack()
                }
            }

            val layoutManager = LinearLayoutManager(requireContext())

            // 아이템 역순 코드
            layoutManager.reverseLayout = true
            layoutManager.stackFromEnd = true

            communityRecentSearchesRecyclerView.layoutManager = layoutManager

            adapter = CommunityRecentSearchesAdapter { deletedItem ->
                // 뷰모델을 통해 아이템 삭제
                communitySearchViewModel.delete(deletedItem)
            }
                communityRecentSearchesRecyclerView.adapter = adapter

            communitySearchViewModel.allSearchHistory.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }

            communityRecentSearchesAllClearButton.setOnClickListener {
                communitySearchViewModel.deleteAllData()
            }

            searchView.editText.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = searchView.text.toString().trim()

                    // 검색어가 비어 있지 않으면..
                    if (query.isNotEmpty()) {

                        val searchesEntity = SearchesEntity(0, query)
                        // 검색어를 데이터 데이스에 삽입
                        communitySearchViewModel.insert(searchesEntity)
                    }
                    searchView.text?.clear()
                    searchView.hide()
                    true
                } else {
                    false
                }
            }
        }

        return communitySearchBinding.root
    }
}

