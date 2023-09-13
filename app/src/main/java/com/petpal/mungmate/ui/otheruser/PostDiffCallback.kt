package com.petpal.mungmate.ui.otheruser

import androidx.recyclerview.widget.DiffUtil
import com.petpal.mungmate.model.Post

class PostDiffCallback: DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.postID == newItem.postID
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}