package com.hazz.kotlinmvp.utils

import android.util.Log
import okhttp3.*
import okio.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * User: wanglg
 * Date: 2018-04-27
 * Time: 11:56
 * FIXME
 */
class DownloadFileUtil {
    fun downloadFile(path: String, destDir: String, destFileName: String, progressListener: ProgressListener) {
        val request = Request.Builder()
                .url(path)
                .build()
        val client = OkHttpClient.Builder()
                .addNetworkInterceptor { chain ->
                    val originalResponse = chain.proceed(chain.request())
                    originalResponse.newBuilder()
                            .body(ProgressResponseBody(originalResponse.body()!!, progressListener))
                            .build()
                }
                .build()


        try {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    progressListener.onErrorResponse()
                    Log.e("DownloadFileUtil", "Unexpected code " + e.localizedMessage)
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    try {
                        saveFile(response, destDir, destFileName)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        progressListener.onErrorResponse()
                    }

                }
            })
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            progressListener.onErrorResponse()
        }

    }

    @Throws(IOException::class)
    private fun saveFile(response: Response, destDir: String, destFileName: String): String {
        var inputStream: InputStream? = null
        val buf = ByteArray(2048)
        var len = 0
        var fos: FileOutputStream? = null
        try {
            inputStream = response.body()!!.byteStream()

            val dir = File(destDir)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(destDir, destFileName)
            if (file.exists()) {
                file.delete()
            }
            len = inputStream.read(buf)
            fos = FileOutputStream(file)
            while (len != -1) {
                fos.write(buf, 0, len)
                len = inputStream.read(buf)
            }
            fos.flush()

            return file.absolutePath

        } finally {
            try {
                if (inputStream != null) inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                if (fos != null) fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private class ProgressResponseBody(private val responseBody: ResponseBody, private val progressListener: ProgressListener) : ResponseBody() {
        private var bufferedSource: BufferedSource? = null

        override fun contentType(): MediaType? {
            return responseBody.contentType()
        }

        override fun contentLength(): Long {
            return responseBody.contentLength()
        }

        override fun source(): BufferedSource {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()))
            }
            return bufferedSource!!
        }

        private fun source(source: Source): Source {
            return object : ForwardingSource(source) {
                internal var totalBytesRead = 0L

                @Throws(IOException::class)
                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1L)
                    return bytesRead
                }
            }
        }
    }

    interface ProgressListener {
        fun update(bytesRead: Long, contentLength: Long, done: Boolean)

        fun onErrorResponse()
    }
}