package com.petpal.mungmate.ui.walkhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.petpal.mungmate.databinding.RowWalkHistoryMonthlyBinding

class WalkHistoryAdapter : ListAdapter<WalkHistoryUiState, WalkHistoryAdapter.WalkHistoryViewHolder>(WalkHistoryUiStateDiffCallback()) {

    inner class WalkHistoryViewHolder(private val rowWalkHistoryMonthlyBinding: RowWalkHistoryMonthlyBinding):ViewHolder(rowWalkHistoryMonthlyBinding.root) {

        fun bind(walkMatchHistoryUiState: WalkHistoryUiState) {
            rowWalkHistoryMonthlyBinding.run {
                rowTextViewDay.text = walkMatchHistoryUiState.dayOfWeek
                rowTextViewDate.text = walkMatchHistoryUiState.day.toString()
                rowTextViewWalkSumm.text = "${walkMatchHistoryUiState.startTime}~${walkMatchHistoryUiState.endTime}"
                rowTextViewWalkTime.text = "${walkMatchHistoryUiState.endTime-walkMatchHistoryUiState.startTime}분"
                rowTextViewWalkDist.text = "${walkMatchHistoryUiState.distance}km"
                rowTextViewWalkMemo.text = walkMatchHistoryUiState.memo
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalkHistoryViewHolder {
        val rowBinding = RowWalkHistoryMonthlyBinding.inflate(LayoutInflater.from(parent.context))

        rowBinding.root.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )

        return WalkHistoryViewHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: WalkHistoryViewHolder, position: Int) {
        holder.bind(getItem(position) as WalkHistoryUiState)
    }

}
