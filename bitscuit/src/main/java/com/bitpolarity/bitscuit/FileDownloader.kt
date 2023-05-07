package com.bitpolarity.bitscuit

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.util.Log
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

class FileDownloader(
    private var okHttpClient: OkHttpClient,
    private val context: Context
) {

    companion object {
        private const val BUFFER_LENGTH_BYTES = 1024 * 8
        private const val HTTP_TIMEOUT = 30L
    }

    var isPaused = false


    init {
        val okHttpBuilder = okHttpClient.newBuilder()
            .connectTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
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
                                if (!isPaused) {
                                    outputStream.write(buffer, 0, bytesRead)
                                    bytesCopied += bytesRead
                                    if (length > 0) {
                                        val progress = ((bytesCopied * 100) / length).toInt()
                                        emitter.onNext(progress)
                                        Log.d("FileDownloader", "Progress: $progress")
                                    }
                                } else {
                                    emitter.onNext(-1) // indicate paused state
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






