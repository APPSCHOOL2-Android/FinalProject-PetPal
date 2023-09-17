package com.petpal.mungmate.ui.matchhistory

import androidx.recyclerview.widget.DiffUtil

class MatchHistoryUiStateDiffCallback : DiffUtil.ItemCallback<MatchHistoryUiState>() {
    override fun areItemsTheSame(
        oldItem: MatchHistoryUiState,
        newItem: MatchHistoryUiState,
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: MatchHistoryUiState,
        newItem: MatchHistoryUiState,
    ): Boolean {
        return oldItem == newItem
    }
}