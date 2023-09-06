package com.petpal.mungmate.community

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowCommunityCommentBinding

class CommunityDetailCommentAdapter(private val context: Context) :
    RecyclerView.Adapter<CommunityDetailCommentAdapter.ViewHolder>() {

    inner class ViewHolder(item: RowCommunityCommentBinding) :
        RecyclerView.ViewHolder(item.root) {
        val communityCommentMenuImageButton: ImageButton = item.communityCommentMenuImageButton
        val communityProfileImage: ImageView = item.communityCommentProfileImage
        val communityUserNickName: TextView = item.communityCommentUserNickName
        val communityUserPlace: TextView = item.communityCommentUserPlace
        val communityPostDateCreated: TextView = item.communityCommentPostDateCreated
        val communityContent: TextView = item.communityCommentContent
        val communityFavoriteTextView: TextView = item.communityCommentFavoriteTextView
        val communityCommentTextView: TextView = item.communityCommentCommentTextView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowCommunityCommentBinding = RowCommunityCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val viewHolderClass = ViewHolder(rowCommunityCommentBinding)
        return viewHolderClass
    }

    override fun getItemCount() = 10

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide
            .with(context)
            .load("https://mblogthumb-phinf.pstatic.net/MjAxOTEyMTNfMTQx/MDAxNTc2MjAyNzE5NDE0.B-NhNQS5QdweUBY53sWNGA8cJQUupeQeza7ognzYmGUg.1zj3ZPxEc2QrCJ2y5O--fmvMl2yljMb3uZQn6C1xsdUg.JPEG.369ginseng/1576202724454.jpg?type=w800")
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .centerCrop()
            .into(holder.communityProfileImage)

        holder.communityUserNickName.text = "리트리버군"
        holder.communityUserPlace.text = "제주시 애월읍"
        holder.communityPostDateCreated.text = "30분전"
        holder.communityContent.text =
            "귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다."
        holder.communityFavoriteTextView.text = "좋아요 7"
        holder.communityCommentTextView.text = "댓글 2"

        holder.communityCommentMenuImageButton.setOnClickListener { view ->
            val popupMenu = PopupMenu(context, view)
            popupMenu.inflate(R.menu.community_post_detail_comment)

            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.item_comment_report -> {
                        Snackbar.make(view, "댓글 신고하기", Snackbar.LENGTH_SHORT).show()
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }


    }

}