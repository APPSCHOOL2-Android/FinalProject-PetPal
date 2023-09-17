package com.petpal.mungmate.ui.community

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import com.petpal.mungmate.R

class CommunityDeleteDialog(context: Context) {

    lateinit var listener: LessonDeleteDialogClickedListener
    lateinit var btnDelete: Button
    lateinit var btnCancel: Button

    interface LessonDeleteDialogClickedListener {
        fun onDeleteClicked()
    }

    private val dlg = Dialog(context)

    fun start() {

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dlg.setContentView(R.layout.dialog_community_post_delete)

        btnDelete = dlg.findViewById(R.id.checkButton)
        btnDelete.setOnClickListener {
            listener.onDeleteClicked()
            dlg.dismiss()
        }

        btnCancel = dlg.findViewById(R.id.cancelButton)
        btnCancel.setOnClickListener {
            dlg.dismiss()
        }
        dlg.show()
    }

}