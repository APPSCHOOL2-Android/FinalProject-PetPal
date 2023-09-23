package com.petpal.mungmate.ui.community

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentFullScreenBinding


class FullScreenFragment : Fragment() {
    lateinit var fullScreenBinding: FragmentFullScreenBinding
    lateinit var postGetId: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fullScreenBinding = FragmentFullScreenBinding.inflate(inflater)
        val args: FullScreenFragmentArgs by navArgs()
        val postid = args.img
        postGetId = postid

        Glide
            .with(requireContext())
            .load(postGetId)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .into(fullScreenBinding.fullScreenImageView)

//        fullScreenBinding.fullScreenImageView.setOnClickListener {
//            it.findNavController()
//                .popBackStack()
//        }
        fullScreenBinding.run {
            fullScreenToolbar.run {
                bringToFront()
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                navigationIcon?.setColorFilter(
                    resources.getColor(R.color.white),
                    PorterDuff.Mode.SRC_ATOP
                )
                setNavigationOnClickListener {
                    it.findNavController()
                        .popBackStack()
                }
            }
        }

        return fullScreenBinding.root
    }
}