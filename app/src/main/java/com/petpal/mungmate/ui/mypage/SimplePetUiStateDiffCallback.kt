package com.petpal.mungmate.ui.mypage

import androidx.recyclerview.widget.DiffUtil

class SimplePetUiStateDiffCallback : DiffUtil.ItemCallback<SimplePetUiState>() {
    override fun areItemsTheSame(oldItem: SimplePetUiState, newItem: SimplePetUiState): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: SimplePetUiState, newItem: SimplePetUiState): Boolean {
        return oldItem == newItem
    }
}