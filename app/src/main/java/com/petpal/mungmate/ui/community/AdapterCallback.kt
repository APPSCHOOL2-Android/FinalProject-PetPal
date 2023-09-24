package com.petpal.mungmate.ui.community

import androidx.recyclerview.widget.RecyclerView
import com.petpal.mungmate.model.Comment

interface AdapterCallback {
    fun onReplyButtonClicked(commentList: Comment, position: Int)
}