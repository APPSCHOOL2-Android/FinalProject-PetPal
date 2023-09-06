package com.petpal.mungmate.community

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.petpal.mungmate.databinding.RowCommunityBinding
class CommunityAdapter(private val context: Context) :
    RecyclerView.Adapter<CommunityAdapter.ViewHolder>() {

    inner class ViewHolder(item: RowCommunityBinding) :
        RecyclerView.ViewHolder(item.root) {

        val communityProfileImage: ImageView = item.communityProfileImage
        val communityPostTitle: TextView = item.communityPostTitle
        val communityUserNickName: TextView = item.communityUserNickName
        val communityUserPlace: TextView = item.communityUserPlace
        val communityPostDateCreated: TextView = item.communityPostDateCreated
        val communityPostImage: ImageView = item.communityPostImage
        val communityContent: TextView = item.communityContent
        val communityFavoriteTextView:TextView = item.communityFavoriteTextView
        val communityCommentTextView:TextView=item.communityCommentTextView

        init {
            item.root.setOnClickListener {

                val action =CommunityFragmentDirections.actionItemCommunityToCommunityPostDetailFragment(adapterPosition)
                item.root.findNavController().navigate(action)
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowCommunityBinding = RowCommunityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val viewHolderClass = ViewHolder(rowCommunityBinding)
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

        Glide
            .with(context)
            .load("https://mblogthumb-phinf.pstatic.net/MjAxOTEyMTNfMTQx/MDAxNTc2MjAyNzE5NDE0.B-NhNQS5QdweUBY53sWNGA8cJQUupeQeza7ognzYmGUg.1zj3ZPxEc2QrCJ2y5O--fmvMl2yljMb3uZQn6C1xsdUg.JPEG.369ginseng/1576202724454.jpg?type=w800")
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .centerCrop()
            .into(holder.communityPostImage)

        holder.communityPostTitle.text = "귀여운 강아지 사진"
        holder.communityUserNickName.text = "리트리버군"
        holder.communityUserPlace.text = "제주특별자치도 제주시 애월읍"
        holder.communityPostDateCreated.text = "30분전"
        holder.communityContent.text = "귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다.귀여운 리트리버 사진입니다."
        holder.communityFavoriteTextView.text = "좋아요 7"
        holder.communityCommentTextView.text = "댓글 2"



    }

}