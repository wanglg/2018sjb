package com.hazz.kotlinmvp.ui.activity

import android.content.Intent
import com.hazz.kotlinmvp.base.BaseActivity
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
//        if (SharedPreferencesUtils.instance.getValue(GlobalConstants.IS_FIRST_LAUNCH, GlobalConstants.BOOLEAN_TRUE) as Boolean) {
//            Observable.timer(2000, TimeUnit.MILLISECONDS).compose(bindToLifecycle())
//                    .subscribe({
//                        if (!isFinishing) {
//                            ActivityUtil.switchTo(this, GuideActivity::class.java)
//                            SharedPreferencesUtils.instance.put(GlobalConstants.IS_FIRST_LAUNCH, false)
//                            finish()
//                        }
//                    })
//
//        } else {
        Observable.timer(2000, TimeUnit.MILLISECONDS)
                .subscribe({
                    if (!isFinishing) {
                        redirectTo()
                    }
                })
    }

    //    }
    fun redirectTo() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
        finish()
    }

}
