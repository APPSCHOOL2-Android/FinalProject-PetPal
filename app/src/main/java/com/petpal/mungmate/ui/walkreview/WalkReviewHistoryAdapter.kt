package com.petpal.mungmate.ui.walkreview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.petpal.mungmate.databinding.RowWalkReviewHistoryBinding

class WalkReviewHistoryAdapter: RecyclerView.Adapter<WalkReviewHistoryAdapter.ViewHolder>() {
    inner class ViewHolder(private val rowBinding: RowWalkReviewHistoryBinding): RecyclerView.ViewHolder(rowBinding.root){
        val textViewWalkReviewName = rowBinding.textViewWalkReviewName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowBinding = RowWalkReviewHistoryBinding.inflate(LayoutInflater.from(parent.context))

        rowBinding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return ViewHolder(rowBinding)
    }

    override fun getItemCount(): Int {
        return 100
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewWalkReviewName.text = "멍멍이네$position"
    }
}