package zzx.webrtc.rtc.socket

import android.os.Handler
import android.util.Log
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString
import java.util.concurrent.TimeUnit

abstract class WebSocketChannel: IMessageHandler {
    val TAG: String = "WebSocket"
    var mHandler: Handler
    var mWebSocket: WebSocket? = null

    constructor() {
        mHandler = Handler()
    }

    fun initConnection(url: String) {
        val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val builder: Request.Builder = chain.request().newBuilder()
                    //websocket 子协议只是添加一个Sec-WebSocket-Protocol的http请求头，用于告诉服务器我们使用的是janus-protocol协议通信
                    builder.addHeader("Sec-WebSocket-Protocol", "janus-protocol")
                    return chain.proceed(builder.build())
                }
            }).callTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder().url(url).build()
        mWebSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.e(TAG, "onOpen")
                createSession()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.e(TAG, "onMessage")
                onMessageHandler(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {

            }

            override fun onClosing(
                webSocket: WebSocket,
                code: Int,
                reason: String
            ) {
                Log.e(TAG, "onClosing")
            }

            override fun onClosed(
                webSocket: WebSocket,
                code: Int,
                reason: String
            ) {
            }

            override fun onFailure(
                webSocket: WebSocket,
                t: Throwable,
                response: Response?
            ) {
                Log.e(TAG, "onFailure$t")
            }
        })
    }

    abstract fun createSession();

}