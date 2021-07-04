package zzx.webrtc.rtc.socket

import android.os.Handler
import android.text.TextUtils
import org.json.JSONObject
import zzx.webrtc.rtc.janus.JanusTransaction
import zzx.webrtc.rtc.janus.TransactionCallbackError
import zzx.webrtc.rtc.janus.TransactionCallbackSuccess
import zzx.webrtc.rtc.utils.IdBuildHelper
import java.util.concurrent.ConcurrentHashMap

class JanusSocket : WebSocketChannel, JanusReceiptMessag {

    private var isKeepAliveRunning: Boolean = false
    var mSessionId: String = ""
    var mJanusListener: IJanusListener? = null
    private val mTransactions = ConcurrentHashMap<String, JanusTransaction>()
    private val mAttachedPlugins = ConcurrentHashMap<String, String>()
    var mHandler: Handler? = null

    constructor() {
        mHandler = Handler()
    }


    private val fireKeepAlive: Runnable = object : Runnable {
        override fun run() {
            if (isConnection && !TextUtils.isEmpty(mSessionId)) {
                sendKeepAliveMsg(mSessionId)
            }
            mHandler?.postDelayed(this, 25 * 1000)
        }
    }

    fun connect(url: String) {
        initConnection(url)
    }


    override fun createSession() {
        val transaction = IdBuildHelper.randomString(12)
        val janusTransaction = JanusTransaction(transaction)
        janusTransaction.success = object : TransactionCallbackSuccess {
            override fun success(jsonObject: JSONObject?) {
                jsonObject?.let {
                    val data = it.getJSONObject("data")
                    data?.let {
                        mSessionId = it?.getString("id")
                        mJanusListener?.onCreateSession(mSessionId)
                    }
                    //创建成功，开始心跳保活
                    startKeepAliveTimer()
                    // 创建Session成功，添加用户
                    attachPlugin("test 用户的插件名称")
                }

            }
        }
        janusTransaction.error = object : TransactionCallbackError {
            override fun error(jo: JSONObject?) {
                //事物处理error
            }
        }
        mTransactions.put(transaction, janusTransaction)
        // 发送Janus创建消息
        sendCreateMsg(transaction)
    }

    override fun onDestroy(jsonObject: JSONObject) {
        TODO("销毁sessionId")
    }

    override fun onTrickle(jsonObject: JSONObject) {
        var handleId: String? = null
        if (jsonObject.has("sender")) {
            val sender = jsonObject.getString("sender")
            handleId = mAttachedPlugins.get(sender)
        }
        handleId?.let {
            val candidate = jsonObject.getString("candidate")

        }
    }

    override fun onDetached(jsonObject: JSONObject) {
        if (jsonObject.has("sender")) {
            val sender = jsonObject.getString("sender")
            val handleId = mAttachedPlugins.get(sender)
            handleId?.let { mJanusListener?.onDetached(it) }
        }
    }

    override fun onHangUp(jsonObject: JSONObject) {
        //TODO 挂断
    }

    override fun onEvent(jsonObject: JSONObject) {
        if (jsonObject.has("sender")) {
            val sender = jsonObject.getString("sender")
            val handleId = mAttachedPlugins.get(sender)
            if (handleId != null) {
                var data: JSONObject? = null
                var jsep: JSONObject? = null
                jsonObject.getJSONObject("plugindata")?.let {
                    data = it.getJSONObject("data")
                    jsep = it.getJSONObject("jsep")
                }
                mJanusListener?.onDispatch(sender, handleId, data, jsep)
            }
        }
    }

    override fun onError(jsonObject: JSONObject) {
        val transaction = jsonObject.getString("transaction")
        transaction?.let {
            mTransactions.get(it)?.let {
                it.error?.error(jsonObject)
                mTransactions.remove(transaction)
            }
        }
    }

    override fun onSuccess(jsonObject: JSONObject) {
        if (!jsonObject.has("transaction")) {
            return
        }
        val transactionId = jsonObject.getString("transaction")
        if (TextUtils.isEmpty(transactionId)) {
            return
        }
        val transaction = mTransactions.get(transactionId)
        transaction?.run {
            if (!TextUtils.isEmpty(this.feedId)) {
                // 处理成功消息
                feedId?.let { joined?.onJoined(jsonObject, it) }
            } else {
                this.success?.success(jsonObject)
            }
        }
    }

    override fun onKeepAlive(jsonObject: JSONObject) {
        //收到保活消息，可以添加验证信息
    }

    private fun startKeepAliveTimer() {
        isKeepAliveRunning = true
        mHandler?.post(fireKeepAlive)
    }

    override fun sendMessage(msg: String) {
        super<WebSocketChannel>.sendMessage(msg)
    }


    fun setJanusListener(listener: IJanusListener) {
        mJanusListener = listener
    }

    fun attachPlugin(pluginname: String) {
        val tid = IdBuildHelper.randomString(12)
        val janusTransaction = JanusTransaction(tid)
        janusTransaction.success = object : TransactionCallbackSuccess {
            override fun success(msg: JSONObject?) {
                val data = msg?.getJSONObject("data")
                data?.getString("id")?.let {
                    mJanusListener?.onAttached(it)
                    mAttachedPlugins.put(it, it)
                }
            }
        }
        sendAttachMsg(tid, pluginname, mSessionId)
    }

}