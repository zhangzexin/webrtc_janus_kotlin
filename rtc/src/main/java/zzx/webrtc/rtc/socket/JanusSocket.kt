package zzx.webrtc.rtc.socket

import org.json.JSONObject
import zzx.webrtc.rtc.janus.JanusTransaction
import zzx.webrtc.rtc.janus.TransactionCallbackError
import zzx.webrtc.rtc.janus.TransactionCallbackSuccess
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class JanusSocket: WebSocketChannel() {

    private val mTransactions = ConcurrentHashMap<String, JanusTransaction>()

    fun connect(url: String) {
        initConnection(url)
    }


    override fun createSession() {
        val transaction = randomString(12)
        val janusTransaction = JanusTransaction()
        janusTransaction.tid = transaction
        janusTransaction.success = object : TransactionCallbackSuccess {
            override fun success(jsonObject: JSONObject?) {
                val data = jsonObject?.getJSONObject("data")
                val sessionId = data?.getString("id")
                //创建成功，开始心跳保活
            }
        }
        janusTransaction.error = object : TransactionCallbackError {
            override fun error(jo: JSONObject?) {
                //事物处理error
            }
        }
        mTransactions.put(transaction, janusTransaction)
        val jsonObject = JSONObject()
        //创建事物生成session
        jsonObject.putOpt("janus","create")
        jsonObject.putOpt("transaction", transaction)
        sendMessage(jsonObject.toString());
    }

    override fun onDestroy(jsonObject: JSONObject) {
        TODO("Not yet implemented")
    }

    override fun onTrickle(jsonObject: JSONObject) {
        TODO("Not yet implemented")
    }

    override fun onDetached(jsonObject: JSONObject) {
        TODO("Not yet implemented")
    }

    override fun onHangUp(jsonObject: JSONObject) {
        TODO("Not yet implemented")
    }

    override fun onEvent(jsonObject: JSONObject) {
        TODO("Not yet implemented")
    }

    override fun onError(jsonObject: JSONObject) {
        TODO("Not yet implemented")
    }

    override fun onSuccess(jsonObject: JSONObject) {
        TODO("Not yet implemented")
    }

    override fun onKeepAlive(jsonObject: JSONObject): Any {
        TODO("Not yet implemented")
    }

    private fun randomString(length: Int): String {
        val str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val rnd = Random()
        val sb = StringBuilder(length)
        for (i in 0 until length) {
            sb.append(str[rnd.nextInt(str.length)])
        }
        return sb.toString()
    }
}