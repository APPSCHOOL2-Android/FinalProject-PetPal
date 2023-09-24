package com.petpal.mungmate.ui.manageblock

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowBlockUserBinding
import java.lang.reflect.Field

class BlockUserRecyclerAdapter() : ListAdapter<String,BlockUserRecyclerAdapter.BlockUserViewHolder>(BlockStateDiffCallback()) {
    val usersCollectionRef = Firebase.firestore.collection("users")
    val storageRef = FirebaseStorage.getInstance().reference

    inner class BlockUserViewHolder(private val rowBlockUserBinding: RowBlockUserBinding):
        RecyclerView.ViewHolder(rowBlockUserBinding.root) {
            fun bind(blockUserId: String){
                usersCollectionRef.document(blockUserId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        val nickname = documentSnapshot.getString("nickname").toString()
                        val userImage = documentSnapshot.getString("userImage").toString()

                        // 닉네임
                        rowBlockUserBinding.textViewUserKNickName.text = nickname
                        // 프로필 이미지
                        val storageRef = storageRef.child(userImage)
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            val imageUri = uri.toString()
                            Glide
                                .with(itemView.context)
                                .load(uri)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .fitCenter()
                                .fallback(R.drawable.main_image)
                                .into(rowBlockUserBinding.textViewUserImage)
                        }
                    }

                // 차단 해제
                rowBlockUserBinding.buttonUnBlock.setOnClickListener {
                    // 차단 목록에서 해당 사용자 ID 제거
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val userIdToUnblock = getItem(position)

                        // Firestore 문서 업데이트
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        if (currentUser != null) {
                            val userDocument = usersCollectionRef.document(currentUser.uid)
                            
                            // 현재 사용자의 차단 목록에서 userIdToUnblock 제거
                            userDocument.update("blockUserList", FieldValue.arrayRemove(userIdToUnblock))
                                .addOnSuccessListener { 
                                    // 업데이트 성공, RecyclerView 갱신
                                    val updateBlockUserList = currentList.toMutableList()
                                    updateBlockUserList.remove(userIdToUnblock)
                                    submitList(updateBlockUserList)
                                    notifyDataSetChanged()
                                }
                                .addOnFailureListener { 
                                    // 업데이트 실패
                                }
                        }
                    }
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
        holder.bind(getItem(position))
    }
}