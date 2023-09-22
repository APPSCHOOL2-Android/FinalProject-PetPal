package com.petpal.mungmate.ui.matchhistory

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.petpal.mungmate.databinding.RowMatchHistoryBinding

class MatchHistoryAdapter(private val context: Context) :
    ListAdapter<MatchHistoryUiState, MatchHistoryAdapter.MatchHistoryViewHolder>(
        MatchHistoryUiStateDiffCallback()
    ) {

    inner class MatchHistoryViewHolder(private val rowMatchHistoryBinding: RowMatchHistoryBinding) :
        ViewHolder(rowMatchHistoryBinding.root) {

        fun bind(matchHistoryUiState: MatchHistoryUiState) {
            rowMatchHistoryBinding.run {


                Glide
                    .with(context)
                    .load(matchHistoryUiState.userImage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .into(matchHistoryUserImage)

                textViewPlanUserNickname.text = matchHistoryUiState.userNickName
                textViewPlanDog.text = matchHistoryUiState.petName
                textViewPlanPlace.text = matchHistoryUiState.place
                textViewPlanDate.text = matchHistoryUiState.timestamp
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchHistoryViewHolder {
        val rowBinding = RowMatchHistoryBinding.inflate(LayoutInflater.from(parent.context))

        rowBinding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return MatchHistoryViewHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: MatchHistoryViewHolder, position: Int) {
        holder.bind(getItem(position) as MatchHistoryUiState)
    }

}
