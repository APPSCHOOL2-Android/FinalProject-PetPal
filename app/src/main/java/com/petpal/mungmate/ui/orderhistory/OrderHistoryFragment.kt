package com.petpal.mungmate.ui.orderhistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.petpal.mungmate.R
import com.petpal.mungmate.databinding.FragmentOrderHistoryBinding
import com.petpal.mungmate.model.Item
import com.petpal.mungmate.model.Order

class OrderHistoryFragment : Fragment() {
    private var _fragmentOrderHistoryBinding: FragmentOrderHistoryBinding? = null
    private val fragmentOrderHistoryBinding get() = _fragmentOrderHistoryBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentOrderHistoryBinding = FragmentOrderHistoryBinding.inflate(inflater)
        
        // TODO skeleton 로딩 효과 주기
        
        return fragmentOrderHistoryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        fragmentOrderHistoryBinding.run { 
            recyclerViewOrderHistory.run { 
                adapter = OrderHistoryAdapter(getSampleData())
                layoutManager = LinearLayoutManager(context)
                // addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
        }
    }

    private fun getSampleData() : List<Order> {
        return listOf(
            Order("2023.09.12", 
                listOf(
                    Item("펫팔", "신상 로얄 프리미엄 사료", R.drawable.sample_order_item1, 20000, "퍼피 500g", 2),
                    Item("펫팔", "신상 로얄 프리미엄 캔", R.drawable.sample_order_item2, 15000, "참치맛 150g", 1)
                ), 
                "배송중"),
            Order("2023.09.11",
                listOf(
                    Item("펫팔", "신상 로얄 프리미엄 사료", R.drawable.sample_order_item1, 20000, "퍼피 500g", 1),
                ),
                "배송완료"),
        )
    }

}