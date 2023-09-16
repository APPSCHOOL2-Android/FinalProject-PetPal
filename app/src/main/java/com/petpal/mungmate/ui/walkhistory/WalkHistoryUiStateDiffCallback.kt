package com.petpal.mungmate.ui.walkhistory

import androidx.recyclerview.widget.DiffUtil

class WalkHistoryUiStateDiffCallback : DiffUtil.ItemCallback<WalkHistoryUiState>() {

    override fun areItemsTheSame(
        oldItem: WalkHistoryUiState,
        newItem: WalkHistoryUiState,
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: WalkHistoryUiState,
        newItem: WalkHistoryUiState,
    ): Boolean {
        return oldItem == newItem
    }

}
