package com.petpal.mungmate.ui.customercenter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentAnnouncementBinding
import com.petpal.mungmate.model.Announcement

class AnnouncementFragment : Fragment() {
    private var _fragmentAnnouncementBinding: FragmentAnnouncementBinding? = null
    private val fragmentAnnouncementBinding get() = _fragmentAnnouncementBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentAnnouncementBinding = FragmentAnnouncementBinding.inflate(inflater)
        return fragmentAnnouncementBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentAnnouncementBinding.run {
            toolbarAnnouncement.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            recyclerViewAnnouncement.run {
                adapter = AnnouncementAdapter(getSampleData())
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    MaterialDividerItemDecoration(
                        context,
                        MaterialDividerItemDecoration.VERTICAL
                    )
                )
            }
        }
    }

    private fun getSampleData(): List<Announcement> {
        return listOf(
            Announcement("멍메이트 업데이트 소식", "멍메이트를 소개합니다", "2023.09.10"),
            Announcement("멍메이트 업데이트 소식", "멍메이트를 소개합니다", "2023.09.10"),
            Announcement("멍메이트 업데이트 소식", "멍메이트를 소개합니다", "2023.09.10"),
            Announcement("멍메이트 업데이트 소식", "멍메이트를 소개합니다", "2023.09.10"),
            Announcement("멍메이트 업데이트 소식", "멍메이트를 소개합니다", "2023.09.10"),
            Announcement("멍메이트 업데이트 소식", "멍메이트를 소개합니다", "2023.09.10"),
            Announcement("멍메이트 업데이트 소식", "멍메이트를 소개합니다", "2023.09.10"),
            Announcement("멍메이트 업데이트 소식", "멍메이트를 소개합니다", "2023.09.10"),
            Announcement("멍메이트 업데이트 소식", "멍메이트를 소개합니다", "2023.09.10"),
        )
    }


}