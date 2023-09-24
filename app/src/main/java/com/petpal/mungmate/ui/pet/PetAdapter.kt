package com.petpal.mungmate.ui.pet

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowPetBinding

class PetAdapter(private val context: Context) :
    ListAdapter<PetUiState, PetAdapter.PetViewHolder>(PetUiStateDiffCallback()) {

    inner class PetViewHolder(private val rowPetBinding: RowPetBinding) :
        ViewHolder(rowPetBinding.root) {
//            val textViewPetName = rowPetBinding.textViewPetName

        fun bind(pet: PetUiState) {
            rowPetBinding.run {

                val storage = FirebaseStorage.getInstance()
                val storageRef = storage.reference

                val profileImage: StorageReference = storageRef.child(pet.image!!)
                profileImage.downloadUrl
                    .addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        Glide
                            .with(context)
                            .load(imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .fitCenter()
                            .fallback(R.drawable.main_image)
                            .into(imageViewPet)
                    }


                textViewPetName.text = pet.name
                imageView10.setImageResource(
                    if (pet.sex == "1") R.drawable.female_20px
                    else R.drawable.male_20px
                )
                textViewPetInfo.text = "${pet.age}살 • ${pet.breed} • ${pet.weigh}kg"
                textViewPetDesc.text = pet.character

                root.setOnClickListener {
                    val navController = Navigation.findNavController(itemView)
                    navController.navigate(
                        R.id.action_managePetFragment_to_addPetFragment,
                        bundleOf("isAdd" to false, "isUserJoin" to false)
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