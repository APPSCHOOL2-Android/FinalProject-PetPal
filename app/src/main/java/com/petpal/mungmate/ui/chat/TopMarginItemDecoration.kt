package com.petpal.mungmate.ui.chat

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TopMarginItemDecoration(private val marginInDp: Int): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val marginInPixels = (marginInDp * view.resources.displayMetrics.density).toInt()
        outRect.top = marginInPixels
    }
}