package com.petpal.mungmate.ui.manageblock

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.petpal.mungmate.databinding.RowBlockUserBinding

class BlockUserRecyclerAdapter: RecyclerView.Adapter<BlockUserRecyclerAdapter.BlockUserViewHolder>() {

    inner class BlockUserViewHolder(rowBlockUserBinding: RowBlockUserBinding):
        RecyclerView.ViewHolder(rowBlockUserBinding.root) {
        val textViewBlockedUserNickName = rowBlockUserBinding.textViewUserKNickName

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockUserViewHolder {
        val rowBinding = RowBlockUserBinding.inflate(LayoutInflater.from(parent.context))

        rowBinding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return BlockUserViewHolder(rowBinding)
    }

    override fun getItemCount(): Int {
        return 100
    }

    override fun onBindViewHolder(holder: BlockUserViewHolder, position: Int) {
        holder.textViewBlockedUserNickName.text = position.toString()
    }
}