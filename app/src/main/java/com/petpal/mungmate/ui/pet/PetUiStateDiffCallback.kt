package com.petpal.mungmate.ui.pet

import androidx.recyclerview.widget.DiffUtil

class PetUiStateDiffCallback : DiffUtil.ItemCallback<PetUiState>() {
    override fun areItemsTheSame(oldItem: PetUiState, newItem: PetUiState): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: PetUiState, newItem: PetUiState): Boolean {
        return oldItem == newItem
    }
}