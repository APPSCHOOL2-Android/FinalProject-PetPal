package com.petpal.mungmate.ui.customercenter

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowFaqBinding
import com.petpal.mungmate.model.FAQ

// Expendable RecyclerView
class FAQAdapter(private val faqList: List<FAQ>): RecyclerView.Adapter<FAQAdapter.ViewHolder>() {
    // 아이템의 클릭 상태를 저장할 array
    private val expendedItems = SparseBooleanArray()

    // 직전에 클릭됐던 아이템의 position
    private var prePosition = -1

    inner class ViewHolder(private val rowBinding: RowFaqBinding): RecyclerView.ViewHolder(rowBinding.root){

        fun bind(faq: FAQ) {
            rowBinding.textViewFaqCategory.text = faq.category
            rowBinding.textViewFaqQuestion.text = faq.question
            rowBinding.textViewFaqAnswer.text = faq.answer      // 확장될 서브뷰

            // 클릭 상태에 따라 UI 변경 - RecyclerView 스크롤해도 상태 유지
            if (expendedItems.get(adapterPosition)) {
                rowBinding.textViewFaqAnswer.visibility = View.VISIBLE
                rowBinding.imageViewFaqExpand.setImageResource(R.drawable.expand_less_24px)
            } else {
                rowBinding.textViewFaqAnswer.visibility = View.GONE
                rowBinding.imageViewFaqExpand.setImageResource(R.drawable.expand_more_24px)
            }

            // 헤더 클릭시 답변 확장
            rowBinding.root.setOnClickListener {

                if (expendedItems.get(adapterPosition)) {
                    // 펼쳐진 아이템 클릭 시
                    expendedItems.delete(adapterPosition)
                } else {
                    // 펼쳐지지 않은 Item 클릭 시
                    // 직전에 클릭됐던 아이템의 클릭 상태 지우고 현재 아이템 새로 등록하기
                    expendedItems.delete(prePosition)
                    expendedItems.put(adapterPosition, true)
                }

                // 해당 포지션의 변화 알리기 - 이전 클릭, 현재 클릭
                if (prePosition != -1) notifyItemChanged(prePosition)
                notifyItemChanged(adapterPosition)
                prePosition = adapterPosition
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
        return faqList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(faqList[position])
    }
}