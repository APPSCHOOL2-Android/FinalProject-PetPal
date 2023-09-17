package com.petpal.mungmate.ui.matchhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowPetSimpleBinding

class PetFilterAdapter :
    ListAdapter<PetFilterUiState, PetFilterAdapter.PetFilterViewHolder>(PetFilterUiStateDiffCallback()) {

    inner class PetFilterViewHolder(private val rowPetSimpleBinding: RowPetSimpleBinding) :
        ViewHolder(rowPetSimpleBinding.root) {

        fun bind(petFilterUiState: PetFilterUiState) {
            rowPetSimpleBinding.run {
                //TODO: 받아온 이미지로 바꾸기
                imageRowSimplePet.setImageResource(R.drawable.pets_24px)
//                imageRowSimplePet.setImageResource(petFilterUiState.image)
                textView.text = petFilterUiState.name

                root.setOnClickListener {
                    //TODO: 필터링하기
                }

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetFilterViewHolder {
        val rowBinding = RowPetSimpleBinding.inflate(LayoutInflater.from(parent.context))

        rowBinding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        return PetFilterViewHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: PetFilterViewHolder, position: Int) {
        holder.bind(getItem(position) as PetFilterUiState)
    }
}