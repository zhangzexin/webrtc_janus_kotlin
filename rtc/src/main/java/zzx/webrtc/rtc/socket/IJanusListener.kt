package zzx.webrtc.rtc.socket

import org.json.JSONObject

interface IJanusListener {

    fun onCreateSession(sessionId: String)
    fun onAttached(id: String?)
    fun onDispatch(sender: String, handleId: String, data: JSONObject?, jsep: JSONObject?)
    fun onDetached(handleId: String)
}