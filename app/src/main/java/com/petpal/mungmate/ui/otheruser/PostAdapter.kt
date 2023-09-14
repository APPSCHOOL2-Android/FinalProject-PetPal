package com.petpal.mungmate.ui.otheruser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.petpal.mungmate.databinding.RowSimplePostBinding

class PostAdapter : ListAdapter<PostUiState, PostAdapter.PostViewHolder>(PostDiffCallback()) {
    inner class PostViewHolder(private val rowSimplePostBinding: RowSimplePostBinding) :
        ViewHolder(rowSimplePostBinding.root) {
        fun bind(post: PostUiState) {
            rowSimplePostBinding.run {
                textViewRowPostTitle.text = post.title
                textViewRowCategory.text = post.category
                textViewRowTimeStamp.text = post.dateCreated
                textViewRowPostCommentCnt.text = post.commentCnt.toString()

                root.setOnClickListener {
                    //TODO: 게시글로 이동
                }
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
        holder.bind(getItem(position) as PostUiState)
    }
}