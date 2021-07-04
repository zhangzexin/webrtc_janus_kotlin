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
                onSuccess(jsonObject)
            }
            error-> {
                Log.d(MsgTAG, "onMessageHandler: error")
                //TODO 处理失败
                onError(jsonObject)
            }
            hangup-> {
                Log.d(MsgTAG, "onMessageHandler: hangup")
                onHangUp(jsonObject)
            }
            detached-> {
                Log.d(MsgTAG, "onMessageHandler: detached")
                onDetached(jsonObject)
            }
            event-> {
                Log.d(MsgTAG, "onMessageHandler: event")
                onEvent(jsonObject)
            }
            trickle-> {
                Log.d(MsgTAG, "onMessageHandler: trickle")
                onTrickle(jsonObject)
            }
            destroy-> {
                Log.d(MsgTAG, "onMessageHandler: detroy")
                onDestroy(jsonObject)
            }
        }
    }

    fun onDestroy(jsonObject: JSONObject)

    fun onTrickle(jsonObject: JSONObject)

    fun onDetached(jsonObject: JSONObject)

    fun onHangUp(jsonObject: JSONObject)

    fun onEvent(jsonObject: JSONObject)

    fun onError(jsonObject: JSONObject)

    fun onSuccess(jsonObject: JSONObject)

    fun onKeepAlive(jsonObject: JSONObject)
}