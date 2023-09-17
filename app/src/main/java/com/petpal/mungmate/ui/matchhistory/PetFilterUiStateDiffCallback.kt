package com.petpal.mungmate.ui.matchhistory

import androidx.recyclerview.widget.DiffUtil

class PetFilterUiStateDiffCallback : DiffUtil.ItemCallback<PetFilterUiState>() {
    override fun areItemsTheSame(oldItem: PetFilterUiState, newItem: PetFilterUiState): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: PetFilterUiState, newItem: PetFilterUiState): Boolean {
        return oldItem == newItem
    }
}
