package com.petpal.mungmate.ui.community

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowCommunityCommentBinding
import com.petpal.mungmate.model.Comment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class ReplyAdapter(private val replyList: MutableList<Comment>, private val context: Context) :
    RecyclerView.Adapter<ReplyAdapter.ViewHolder>() {

    inner class ViewHolder(item: RowCommunityCommentBinding) :
        RecyclerView.ViewHolder(item.root) {

        val replyProfileImage: ImageView = item.communityCommentProfileImage
        val replyNickName: TextView = item.communityCommentUserNickName
        val replyContent: TextView = item.communityCommentContent
        val replyDateCreated: TextView = item.communityCommentPostDateCreated
        val replyFavoriteConstraintLayout: ConstraintLayout = item.communityFavoriteConstraintLayout
        val replyCommunityCommentReplyTextView: TextView = item.communityCommentReplyTextView
        val replyCommunityCommentCommentCounter: TextView = item.communityCommentCommentCounter

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowCommunityCommentBinding = RowCommunityCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(rowCommunityCommentBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.replyFavoriteConstraintLayout.visibility = View.GONE
        holder.replyCommunityCommentReplyTextView.visibility = View.GONE
        holder.replyCommunityCommentCommentCounter.visibility = View.GONE

        val reply = replyList[position]
        val db = FirebaseFirestore.getInstance()
        val user = reply.commentUid
        if (user != null) {
            val userId = reply.commentUid

            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {

                        val userImage = documentSnapshot.getString("userImage")
                        val nickname = documentSnapshot.getString("nickname")
                        if (userImage != null) {

                            val storage = FirebaseStorage.getInstance()
                            val storageRef = storage.reference.child(userImage)
                            holder.replyNickName.text = nickname
                            storageRef.downloadUrl.addOnSuccessListener { uri ->
                                Glide.with(context)
                                    .load(uri)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .fitCenter()
                                    .fallback(R.drawable.main_image)
                                    .error(R.drawable.main_image)
                                    .into(holder.replyProfileImage)
                            }.addOnFailureListener { exception ->

                            }
                        } else {

                        }
                    } else {

                    }
                }
                .addOnFailureListener { e ->

                }
        }
        holder.replyContent.text = reply.commentContent

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        val snapshotTime =
            dateFormat.parse(reply.commentDateCreated)

        val currentTime = Date()
        val timeDifferenceMillis =
            currentTime.time - snapshotTime.time
        val timeAgo = when {
            timeDifferenceMillis < 60_000 -> "방금 전" // 1분 미만
            timeDifferenceMillis < 3_600_000 -> "${timeDifferenceMillis / 60_000}분 전" // 1시간 미만
            timeDifferenceMillis < 86_400_000 -> "${timeDifferenceMillis / 3_600_000}시간 전" // 1일 미만
            else -> "${timeDifferenceMillis / 86_400_000}일 전" // 1일 이상 전
        }

        holder.replyDateCreated.text = timeAgo
    }

    override fun getItemCount() = replyList.size
    fun updateData(newData: MutableList<Comment>) {
        replyList.clear()
        replyList.addAll(newData)
        notifyDataSetChanged()
    }
}