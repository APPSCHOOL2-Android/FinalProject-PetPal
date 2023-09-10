package com.petpal.mungmate.ui.matchhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.petpal.mungmate.databinding.RowMatchHistoryBinding

class MatchHistoryRecyclerAdapter : Adapter<MatchHistoryRecyclerAdapter.MatchHistoryViewBinding>() {
    inner class MatchHistoryViewBinding(rowMatchHistoryBinding: RowMatchHistoryBinding) :
        ViewHolder(rowMatchHistoryBinding.root) {

        val textViewPlanUserNickname = rowMatchHistoryBinding.textViewPlanUserNickname
        val textViewPlanDate = rowMatchHistoryBinding.textViewPlanDate
        val textViewPlanDog = rowMatchHistoryBinding.textViewPlanDog
        val textViewPlanPlace = rowMatchHistoryBinding.textViewPlanPlace

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchHistoryViewBinding {
        val rowBinding = RowMatchHistoryBinding.inflate(LayoutInflater.from(parent.context))

        rowBinding.root.layoutParams = ViewGroup.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )

        return MatchHistoryViewBinding(rowBinding)
    }

    override fun getItemCount(): Int {
        return 100
    }

    override fun onBindViewHolder(holder: MatchHistoryViewBinding, position: Int) {
        holder.textViewPlanUserNickname.text = position.toString()
    }


}
