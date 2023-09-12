package com.petpal.mungmate.ui.otheruser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.petpal.mungmate.databinding.RowSimplePostBinding
import com.petpal.mungmate.model.Post

class PostAdapter : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {
    inner class PostViewHolder(rowSimplePostBinding: RowSimplePostBinding) :
        ViewHolder(rowSimplePostBinding.root) {
        val textViewPostTitle = rowSimplePostBinding.textViewRowPostTitle
        val textViewPostCategory = rowSimplePostBinding.textViewRowCategory
        val textViewPostTimeStamp = rowSimplePostBinding.textViewRowTimeStamp
        val textViewPostCommentCnt = rowSimplePostBinding.textViewRowPostCommentCnt

        init {
            rowSimplePostBinding.root.setOnClickListener {
                //TODO: 게시글로 이동
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val rowBinding = RowSimplePostBinding.inflate(LayoutInflater.from(parent.context))

        rowBinding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return PostViewHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.textViewPostTitle.text = position.toString()
    }
}