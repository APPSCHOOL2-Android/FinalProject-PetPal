package com.petpal.mungmate.ui.managepet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowPetBinding

class PetAdapter :
    ListAdapter<PetUiState, PetAdapter.PetViewHolder>(PetUiStateDiffCallback()) {

    inner class PetViewHolder(private val rowPetBinding: RowPetBinding) :
        ViewHolder(rowPetBinding.root) {
//            val textViewPetName = rowPetBinding.textViewPetName

        fun bind(pet: PetUiState) {
            rowPetBinding.run {
                textViewPetName.text = pet.name
                textViewPetInfo.text = "${pet.age}살 • ${pet.breed} • ${pet.weigh}kg"
                textViewPetDesc.text = pet.character

                root.setOnClickListener {
                    val navController = Navigation.findNavController(itemView)
                    navController.navigate(R.id.action_managePetFragment_to_addPetFragment,
                        bundleOf("isAdd" to false)
                    )
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val rowBinding = RowPetBinding.inflate(LayoutInflater.from(parent.context))

        rowBinding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return PetViewHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(getItem(position) as PetUiState)
//        holder.textViewPetName.text = position.toString()
    }
}