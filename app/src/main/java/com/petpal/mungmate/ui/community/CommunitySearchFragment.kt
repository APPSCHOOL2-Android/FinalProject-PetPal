package com.petpal.mungmate.ui.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
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
        val db = SearchesDatabase.getDataBase(requireContext())

        communitySearchBinding.run {
            communitySearchToolbar.run {
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    it.findNavController()
                        .navigate(R.id.action_communitySearchFragment_to_item_community)
                }
            }

            val layoutManager = LinearLayoutManager(requireContext())
            communityRecentSearchesRecyclerView.layoutManager = layoutManager

            adapter = CommunityRecentSearchesAdapter()
            communityRecentSearchesRecyclerView.adapter = adapter

            communitySearchViewModel.allSearchHistory.observe(viewLifecycleOwner) { searchHistoryList ->
                adapter.submitList(searchHistoryList)
            }
            communityRecentSearchesAllClearButton.setOnClickListener {
                communitySearchViewModel.deleteAllData()
            }

            searchView.editText.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = searchView.text.toString().trim()
                    if (query.isNotEmpty()) {
                        val searchesEntity = SearchesEntity(0,query)
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

