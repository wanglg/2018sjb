package com.hazz.kotlinmvp.utils.okhttp;

/**
 * Created by wyz on 2018/4/15.
 */

public interface ProgressListener {
    //已完成的 总的文件长度 是否完成
    void onProgress(long currentBytes, long contentLength, boolean done);
}
