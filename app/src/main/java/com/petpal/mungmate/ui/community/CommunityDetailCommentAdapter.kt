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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowCommunityCommentBinding
import com.petpal.mungmate.model.Comment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class CommunityDetailCommentAdapter(
    private val context: Context,
    private val postCommentList: MutableList<Comment>,
    private val postGetId: String,
    private val commentViewModel: CommentViewModel
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
            .load(commentList.commentUserImage)
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

            holder.communityCommentMenuImageButton.setOnClickListener { view ->
                val popupMenu = PopupMenu(context, view)
                popupMenu.inflate(R.menu.community_post_detail_comment)
                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser
                val currentUserId = currentUser?.uid

                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.item_comment_delete -> {
                            val commentToDelete = postCommentList[holder.adapterPosition]

                            Log.d("이건 뭐죠?",commentToDelete.commentUid.toString())
                            Log.d("이건 뭐죠?",currentUserId.toString())
                            if (commentToDelete.commentUid.toString() == currentUserId.toString()) {
                                val db = FirebaseFirestore.getInstance()
                                db.collection("Post").document(postGetId).update(
                                    "postComment",
                                    FieldValue.arrayRemove(commentToDelete)
                                ).addOnSuccessListener {
                                    commentViewModel.deleteComment(commentToDelete)
                                    postCommentList.remove(commentToDelete)
                                    notifyDataSetChanged()
                                    Snackbar.make(view, "댓글이 삭제되었습니다.", Snackbar.LENGTH_SHORT)
                                        .show()
                                }.addOnFailureListener { e ->

                                    Log.e("Firestore", "댓글 삭제 실패: $e")
                                    Snackbar.make(view, "댓글 삭제 실패: $e", Snackbar.LENGTH_SHORT)
                                        .show()
                                }
                            }else{
                                Snackbar.make(view, "댓글 작성자만 삭제할 수 있습니다.", Snackbar.LENGTH_SHORT).show()
                            }

                            true
                        }

                        else -> false
                    }
                }
                popupMenu.show()
            }
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