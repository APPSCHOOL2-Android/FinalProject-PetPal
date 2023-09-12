package com.petpal.mungmate.ui.orderhistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
            recyclerViewOrderHistory.apply {
                adapter = OrderHistoryAdapter(getSampleData())
                layoutManager = LinearLayoutManager(context)
                // addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

                val fadeIn = AlphaAnimation(0f, 1f).apply { duration = 500 }
                val fadeOut = AlphaAnimation(1f, 0f).apply { duration = 500 }
                var isTop = true

                addOnScrollListener(object: RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        // 최상단에 있는 순간 -> FAB 숨기기
                        if (!canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                            fabOrderHistoryTop.startAnimation(fadeOut)
                            fabOrderHistoryTop.visibility = View.GONE
                            isTop = true
                        } else {
                            // 최상단에서 내리기 시작하는 순간 -> FAB 보이기
                            if (isTop) {
                                fabOrderHistoryTop.visibility = View.VISIBLE
                                fabOrderHistoryTop.startAnimation(fadeIn)
                                isTop = false
                            }
                        }
                    }
                })
            }

            fabOrderHistoryTop.setOnClickListener {
                // 최상단 이동
                recyclerViewOrderHistory.smoothScrollToPosition(0)
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
            Order("2023.09.11", listOf(Item("펫팔", "신상 로얄 프리미엄 사료", R.drawable.sample_order_item1, 20000, "퍼피 500g", 1),), "배송완료"),
            Order("2023.09.10", listOf(Item("펫팔", "신상 로얄 프리미엄 사료", R.drawable.sample_order_item1, 20000, "퍼피 500g", 1),), "배송완료"),
            Order("2023.09.09", listOf(Item("펫팔", "신상 로얄 프리미엄 사료신상 로얄 프리미엄 사료신상 로얄 프리미엄 사료신상 로얄 프리미엄 사료신상 로얄 프리미엄 사료신상 로얄 프리미엄 사료신상 로얄 프리미엄 사료신상 로얄 프리미엄 사료신상 로얄 프리미엄 사료신상 로얄 프리미엄 사료", R.drawable.sample_order_item1, 20000, "퍼피 500g", 1),), "배송완료"),
            Order("2023.09.09", listOf(Item("펫팔", "신상 로얄 프리미엄 사료", R.drawable.sample_order_item1, 20000, "퍼피 500g", 1),), "배송완료"),
            Order("2023.09.09", listOf(Item("펫팔", "신상 로얄 프리미엄 사료", R.drawable.sample_order_item1, 20000, "퍼피 500g", 1),), "배송완료")
        )
    }

}