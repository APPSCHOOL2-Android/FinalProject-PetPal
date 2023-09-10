package com.petpal.mungmate.ui.customercenter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.petpal.mungmate.databinding.FragmentAnnouncementDetailBinding

class AnnouncementDetailFragment : Fragment() {
    private var _fragmentAnnouncementDetailBinding: FragmentAnnouncementDetailBinding? = null
    private val fragmentAnnouncementDetailBinding get() = _fragmentAnnouncementDetailBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentAnnouncementDetailBinding = FragmentAnnouncementDetailBinding.inflate(inflater)
        return fragmentAnnouncementDetailBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentAnnouncementDetailBinding.run {
            toolbarAnnouncementDetail.run {
                setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
            }
        }
    }

}