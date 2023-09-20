package com.petpal.mungmate.ui.placereview

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.petpal.mungmate.MainActivity
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentPlaceReviewBinding
import com.petpal.mungmate.databinding.RowPlaceReviewBinding
import com.petpal.mungmate.model.Review
import com.petpal.mungmate.ui.walk.WalkRepository
import com.petpal.mungmate.ui.walk.WalkViewModel
import com.petpal.mungmate.ui.walk.WalkViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PlaceReviewFragment : Fragment() {
    private lateinit var fragmentPlaceReviewBinding: FragmentPlaceReviewBinding
    private lateinit var reviewAdapter: ReviewAdapter
    private val viewModel: PlaceReviewViewModel by viewModels { PlaceReviewViewModelFactory(PlaceReviewRepository()) }
    private lateinit var mainActivity: MainActivity
    private val auth= Firebase.auth
    private val user=auth.currentUser
    private val userUid=user?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val placeName = arguments?.getString("place_name")
        val phone = arguments?.getString("phone")
        val roadAddressName = arguments?.getString("place_road_adress_name")
        val placeCategory = arguments?.getString("place_category")
        val placeId = arguments?.getString("place_id")
        if(placeId!=null) {
            Log.d("placeididid", placeId)
        }else{
            Log.d("placeididid", "null이야")
        }
        reviewAdapter = placeId?.let { ReviewAdapter(emptyList(), it) }!!
        val avgRating=arguments?.getFloat("avgRating")

        mainActivity = activity as MainActivity
        fragmentPlaceReviewBinding = FragmentPlaceReviewBinding.inflate(layoutInflater)

       // reviewAdapter = placeId?.let { ReviewAdapter(emptyList(), it) }!!
        fragmentPlaceReviewBinding.reviewsRecyclerView.adapter = reviewAdapter
        fragmentPlaceReviewBinding.reviewsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        if (avgRating != null) { fragmentPlaceReviewBinding.placeUserRatingBar1.rating=avgRating }
        fragmentPlaceReviewBinding.textViewPlaceReviewTitle.text = placeName
        fragmentPlaceReviewBinding.textView22.text = roadAddressName
        fragmentPlaceReviewBinding.textView25.text = phone
        fragmentPlaceReviewBinding.textView21.text = placeCategory

        lifecycleScope.launch {
            viewModel.reviews.collect { updatedReviews ->
                if(updatedReviews.isEmpty())
                {
                    fragmentPlaceReviewBinding.noReviewsTextView.visibility=View.VISIBLE
                    fragmentPlaceReviewBinding.reviewsRecyclerView.visibility=View.GONE
                }else{
                    fragmentPlaceReviewBinding.noReviewsTextView.visibility=View.GONE
                    fragmentPlaceReviewBinding.reviewsRecyclerView.visibility=View.VISIBLE
                    reviewAdapter.updateReviews(updatedReviews)
                }

            }
        }

        placeId?.let {
            viewModel.fetchAllReviewsForPlace(it)
        }

        return fragmentPlaceReviewBinding.root
    }

    inner class ReviewAdapter(private var reviews: List<Review>,private val placeId: String) : RecyclerView.Adapter<ReviewAdapter.ViewHolderClass>() {
        inner class ViewHolderClass(rowBinding: RowPlaceReviewBinding) : RecyclerView.ViewHolder(rowBinding.root) {
            val ratingBar: RatingBar = rowBinding.placeReviewDetailRatingBar
            val usernameTextView: TextView = rowBinding.textViewPlaceReviewDetailUserName
            val dateTextView: TextView = rowBinding.textViewPlaceReviewDetailDate
            val commentTextView: TextView = rowBinding.textView4
            val reviewImageView: ImageView = rowBinding.imageView11
            val placeReviewModify:TextView=rowBinding.textViewPlaceReviewModify
            val placeReviewDelete:TextView=rowBinding.textViewPlaceReviewDelete

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
            val rowBinding = RowPlaceReviewBinding.inflate(layoutInflater, parent, false)


            return ViewHolderClass(rowBinding)

        }

        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {


            val review = reviews[position]
            holder.usernameTextView.text = review.userNickname
            holder.dateTextView.text = review.date
            holder.ratingBar.rating = review.rating!!
            holder.commentTextView.text = review.comment
            val reviewId = "${review.date}_${review.userid}"//리뷰의 문서 id 날짜+userid로 구성
            val widthPx = dpToPx(100, holder.reviewImageView.context)
            val heightPx = dpToPx(100, holder.reviewImageView.context)

            review.imageRes?.let { imageUrl ->
                Glide.with(holder.reviewImageView.context)
                    .load(imageUrl)
                    .override(widthPx, heightPx) // 추가된 부분
                    .into(holder.reviewImageView)
            }

            if (userUid == review.userid) {
                holder.placeReviewModify.visibility = View.VISIBLE
                holder.placeReviewDelete.visibility = View.VISIBLE
            } else {
                holder.placeReviewModify.visibility = View.GONE
                holder.placeReviewDelete.visibility = View.GONE
            }
            holder.placeReviewModify.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("reviewUserId", review.userid)
                bundle.putString("reviewDate", review.date)
                bundle.putString("reviewContent", review.comment)
                bundle.putFloat("reviewRating", review.rating!!)
                bundle.putString("reviewImageURL", review.imageRes)
                bundle.putString("placeId",placeId)
                mainActivity.navigate(R.id.action_placeReviewFragment_to_placeReviewModifyFragment,bundle)
            }

            holder.placeReviewDelete.setOnClickListener {
                val builder = AlertDialog.Builder(it.context)
                builder.setTitle("멍메이트")
                builder.setMessage("정말로 이 리뷰를 삭제하시겠습니까?")
                builder.setPositiveButton("확인") { dialog, _ ->
                    viewModel.deleteReviewForPlace(placeId, reviewId)
                    dialog.dismiss()
                }
                builder.setNegativeButton("취소") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.create().show()
            }
        }

        override fun getItemCount(): Int = reviews.size

        fun updateReviews(newReviews: List<Review>) {
            reviews = newReviews
            notifyDataSetChanged()
        }
    }

    fun dpToPx(dp: Int, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
}


class PlaceReviewViewModelFactory(private val repository: PlaceReviewRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaceReviewViewModel::class.java)) {
            return PlaceReviewViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


