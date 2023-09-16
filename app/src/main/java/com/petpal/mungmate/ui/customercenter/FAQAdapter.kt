package com.petpal.mungmate.ui.customercenter

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowFaqBinding
import com.petpal.mungmate.model.FAQ

// Expendable RecyclerView
class FAQAdapter(private val dataList: List<FAQ>): RecyclerView.Adapter<FAQAdapter.ViewHolder>(), Filterable {

    private val expendedItems = SparseBooleanArray()    // 아이템의 클릭 상태를 저장할 array
    private var filteredDataList = dataList


    // 직전에 클릭됐던 아이템의 position
    private var prePosition = -1

    inner class ViewHolder(private val rowBinding: RowFaqBinding): RecyclerView.ViewHolder(rowBinding.root){

        fun bind(faq: FAQ) {
            rowBinding.textViewFaqCategory.text = faq.category
            rowBinding.textViewFaqQuestion.text = faq.question
            rowBinding.textViewFaqBody.text = faq.answer      // 확장될 서브뷰

            // 클릭 상태에 따라 UI 변경 - RecyclerView 스크롤해도 상태 유지
            if (expendedItems.get(absoluteAdapterPosition)) {
                rowBinding.textViewFaqBody.visibility = View.VISIBLE
                rowBinding.imageViewFaqExpand.setImageResource(R.drawable.expand_less_24px)
            } else {
                rowBinding.textViewFaqBody.visibility = View.GONE
                rowBinding.imageViewFaqExpand.setImageResource(R.drawable.expand_more_24px)
            }

            // 헤더 클릭시 답변 확장
            rowBinding.root.setOnClickListener {
                if (expendedItems.get(absoluteAdapterPosition)) {
                    // 펼쳐진 아이템 클릭 시
                    expendedItems.delete(absoluteAdapterPosition)
                } else {
                    // 펼쳐지지 않은 Item 클릭 시
                    // 직전에 클릭됐던 아이템의 클릭 상태 지우고 현재 아이템 새로 등록하기
                    expendedItems.delete(prePosition)
                    expendedItems.put(absoluteAdapterPosition, true)
                }

                // 해당 포지션의 변화 알리기 - 이전 클릭, 현재 클릭
                if (prePosition != -1) notifyItemChanged(prePosition)
                notifyItemChanged(absoluteAdapterPosition)
                prePosition = absoluteAdapterPosition
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowBinding = RowFaqBinding.inflate(LayoutInflater.from(parent.context))

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

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint.toString()
                val filteredList = if (query == "전체") {
                    dataList
                } else {
                    dataList.filter { it.category == query }
                }

                val result = FilterResults()
                result.values = filteredList
                return result
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.values is List<*>) {
                    filteredDataList = results.values as List<FAQ>
                    notifyDataSetChanged()
                }
            }

        }
    }
}