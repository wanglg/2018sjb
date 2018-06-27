package com.hazz.kotlinmvp.ui.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.content.FileProvider
import com.hazz.kotlinmvp.R
import com.hazz.kotlinmvp.base.BaseActivity
import com.hazz.kotlinmvp.ui.UpgradeVersionDialog
import com.hazz.kotlinmvp.utils.DownloadFileUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * User: wanglg
 * Date: 2018-06-27
 * Time: 19:24
 * FIXME
 */
class ForceActivity : BaseActivity() {
    private var url: String? = null
    val URL = "URL"
    private var currentPercentage = -1
    var progressDialog: UpgradeVersionDialog? = null
    override fun layoutId(): Int {
        return R.layout.activity_force
    }

    override fun initData() {
        url = intent.getStringExtra(URL)
        progressDialog = UpgradeVersionDialog(this)
        progressDialog?.build()
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.show()
        val util = DownloadFileUtil()
        util.downloadFile(url!!, getDiskFileDir(this) + File.separator + "apk", "install.apk", object : DownloadFileUtil.ProgressListener {
            override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
                val percentage = (bytesRead * 1.0f / contentLength * 100).toInt()
                if (currentPercentage == percentage) {//百分比没有变化
                    return
                } else {
                    currentPercentage = percentage
                }
                Observable.just(percentage).observeOn(AndroidSchedulers.mainThread()).subscribe({
                    progressDialog?.setProgress(it)
                })

                if (bytesRead == contentLength) {
                    val filePath = getDiskFileDir(this@ForceActivity) + File.separator + "apk" + "/" + "install.apk"
                    Observable.timer(1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                installApk(this@ForceActivity, filePath)
                            })
                }
            }

            override fun onErrorResponse() {
            }

        })
    }

    override fun initView() {
    }

    override fun start() {
    }

    private fun getDiskFileDir(context: Context): String {
        var cachePath: String? = null
        try {
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
                cachePath = context.getExternalFilesDir(null)!!.absolutePath
            } else {
                cachePath = context.filesDir.path
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cachePath == null) {
                cachePath = context.filesDir.path
            }
        }
        return cachePath!!
    }

    fun installApk(context: Context, apkPath: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        val apkFile = File(apkPath)
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val contentUri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", apkFile)
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.setDataAndType(Uri.parse("file://$apkPath"),
                    "application/vnd.android.package-archive")
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }

    }
}