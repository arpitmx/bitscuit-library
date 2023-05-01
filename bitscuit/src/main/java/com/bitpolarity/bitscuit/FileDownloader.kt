package com.bitpolarity.bitscuit

import android.util.Log
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

class FileDownloader(okHttpClient: OkHttpClient) {

    companion object {
        private const val BUFFER_LENGTH_BYTES = 1024 * 64
        private const val HTTP_TIMEOUT = 30L
    }

    private var okHttpClient: OkHttpClient

    init {
        val okHttpBuilder = okHttpClient.newBuilder()
            .connectTimeout(HTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(HTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)
        this.okHttpClient = okHttpBuilder.build()
    }

    fun download(url: String, file: File): Observable<Int> {
        return Observable.create { emitter ->
            val request = Request.Builder().url(url).build()
            val response = okHttpClient.newCall(request).execute()
            val body = response.body
            when {
                response.code !in HttpURLConnection.HTTP_OK until HttpURLConnection.HTTP_MULT_CHOICE -> {
                    throw IllegalArgumentException("Error occurred when do http get $url")
                }
                body == null -> {
                    throw IllegalArgumentException("Response body is null")
                }
                else -> {
                    val length = body.contentLength()
                    body.byteStream().use { inputStream ->
                        file.outputStream().use { outputStream ->
                            val buffer = ByteArray(BUFFER_LENGTH_BYTES)
                            var bytesRead = inputStream.read(buffer)
                            var bytesCopied = 0L
                            while (bytesRead >= 0) {
                                outputStream.write(buffer, 0, bytesRead)
                                bytesCopied += bytesRead
                                if (length > 0) {
                                    val progress = ((bytesCopied * 100) / length).toInt()
                                    emitter.onNext(progress)
                                    Log.d("FileDownloader", "Progress: $progress")
                                }
                                bytesRead = inputStream.read(buffer)
                            }
                        }
                    }
                    emitter.onComplete()
                }
            }
        }
    }

}
