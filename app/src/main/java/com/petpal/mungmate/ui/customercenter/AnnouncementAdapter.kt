package com.petpal.mungmate.ui.customercenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.RowAnnouncementBinding
import com.petpal.mungmate.model.Announcement

class AnnouncementAdapter(private val dataList: List<Announcement>):RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {

    inner class ViewHolder(private val rowBinding: RowAnnouncementBinding): RecyclerView.ViewHolder(rowBinding.root){

        fun bind(announcement: Announcement) {
            rowBinding.run {
                textViewRowAnnounceTitle.text = announcement.title
                textViewRowAnnounceDate.text = announcement.dateCreated

                root.setOnClickListener {
                    // 공지사항 상세 이동
                    val navController = Navigation.findNavController(itemView)
                    navController!!.navigate(R.id.action_announcementFragment_to_announcementDetailFragment)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowBinding = RowAnnouncementBinding.inflate(LayoutInflater.from(parent.context))

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