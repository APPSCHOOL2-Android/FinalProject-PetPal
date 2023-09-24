package com.petpal.mungmate.ui.manageblock

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowBlockUserBinding
import com.petpal.mungmate.model.BlockUser

class BlockUserRecyclerAdapter(private val context: Context) : ListAdapter<BlockUser,BlockUserRecyclerAdapter.BlockUserViewHolder>(BlockStateDiffCallback()) {

    inner class BlockUserViewHolder(private val  rowBlockUserBinding: RowBlockUserBinding):
        RecyclerView.ViewHolder(rowBlockUserBinding.root) {
            fun bind(blockUser:BlockUser){
                rowBlockUserBinding.run{
                    val storage = FirebaseStorage.getInstance()
                    val storageRef = storage.reference

                    val profileImage: StorageReference = storageRef.child(blockUser.blackUserImage.toString())
                    profileImage.downloadUrl
                        .addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            Glide
                                .with(context)
                                .load(imageUrl)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .fitCenter()
                                .fallback(R.drawable.main_image)
                                .into(textViewUserImage)
                        }

                    textViewUserKNickName.text = blockUser.blackUserNickname
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockUserViewHolder {
        val rowBinding = RowBlockUserBinding.inflate(LayoutInflater.from(parent.context))

        rowBinding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return BlockUserViewHolder(rowBinding)
    }


    override fun onBindViewHolder(holder: BlockUserViewHolder, position: Int) {
        holder.bind(getItem(position) as BlockUser)
    }
}