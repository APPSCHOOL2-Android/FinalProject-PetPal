package com.petpal.mungmate.ui.community

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.petpal.mungmate.MainActivity
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
                    R.id.action_mainFragment_to_communityPostDetailFragment,
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
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child(post.userImage.toString())
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Glide
                .with(context)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .fallback(R.drawable.main_image)
                .error(R.drawable.main_image)
                .into(holder.communityProfileImage)
        }
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
                R.id.action_mainFragment_to_fullScreenFragment,
                bundle
            )
        }

        holder.communityPostTitle.text = post.postTitle
        holder.communityUserNickName.text = post.userNickName
        holder.communityPostDateCreated.text = post.postDateCreated.toString()
        holder.communityContent.text = post.postContent
        holder.communityCommentCounter.text= post.postComment?.size.toString()


        Log.d("어떤 카테고리",post.postCategory.toString())
        holder.communityPostCategoryTextView.text= post.postCategory.toString()
        /*when(post.postCategory.toString()){
            "일상"->{
//                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_daily))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "산책일지"->{
//                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_walk_log))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "산책 메이트 구해요"->{
//                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_walking_maid_matching))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "장소 후기"->{
//                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_place_reviews))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "애견용품 후기"->{
//                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_dog_product_reviews))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "시츄 아지트"->{
//                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_shih_tzu))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "푸들 아지트"->{
//                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_poodle))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "포메라니안 아지트"->{
//                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_pomeranian))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "치와와 아지트"->{
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "몰티즈 아지트"->{
//                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_maltese))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "진돗개 아지트"->{
//                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_jindo_dog))
//                holder.communityPostCategoryTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "리트리버 아지트"->{
//                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_retriever))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            "웰시 코기 아지트"->{
//                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_welsh_corgi))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
            else->{
//                holder.communityPostCategoryCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_puppies))
                holder.communityPostCategoryTextView.text= post.postCategory.toString()
            }
        }*/

        holder.communityFavoriteLottie.scaleX = 2.0f
        holder.communityFavoriteLottie.scaleY = 2.0f

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        val collectionName = "Post"
        val documentId = post.postID.toString()
        val docRef = db.collection(collectionName).document(documentId)

        docRef.addSnapshotListener { documentSnapshot, e ->
            if (e != null) {
                // 오류 처리
                return@addSnapshotListener
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                // 문서가 존재하고 데이터가 변경됨
                val likedUserIds = documentSnapshot.get("likedUserIds") as? List<String>

                // 사용자 ID가 likedUserIds에 포함되어 있는지 확인
                val isLiked = likedUserIds?.contains(user) == true
                val numberOfLikes = likedUserIds?.size ?: 0

                Log.d("numberOfLikes", numberOfLikes.toString())
                holder.communityFavoriteCounter.text = numberOfLikes.toString()

                // 좋아요 상태에 따라 버튼 색상 설정
                if (isLiked) {
                    holder.communityFavoriteLottie.playAnimation()
                } else {
                    holder.communityFavoriteLottie.cancelAnimation()
                    holder.communityFavoriteLottie.progress = 0f
                }

                holder.communityFavoriteLottie.setOnClickListener {
                    // 좋아요 토글 로직 추가
                    val updatedLikedUserIds = likedUserIds?.toMutableList() ?: mutableListOf()

                    if (isLiked) {
                        // 사용자가 이미 좋아요를 누른 경우: 좋아요 취소
                        updatedLikedUserIds.remove(user)
                        holder.communityFavoriteLottie.cancelAnimation()
                        holder.communityFavoriteLottie.progress = 0f
                    } else {
                        // 사용자가 좋아요를 누르지 않은 경우: 좋아요 추가
                        updatedLikedUserIds.add(user.toString())
                        holder.communityFavoriteLottie.playAnimation()
                    }

                    // Firestore 업데이트
                    docRef.update("likedUserIds", updatedLikedUserIds)
                        .addOnSuccessListener {
                            // 업데이트 성공
                        }
                        .addOnFailureListener { e ->
                            Log.d("CommunityAdapter", e.toString())
                        }
                }
            } else {
                // 문서가 존재하지 않음 또는 삭제됨
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