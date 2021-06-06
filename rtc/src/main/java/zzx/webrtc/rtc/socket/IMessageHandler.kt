package zzx.webrtc.rtc.socket

import android.util.Log
import org.json.JSONObject
import zzx.webrtc.rtc.socket.JanusMsgType.*

interface IMessageHandler {
    val MsgTAG: String
        get() = "MessageHandler"


    fun onMessageHandler(msg: String) {
        Log.d(MsgTAG, "Janus onMessage: ")
        val jsonObject = JSONObject(msg)
        val janus = jsonObject.getString("janus")
        val type = JanusMsgType.valueOf(janus)
        when(type) {
            keepalive -> {
                Log.d(MsgTAG, "onMessageHandler: keepalive")
                onKeepAlive(jsonObject)
            }
            ack-> Log.d(MsgTAG, "onMessageHandler: ack")

            success-> {
                Log.d(MsgTAG, "onMessageHandler: success")
                //TODO 处理成功
            }
            error-> {
                Log.d(MsgTAG, "onMessageHandler: error")
                //TODO 处理失败
            }
            hangup-> Log.d(MsgTAG, "onMessageHandler: hangup")
            detached-> Log.d(MsgTAG, "onMessageHandler: detached")
            event-> Log.d(MsgTAG, "onMessageHandler: event")
            trickle-> Log.d(MsgTAG, "onMessageHandler: trickle")
            destroy-> Log.d(MsgTAG, "onMessageHandler: detroy")
        }
    }

    fun onKeepAlive(jsonObject: JSONObject): Any {
        TODO("Not yet implemented")
    }
}