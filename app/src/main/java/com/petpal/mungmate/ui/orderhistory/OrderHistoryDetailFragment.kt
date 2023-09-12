package com.petpal.mungmate.ui.orderhistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentOrderHistoryDetailBinding
import com.petpal.mungmate.model.Item

class OrderHistoryDetailFragment : Fragment() {
    private var _fragmentOrderHistoryDetailBinding: FragmentOrderHistoryDetailBinding? = null
    private val fragmentOrderHistoryDetailBinding get() = _fragmentOrderHistoryDetailBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentOrderHistoryDetailBinding = FragmentOrderHistoryDetailBinding.inflate(inflater)
        return fragmentOrderHistoryDetailBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentOrderHistoryDetailBinding.run {
            recyclerViewOrderDetailItem.run {
                adapter = OrderHistoryItemAdapter(getSampleData())
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
        }
    }

    private fun getSampleData(): List<Item> {
        return listOf(
            Item("펫팔", "신상 로얄 프리미엄 사료", R.drawable.sample_order_item1, 20000, "퍼피 500g", 2),
            Item("펫팔", "신상 로얄 프리미엄 캔", R.drawable.sample_order_item2, 15000, "참치맛 150g", 1)
        )
    }

}