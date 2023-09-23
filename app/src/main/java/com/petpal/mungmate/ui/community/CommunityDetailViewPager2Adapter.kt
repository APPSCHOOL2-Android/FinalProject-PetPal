package com.petpal.mungmate.ui.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.model.PostImage

class CommunityDetailViewPager2Adapter(private val mainActivity: MainActivity): ListAdapter<PostImage, CommunityDetailViewPager2Adapter.ItemViewHolder>(differ) {

    inner class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(bannerItem: PostImage) {
            val bannerImageView = view.findViewById<ImageView>(R.id.communityViewPager2Image)


            Glide
                .with(bannerImageView.context)
                .load(bannerItem.image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .error(R.drawable.baseline_error_24)
                .fallback(R.drawable.main_image)
                .into(bannerImageView)

            bannerImageView.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString("img", currentList[position].image)
                    }

                    mainActivity.navigate(
                        R.id.action_communityPostDetailFragment_to_fullScreenFragment,
                        bundle
                    )
                }
            /*
                    if (postImagesList != null) {
                    if (postImagesGetList.isNotEmpty()) {
                        Glide
                            .with(requireContext())
                            .load(postImagesGetList[0])
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .fitCenter()
                            .error(R.drawable.baseline_error_24)
                            .fallback(R.drawable.main_image)
                            .into(communityPostDetailPostImage)
                    } else {
                        communityPostDetailPostCardView.visibility = View.GONE
                    }
                } else {
                    Snackbar.make(
                        communityPostDetailPostImage,
                        "이미지를 가져오는데 실패했습니다.",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }
             */

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(R.layout.item_community_view_pager2, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])

    }

    companion object {

        val differ = object : DiffUtil.ItemCallback<PostImage>() {
            override fun areItemsTheSame(oldItem: PostImage, newItem: PostImage): Boolean {
                return oldItem.image == newItem.image
            }

            override fun areContentsTheSame(oldItem: PostImage, newItem: PostImage): Boolean {
                return oldItem == newItem
            }

        }
    }
}