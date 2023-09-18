package com.petpal.mungmate.ui.community

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.MainFragmentDirections
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowCommunityBinding
import com.petpal.mungmate.model.Post

class CommunityAdapter(
    private val context: Context,
    private val mainActivity: MainActivity,
    private val postList: MutableList<Post>

) :
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
        val communityCommentCounter: TextView = item.communityCommentCounter
        val communityFavoriteLottie: LottieAnimationView = item.communityFavoriteLottie
        val communityFavoriteCounter: TextView = item.communityFavoriteCounter

        init {
            item.root.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("position", postList[adapterPosition].postID)
                }

                Log.d("확인2", postList[adapterPosition].postID.toString())
                mainActivity.navigate(
                    R.id.action_mainFragment_to_communityPostDetailFragment,
                    bundle
                )

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

    override fun getItemCount() = postList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postList[position]

        Glide
            .with(context)
            .load(post.userImage)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .into(holder.communityProfileImage)

        Glide
            .with(context)
            .load(post.postImages)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .into(holder.communityPostImage)

        holder.communityPostTitle.text = post.postTitle
        holder.communityUserNickName.text = post.userNickName
        holder.communityUserPlace.text = post.userPlace
        holder.communityPostDateCreated.text = post.postDateCreated.toString()
        holder.communityContent.text = post.postContent
        holder.communityCommentCounter.text= post.postComment
        holder.communityFavoriteCounter.text = post.postLike.toString()

        var isClicked = false

        holder.communityFavoriteLottie.scaleX = 2.0f
        holder.communityFavoriteLottie.scaleY = 2.0f

        holder.communityFavoriteLottie.setOnClickListener {

            isClicked = !isClicked // 클릭할 때마다 변수를 반전시킴
            if (isClicked) {
                holder.communityFavoriteLottie.playAnimation()

            } else {
                holder.communityFavoriteLottie.cancelAnimation()
                holder.communityFavoriteLottie.progress = 0f
            }

        }
    }

    fun add(post: Post) {
        if (!postList.contains(post)) {
            postList.add(post)
            notifyItemChanged(postList.size - 1)
        } else {
            update(post)
        }
    }

    fun update(post: Post) {
        val index = postList.indexOf(post)
        if (index != -1) {
            postList[index] = post
            notifyItemChanged(index)
        }
    }

    fun delete(post: Post) {
        val index = postList.indexOf(post)
        if (index != -1) {
            postList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun clear() {
        postList.clear()
    }


}