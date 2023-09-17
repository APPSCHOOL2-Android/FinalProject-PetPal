package com.petpal.mungmate.ui.placereview

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val placeName = arguments?.getString("place_name")
        val phone = arguments?.getString("phone")
        val roadAddressName = arguments?.getString("place_road_adress_name")
        val placeCategory = arguments?.getString("place_cateogry")
        val placeId=arguments?.getString("place_id")

        fragmentPlaceReviewBinding = FragmentPlaceReviewBinding.inflate(layoutInflater)

        // Initialize the adapter and set to the RecyclerView
        reviewAdapter = ReviewAdapter(emptyList())
        fragmentPlaceReviewBinding.reviewsRecyclerView.adapter = reviewAdapter
        fragmentPlaceReviewBinding.reviewsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

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

    inner class ReviewAdapter(private var reviews: List<Review>) : RecyclerView.Adapter<ReviewAdapter.ViewHolderClass>() {
        inner class ViewHolderClass(rowBinding: RowPlaceReviewBinding) : RecyclerView.ViewHolder(rowBinding.root) {
            val ratingBar: RatingBar = rowBinding.placeReviewDetailRatingBar
            val usernameTextView: TextView = rowBinding.textViewPlaceReviewDetailUserName
            val dateTextView: TextView = rowBinding.textViewPlaceReviewDetailDate
            val commentTextView: TextView = rowBinding.textView4
            val reviewImageView: ImageView = rowBinding.imageView11
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
            val rowBinding = RowPlaceReviewBinding.inflate(layoutInflater, parent, false)
            return ViewHolderClass(rowBinding)
        }

        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            val review = reviews[position]
            holder.usernameTextView.text = review.userid
            holder.dateTextView.text = review.date
            holder.ratingBar.rating = review.rating!!
            holder.commentTextView.text = review.comment
            review.imageRes?.let { holder.reviewImageView.setImageResource(it) }
        }

        override fun getItemCount(): Int = reviews.size

        // This function updates the reviews list and notifies the RecyclerView of the changes
        fun updateReviews(newReviews: List<Review>) {
            reviews = newReviews
            notifyDataSetChanged()
        }
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


