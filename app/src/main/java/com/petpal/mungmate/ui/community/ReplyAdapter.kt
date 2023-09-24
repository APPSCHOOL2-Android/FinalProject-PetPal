package com.petpal.mungmate.ui.community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.petpal.mungmate.R
import com.petpal.mungmate.model.Comment

class ReplyAdapter(private val replyList: MutableList<Comment>) :
    RecyclerView.Adapter<ReplyAdapter.ViewHolder>() {

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val replyContent: TextView = item.findViewById(R.id.communityCommentContent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_community_comment, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reply = replyList[position]

        holder.replyContent.text = reply.commentContent
    }

    override fun getItemCount() = replyList.size


}