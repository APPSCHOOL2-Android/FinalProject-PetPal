package com.petpal.mungmate.ui.orderhistory

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
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
            toolbarOrderDetail.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            recyclerViewOrderDetailItem.run {
                adapter = OrderHistoryItemAdapter(getSampleData())
                layoutManager = LinearLayoutManager(context)
                // addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }

            buttonExchange.setOnClickListener {
                Snackbar.make(fragmentOrderHistoryDetailBinding.root, "교환 신청이 완료됐습니다", Snackbar.LENGTH_SHORT).show()
            }

            buttonRefund.setOnClickListener {
                Snackbar.make(fragmentOrderHistoryDetailBinding.root, "반품 신청이 완료됐습니다", Snackbar.LENGTH_SHORT).show()
            }

            buttonDeliveryTracking.setOnClickListener {
                // TODO 택배사 배송 조회 화면
            }

            buttonCancelOrder.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("주문 취소")
                    .setMessage("한 번 취소한 주문은 다시 되돌릴 수 없습니다.")
                    .setPositiveButton("확인"){ dialogInterface: DialogInterface, i: Int ->
                        Snackbar.make(fragmentOrderHistoryDetailBinding.root, "주문이 취소됐습니다", Snackbar.LENGTH_SHORT).show()
                        buttonRefund.isEnabled = false
                        buttonExchange.isEnabled = false
                        buttonDeliveryTracking.isEnabled = false
                        buttonCancelOrder.isEnabled = false
                    }
                    .setNegativeButton("취소", null)
                    .show()
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