package com.petpal.mungmate.ui.orderhistory

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowOrderHistoryItemBinding

import com.petpal.mungmate.model.Item
import java.text.DecimalFormat

class OrderHistoryItemAdapter(private val dataList: List<Item>): RecyclerView.Adapter<OrderHistoryItemAdapter.ViewHolder>() {
    inner class ViewHolder(private val rowBinding: RowOrderHistoryItemBinding): RecyclerView.ViewHolder(rowBinding.root) {
        fun bind(item: Item){
            rowBinding.run {
                Glide.with(itemView.context)
                    .load(item.mainImageId)
                    .into(imageViewOrderItem)

                textViewOrderItemBrand.text = item.brand
                textViewOrderItemName.text = item.name
                textViewOrderItemOption.text = "${item.option} | ${item.amount}개"
                // 1,000 천의 자리에 반점 추가
                val decimalFormat = DecimalFormat("#,###")
                textViewOrderItemTotalPrice.text = decimalFormat.format(item.price * item.amount) + "원"
            }
        }
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
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }
}