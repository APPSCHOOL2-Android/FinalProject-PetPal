package com.petpal.mungmate.ui.community


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.petpal.mungmate.databinding.RowCommunityRecentSearchesBinding


class CommunityRecentSearchesAdapter(
    private val onItemDeleteListener: (SearchesEntity) -> Unit,
    private val onItemSearchListener: (String) -> Unit
) :
    ListAdapter<SearchesEntity, CommunityRecentSearchesAdapter.CommunityRecentSearchesViewHolder>(
        DiffCallback()
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommunityRecentSearchesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowCommunityRecentSearchesBinding.inflate(inflater, parent, false)
        return CommunityRecentSearchesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommunityRecentSearchesViewHolder, position: Int) {
        val searchHistory = getItem(position)
        holder.bind(searchHistory)
    }

    inner class CommunityRecentSearchesViewHolder(private val binding: RowCommunityRecentSearchesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(searchHistory: SearchesEntity) {
            binding.communityRecentSearchesTextView.text = searchHistory.searchesContent

            binding.communityRecentSearchesDeleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val deletedItem = getItem(position)
                    // 아이템 삭제 함수 호출
                    onItemDeleteListener(deletedItem)
                }
            }
            binding.communityRecentSearchesTextView.setOnClickListener {
                val query = searchHistory.searchesContent
                onItemSearchListener(query)
            }
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