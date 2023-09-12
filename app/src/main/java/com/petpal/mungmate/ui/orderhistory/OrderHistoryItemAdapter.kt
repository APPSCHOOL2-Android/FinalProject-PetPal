package com.petpal.mungmate.ui.orderhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.petpal.mungmate.databinding.RowOrderHistoryItemBinding

import com.petpal.mungmate.model.Item

class OrderHistoryItemAdapter(private val dataList: List<Item>): RecyclerView.Adapter<OrderHistoryItemAdapter.ViewHolder>() {
    inner class ViewHolder(private val rowBinding: RowOrderHistoryItemBinding): RecyclerView.ViewHolder(rowBinding.root) {
        fun bind(item: Item){}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowBinding = RowOrderHistoryItemBinding.inflate(LayoutInflater.from(parent.context))

        rowBinding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return ViewHolder(rowBinding)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }
}