package com.petpal.mungmate.ui.manageblock

import androidx.recyclerview.widget.DiffUtil

class BlockStateDiffCallback  : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
       return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}

