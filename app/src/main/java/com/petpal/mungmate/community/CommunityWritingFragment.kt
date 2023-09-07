package com.petpal.mungmate.community


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentCommunityWritingBinding


class CommunityWritingFragment : Fragment() {

    lateinit var communityWritingBinding: FragmentCommunityWritingBinding
    private val categoryList = arrayOf(
        "전체", "일상", "산책일지", "장소후기"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        communityWritingBinding = FragmentCommunityWritingBinding.inflate(inflater)
        bottomNavigationViewGone()

        communityWritingBinding.run {
            toolbar()


            val itemAdapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.community_category_list, categoryList
            )
            categoryItem.setAdapter(itemAdapter)


        }

        return communityWritingBinding.root
    }


    private fun bottomNavigationViewGone() {
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.visibility = View.GONE
    }

    private fun FragmentCommunityWritingBinding.toolbar() {
        communityWritingToolbar.run {
            inflateMenu(R.menu.community_writing_menu)
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_communityWritingFragment_to_item_community)
            }
            setOnMenuItemClickListener {

                when (it?.itemId) {
                    R.id.item_complete -> {
                        Snackbar.make(requireView(), "완료", Snackbar.LENGTH_SHORT).show()
                    }

                }
                false
            }
        }
    }

}
