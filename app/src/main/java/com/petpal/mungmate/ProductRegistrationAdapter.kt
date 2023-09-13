package com.petpal.mungmate

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.petpal.mungmate.databinding.ItemProductRegistrationImageviewBinding
import com.petpal.mungmate.model.Image

class ProductRegistrationAdapter(
    val mainImageList: MutableList<Pair<Image, Bitmap>>,
    val buttonAddMainImage: Button
) :
    RecyclerView.Adapter<ProductRegistrationAdapter.MainImageViewHolder>() {
    inner class MainImageViewHolder(itemImageviewDeleteBinding: ItemProductRegistrationImageviewBinding) :
        RecyclerView.ViewHolder(itemImageviewDeleteBinding.root) {
        val imageViewMain: ImageView = itemImageviewDeleteBinding.imageViewDelete
        val textViewIsMain: TextView = itemImageviewDeleteBinding.textViewIsMain
        private val buttonDeleteMain: Button = itemImageviewDeleteBinding.buttonDelete

        init {
            // 우측 상단 X버튼 클릭시 이미지 삭제
            buttonDeleteMain.setOnClickListener {
                mainImageList.removeAt(adapterPosition)
                buttonAddMainImage.text =
                    "${mainImageList.count()}/5"
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainImageViewHolder {
        val imageViewBinding = ItemProductRegistrationImageviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        imageViewBinding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return MainImageViewHolder(imageViewBinding)
    }

    override fun getItemCount(): Int {
        return mainImageList.size
    }

    override fun onBindViewHolder(holder: MainImageViewHolder, position: Int) {
        // 메인 이미지 리스트에 저장된 Bitmap을 커스텀 뷰에 표시
        holder.imageViewMain.setImageBitmap(mainImageList[position].second)
        // 첫번째 이미지를 대표 이미지로 지정
        holder.textViewIsMain.visibility = if (position == 0) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }
}
