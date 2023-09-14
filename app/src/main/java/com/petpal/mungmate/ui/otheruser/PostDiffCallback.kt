package com.petpal.mungmate.ui.otheruser

import androidx.recyclerview.widget.DiffUtil

class PostDiffCallback : DiffUtil.ItemCallback<PostUiState>() {
    override fun areItemsTheSame(oldItem: PostUiState, newItem: PostUiState): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: PostUiState, newItem: PostUiState): Boolean {
        return oldItem == newItem
    }
}