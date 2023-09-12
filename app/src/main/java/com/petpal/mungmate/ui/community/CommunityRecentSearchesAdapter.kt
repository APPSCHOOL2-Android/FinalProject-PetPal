package com.petpal.mungmate.ui.community


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.petpal.mungmate.databinding.RowCommunityRecentSearchesBinding


class CommunityRecentSearchesAdapter : ListAdapter<SearchesEntity, CommunityRecentSearchesAdapter.SearchHistoryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowCommunityRecentSearchesBinding.inflate(inflater, parent, false)
        return SearchHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        val searchHistory = getItem(position)
        holder.bind(searchHistory)
    }

    inner class SearchHistoryViewHolder(private val binding: RowCommunityRecentSearchesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(searchHistory: SearchesEntity) {
            binding.communityRecentSearchesTextView.text = searchHistory.searchesContent
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<SearchesEntity>() {
    override fun areItemsTheSame(oldItem: SearchesEntity, newItem: SearchesEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SearchesEntity, newItem: SearchesEntity): Boolean {
        return oldItem == newItem
    }
}