package com.petpal.mungmate.ui.customercenter

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowInquiryBinding
import com.petpal.mungmate.model.Inquiry

class InquiryAdapter(private val dataList: List<Inquiry>): RecyclerView.Adapter<InquiryAdapter.ViewHolder>() {
    private val expendedItems = SparseBooleanArray()
    private var prePosition = -1

    inner class ViewHolder(private val rowBinding: RowInquiryBinding): RecyclerView.ViewHolder(rowBinding.root){
        fun bind(inquiry: Inquiry) {
            rowBinding.run {
                textViewInquiryTitle.text = "[${inquiry.category}]${inquiry.title}"
                textViewInquiryQuestion.text = inquiry.question
                textViewInquiryDateCreated.text = inquiry.dateCreated

                // 확장 여부에 따라 UI 변경 (RecyclerView 재활용 시 리셋 방지)
                if (expendedItems.get(absoluteAdapterPosition)) {
                    imageViewInquiryExpand.setImageResource(R.drawable.expand_less_24px)
                    layoutInquiryBody.visibility = View.VISIBLE

                    // 답변 여부에 따라 UI 변경
                    if (inquiry.state) {
                        chipInquiryState.text = "답변완료"
                        layoutInquiryAnswer.visibility = View.VISIBLE
                        textViewInquiryAnswer.text = inquiry.answer
                    } else {
                        chipInquiryState.text = "답변대기"
                        layoutInquiryAnswer.visibility = View.GONE
                    }
                } else {
                    imageViewInquiryExpand.setImageResource(R.drawable.expand_more_24px)
                    layoutInquiryBody.visibility = View.GONE
                }

                // 헤더 클릭시 확장 상태 변경
                root.setOnClickListener {
                    if (expendedItems.get(absoluteAdapterPosition)) {
                        expendedItems.delete(absoluteAdapterPosition)
                    } else {
                        expendedItems.delete(prePosition)
                        expendedItems.put(absoluteAdapterPosition, true)
                    }

                    if (prePosition != -1) notifyItemChanged(prePosition)
                    notifyItemChanged(absoluteAdapterPosition)
                    prePosition = absoluteAdapterPosition
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowBinding = RowInquiryBinding.inflate(LayoutInflater.from(parent.context))

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