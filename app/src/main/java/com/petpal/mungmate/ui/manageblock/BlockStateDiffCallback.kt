package com.petpal.mungmate.ui.manageblock

import androidx.recyclerview.widget.DiffUtil
import com.petpal.mungmate.model.BlockUser

class BlockStateDiffCallback  : DiffUtil.ItemCallback<BlockUser>() {
    override fun areItemsTheSame(oldItem: BlockUser, newItem: BlockUser): Boolean {
       return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: BlockUser, newItem: BlockUser): Boolean {
        return oldItem == newItem
    }
}

