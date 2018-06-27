package com.hazz.kotlinmvp.ui

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.hazz.kotlinmvp.R

/**
 * User: wanglg
 * Date: 2018-04-27
 * Time: 17:57
 * FIXME
 */
class UpgradeVersionDialog(var mContext: Context) {
    private var dialog: Dialog? = null
    private var upgradeContext: TextView? = null
    private var progressBar: ProgressBar? = null
    private var isForce: Boolean? = false
    fun build(): UpgradeVersionDialog {
        val view = LayoutInflater.from(mContext).inflate(R.layout.dialog_new_version, null)
        progressBar = view.findViewById(R.id.progressbar)
        dialog = Dialog(mContext, R.style.UpgradeDialogStyle)
        dialog?.setContentView(view)

        return this
    }


    fun setProgress(progress: Int) {
        progressBar?.setProgress(progress)
    }

    fun setCanceledOnTouchOutside(b: Boolean) {
        dialog?.setCanceledOnTouchOutside(b)
    }

    fun show() {
        dialog?.show()
    }

}