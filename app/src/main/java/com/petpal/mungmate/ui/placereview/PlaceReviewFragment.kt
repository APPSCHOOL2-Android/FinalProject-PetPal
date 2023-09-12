package com.petpal.mungmate.ui.placereview

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentPlaceReviewBinding
import com.petpal.mungmate.databinding.RowPlaceReviewBinding

class PlaceReviewFragment : Fragment() {
    val reviews = listOf(
        Review("User3", "2023.09.09", 2.5f, "별로였어요.", R.drawable.dog_home),
        Review("User4", "2023.09.08", 4.0f, "다시 방문하고 싶네요.", R.drawable.dog_home),
        Review("User5", "2023.09.07", 3.5f, "서비스가 좋았습니다.", R.drawable.dog_home),
        Review("User6", "2023.09.06", 5.0f, "최고의 경험이었어요!", R.drawable.dog_home),
        Review("User7", "2023.09.05", 1.5f, "다시는 가고 싶지 않아요.", R.drawable.dog_home),
        Review("User8", "2023.09.04", 4.5f, "정말 매력적인 곳이에요.", R.drawable.dog_home),
        Review("User9", "2023.09.03", 3.0f, "평범했어요.", R.drawable.dog_home),
        Review("User10", "2023.09.02", 2.0f, "기대 이하였습니다.", R.drawable.dog_home)
    )
    lateinit var fragmentPlaceReviewBinding:FragmentPlaceReviewBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPlaceReviewBinding = FragmentPlaceReviewBinding.inflate(layoutInflater)


        fragmentPlaceReviewBinding.reviewsRecyclerView.adapter = ReviewAdapter(reviews)
        fragmentPlaceReviewBinding.reviewsRecyclerView.layoutManager=LinearLayoutManager(requireContext())


        return fragmentPlaceReviewBinding.root
    }


    inner class ReviewAdapter(private val reviews: List<Review>) : RecyclerView.Adapter<ReviewAdapter.ViewHolderClass>() {
        inner class ViewHolderClass(rowBinding:RowPlaceReviewBinding) : RecyclerView.ViewHolder(rowBinding.root),OnClickListener{


            var ratingBar: RatingBar
            var usernameTextView: TextView
            var dateTextView: TextView
            var commentTextView: TextView
            var reviewImageView: ImageView

            init{
                ratingBar=rowBinding.placeReviewDetailRatingBar
                usernameTextView=rowBinding.textViewPlaceReviewDetailUserName
                dateTextView=rowBinding.textViewPlaceReviewDetailDate
                commentTextView=rowBinding.textView4
                reviewImageView=rowBinding.imageView11
            }
            //온클릭이 있어야 하나 ?
            override fun onClick(v: View?) {
                TODO("Not yet implemented")
            }

        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolderClass {
            val rowBinding = RowPlaceReviewBinding.inflate(layoutInflater)
            val viewHolderClass = ViewHolderClass(rowBinding)
            rowBinding.root.setOnClickListener(viewHolderClass)
            val params = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            rowBinding.root.layoutParams = params
            return viewHolderClass
        }

        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            val review = reviews[position]
            holder.usernameTextView.text = review.username
            holder.dateTextView.text = review.date
            holder.ratingBar.rating = review.rating
            holder.commentTextView.text = review.comment
            holder.reviewImageView.setImageResource(review.imageRes)
        }

        override fun getItemCount(): Int = reviews.size
    }
}

data class Review(
    val username: String,
    val date: String,
    val rating: Float,
    val comment: String,
    val imageRes: Int
)