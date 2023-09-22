package com.petpal.mungmate.ui.community

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowCommunityBinding
import com.petpal.mungmate.model.Post

class CommunitySearchAdapter(
    private val context: Context,
    private val mainActivity: MainActivity,
    private val postList: MutableList<Post>

) :
    RecyclerView.Adapter<CommunitySearchAdapter.ViewHolder>() {

    inner class ViewHolder(item: RowCommunityBinding) :
        RecyclerView.ViewHolder(item.root) {

        val communityProfileImage: ImageView = item.communityProfileImage
        val communityPostTitle: TextView = item.communityPostTitle
        val communityUserNickName: TextView = item.communityUserNickName
        val communityPostDateCreated: TextView = item.communityPostDateCreated
        val communityPostImage: ImageView = item.communityPostImage
        val communityContent: TextView = item.communityContent
        val communityCommentCounter: TextView = item.communityCommentCounter
        val communityFavoriteLottie: LottieAnimationView = item.communityFavoriteLottie
        val communityFavoriteCounter: TextView = item.communityFavoriteCounter
        val communityPostCardView: CardView = item.communityPostCardView
        val communityPostCategoryCard: CardView = item.communityPostCategoryCard
        val communityPostCategoryTextView: TextView = item.communityPostCategoryTextView

        init {
            item.root.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("position", postList[adapterPosition].postID)
                }

                Log.d("확인2", postList[adapterPosition].postID.toString())
                mainActivity.navigate(
                    R.id.action_communitySearchFragment_to_communityPostDetailFragment,
                    bundle,

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

        if (post.postImages?.isNotEmpty()!!) {
            Glide
                .with(context)
                .load(post.postImages?.get(0)?.image.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .fallback(R.drawable.main_image)
                .into(holder.communityPostImage)
            holder.communityPostCardView.visibility = View.VISIBLE
        }else{
            holder.communityPostCardView.visibility = View.GONE
        }


        holder.communityPostImage.setOnClickListener {
            val bundle = Bundle().apply {
                putString("img", post.postImages?.get(0)?.image.toString())
            }

            mainActivity.navigate(
                R.id.action_communitySearchFragment_to_fullScreenFragment,
                bundle
            )
        }

        holder.communityPostTitle.text = post.postTitle
        holder.communityUserNickName.text = post.userNickName
        holder.communityPostDateCreated.text = post.postDateCreated.toString()
        holder.communityContent.text = post.postContent
        holder.communityCommentCounter.text= post.postComment?.size.toString()
        holder.communityFavoriteCounter.text = post.postLike.toString()
        holder.communityPostCategoryTextView.text= post.postCategory.toString()

        Log.d("어떤 카테고리",post.postCategory.toString())
        /*when(post.postCategory.toString()){
            "일상"->{
                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_daily))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "산책일지"->{
                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_walk_log))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "산책 메이트 구해요"->{
                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_walking_maid_matching))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "장소 후기"->{
                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_place_reviews
                ))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "애견용품 후기"->{
                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_dog_product_reviews))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "시츄 아지트"->{
                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_shih_tzu))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "푸들 아지트"->{
                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_poodle))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "포메라니안 아지트"->{
                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_pomeranian))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "치와와 아지트"->{
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "몰티즈 아지트"->{
                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_maltese))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "진돗개 아지트"->{
                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_jindo_dog))
                holder.communityPostCategoryTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "리트리버 아지트"->{
                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_retriever))

                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "웰시 코기 아지트"->{
                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_welsh_corgi))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            else->{
                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_puppies))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
        }*/



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

    fun addAll(posts: List<Post>) {
        val startPosition = postList.size
        postList.addAll(posts)
        notifyItemRangeInserted(startPosition, posts.size)
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