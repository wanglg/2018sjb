package com.hazz.kotlinmvp.ui.activity

import android.content.Intent
import com.hazz.kotlinmvp.base.BaseActivity
import com.hazz.kotlinmvp.net.RetrofitManager
import com.hazz.kotlinmvp.rx.scheduler.SchedulerUtils
import io.reactivex.Observable
import java.util.concurrent.TimeUnit


/**
 * 启动页 闪屏页
 * Created by caowt on 2018/1/4 0004.
 */
class LaunchActivity : BaseActivity() {
    override fun initView() {
    }

    override fun start() {
    }

    override fun layoutId(): Int {
        return 0
    }


    override fun setContentView(layoutResID: Int) {
    }

    override fun initData() {
        startAPP()
    }


    private fun startAPP() {
        RetrofitManager.noHeadservice.getUpdateInfo().compose(SchedulerUtils.ioToMain())
                .subscribe({
                    if (it.status == 1) {
                        if (it.url.endsWith(".apk")) {
                            val intent = Intent(this, ForceActivity::class.java)
                            intent.putExtra("URL", it.url)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this, WebShowActivity::class.java)
                            intent.putExtra("url", it.url)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Observable.timer(1000, TimeUnit.MILLISECONDS)
                                .subscribe({
                                    if (!isFinishing) {
                                        redirectTo()
                                    }
                                })
                    }

                }, {
                    Observable.timer(1000, TimeUnit.MILLISECONDS)
                            .subscribe({
                                if (!isFinishing) {
                                    redirectTo()
                                }
                            })
                })

    }

    //    }
    fun redirectTo() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
        finish()
    }

}
