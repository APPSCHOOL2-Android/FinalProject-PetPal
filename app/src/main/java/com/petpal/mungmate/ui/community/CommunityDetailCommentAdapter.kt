package com.petpal.mungmate.ui.community

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowCommunityCommentBinding
import com.petpal.mungmate.model.Comment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class CommunityDetailCommentAdapter(
    private val context: Context,
    private val postCommentList: MutableList<Comment>,
    private val postGetId: String
) :
    RecyclerView.Adapter<CommunityDetailCommentAdapter.ViewHolder>() {

    inner class ViewHolder(item: RowCommunityCommentBinding) :
        RecyclerView.ViewHolder(item.root) {
        val communityCommentMenuImageButton: ImageButton = item.communityCommentMenuImageButton
        val communityProfileImage: ImageView = item.communityCommentProfileImage
        val communityUserNickName: TextView = item.communityCommentUserNickName

        val communityPostDateCreated: TextView = item.communityCommentPostDateCreated
        val communityContent: TextView = item.communityCommentContent
        val communityCommentFavoriteCounter: TextView = item.communityCommentFavoriteCounter
        val communityCommentFavoriteLottie: LottieAnimationView =
            item.communityCommentFavoriteLottie
        val communityCommentCommentCounter: TextView = item.communityCommentCommentCounter

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

    override fun getItemCount() = postCommentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val commentList = postCommentList[position]
        Glide
            .with(context)
            .load("https://mblogthumb-phinf.pstatic.net/MjAxOTEyMTNfMTQx/MDAxNTc2MjAyNzE5NDE0.B-NhNQS5QdweUBY53sWNGA8cJQUupeQeza7ognzYmGUg.1zj3ZPxEc2QrCJ2y5O--fmvMl2yljMb3uZQn6C1xsdUg.JPEG.369ginseng/1576202724454.jpg?type=w800")
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .into(holder.communityProfileImage)


        holder.communityUserNickName.text = commentList.commentNickName.toString()


        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")

        val snapshotTime = dateFormat.parse(commentList.commentDateCreated)

        val currentTime = Date()
        val timeDifferenceMillis = currentTime.time - snapshotTime.time  // Firestore 시간에서 현재 시간을 뺌

        val timeAgo = when {
            timeDifferenceMillis < 60_000 -> "방금 전" // 1분 미만
            timeDifferenceMillis < 3_600_000 -> "${timeDifferenceMillis / 60_000}분 전" // 1시간 미만
            timeDifferenceMillis < 86_400_000 -> "${timeDifferenceMillis / 3_600_000}시간 전" // 1일 미만
            else -> "${timeDifferenceMillis / 86_400_000}일 전" // 1일 이상 전
        }

        holder.communityPostDateCreated.text = timeAgo
        holder.communityContent.text = commentList.commentContent
        holder.communityCommentFavoriteCounter.text = "0"
        holder.communityCommentCommentCounter.text = "0"

        holder.communityCommentMenuImageButton.setOnClickListener { view ->
            val popupMenu = PopupMenu(context, view)
            popupMenu.inflate(R.menu.community_post_detail_comment)

            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.item_comment_delete -> {
                        Snackbar.make(view, "댓글 삭제하기", Snackbar.LENGTH_SHORT).show()
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }

        var isClicked = false

        holder.communityCommentFavoriteLottie.scaleX = 2.0f
        holder.communityCommentFavoriteLottie.scaleY = 2.0f

        holder.communityCommentFavoriteLottie.setOnClickListener {
            isClicked = !isClicked // 클릭할 때마다 변수를 반전시킴
            if (isClicked) {
                holder.communityCommentFavoriteLottie.playAnimation()

            } else {
                holder.communityCommentFavoriteLottie.cancelAnimation()
                holder.communityCommentFavoriteLottie.progress = 0f
            }

        }

    }

    fun updateData(newData: MutableList<Comment>) {
        Log.d("값 추적", newData.toString())
        postCommentList.clear()
        postCommentList.addAll(newData)
        notifyDataSetChanged()
    }
}