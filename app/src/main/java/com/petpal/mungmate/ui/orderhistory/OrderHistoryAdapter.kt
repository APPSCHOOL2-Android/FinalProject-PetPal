package com.petpal.mungmate.ui.orderhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowOrderHistoryBinding
import com.petpal.mungmate.model.Order

class OrderHistoryAdapter(private val dataList: List<Order>): RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>(), Filterable {
    private var filteredDataList: List<Order> = dataList    // 필터링 된 데이터 리스트

    inner class ViewHolder(private val rowBinding: RowOrderHistoryBinding): RecyclerView.ViewHolder(rowBinding.root){

        fun bind(order: Order) {
            rowBinding.run {
                textViewOrderHistoryDate.text = order.orderDate
                textViewOrderHistoryStatus.text = order.orderStatus

                // 주문 상품 목록
                recyclerViewOrderHistoryItem.run {
                    adapter = OrderHistoryItemAdapter(order.itemList)
                    layoutManager = LinearLayoutManager(itemView.context)
                    // addItemDecoration(DividerItemDecoration(itemView.context, DividerItemDecoration.VERTICAL))
                }

                layoutOrder.setOnClickListener {
                    // 주문 상세 화면 이동
                    val navController = Navigation.findNavController(itemView)
                    navController!!.navigate(R.id.action_orderHistoryFragment_to_orderHistoryDetailFragment)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowBinding = RowOrderHistoryBinding.inflate(LayoutInflater.from(parent.context))

        rowBinding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return ViewHolder(rowBinding)
    }

    override fun getItemCount(): Int {
        return filteredDataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredDataList[position])
    }

    // 주문 상태별 필터링
    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint.toString()
                val filteredList = dataList.filter { it.orderStatus == query }

                val result = FilterResults()
                result.values = filteredList
                return result
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.values is List<*>) {
                    filteredDataList = results.values as List<Order>
                    notifyDataSetChanged()
                }
            }
        }
    }


}