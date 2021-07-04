package zzx.webrtc.rtc.socket

import android.text.TextUtils
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import zzx.webrtc.rtc.utils.IdBuildHelper

interface JanusReceiptMessag {

    fun sendCreateMsg(tid: String) {
        val jsonObject = JSONObject()
        jsonObject.put("janus", "create")
        jsonObject.put("transaction", tid)
        sendMessage(jsonObject.toString())
    }

    fun sendAttachMsg(tid: String, pluginname: String, sessionId: String) {
        val jsonObject = JSONObject()
        jsonObject.put("janus", "attach")
        jsonObject.put("transaction", tid)
        jsonObject.put("plugin", pluginname)
        jsonObject.put("session_id", sessionId)
        sendMessage(jsonObject.toString())
    }

    fun sendKeepAliveMsg(mSessionId: String) {
        val jsonObject = JSONObject()
        jsonObject.put("janus", "keepalive")
        jsonObject.put("session_id", mSessionId)
        jsonObject.put("transaction", mSessionId)
        sendMessage(jsonObject.toString())
    }

    fun sendDestroySession(tid: String, sessionId: String) {
        val jsonObject = JSONObject()
        jsonObject.put("janus", "destroy")
        jsonObject.put("transaction", tid)
        jsonObject.put("session_id", sessionId)
        sendMessage(jsonObject.toString())
    }

    fun sendCreateOffer(handleId: String, sdp: SessionDescription, sessionId: String) {
        val message = JSONObject()
        val publish = JSONObject()
        publish.putOpt("audio", true)
        publish.putOpt("video", true)
        val jsep = JSONObject()
        jsep.putOpt("type", sdp.type)
        jsep.putOpt("sdp", sdp.description)
        message.putOpt("janus", "message")
        message.putOpt("body", publish)
        message.putOpt("jsep", jsep)
        message.putOpt("transaction", IdBuildHelper.randomString(12))
        message.putOpt("session_id", sessionId)
        message.putOpt("handle_id", handleId)

        sendMessage(message.toString())
    }

    fun sendTrickleCandidateComplete(handleId: String, sessionId: String) {
        val candidate = JSONObject()
        val message = JSONObject()
        candidate.putOpt("completed", true)
        message.putOpt("janus", "trickle")
        message.putOpt("candidate", candidate)
        message.putOpt("transaction", IdBuildHelper.randomString(12))
        message.putOpt("session_id", sessionId)
        message.putOpt("handle_id", handleId)
        sendMessage(message.toString())
    }

    fun sendTrickleCandidate(handleId: String, iceCandidate: IceCandidate, sessionId: String) {
        val candidate = JSONObject()
        val message = JSONObject()
        candidate.putOpt("candidate", iceCandidate.sdp)
        candidate.putOpt("sdpMid", iceCandidate.sdpMid)
        candidate.putOpt("sdpMLineIndex", iceCandidate.sdpMLineIndex)

        message.putOpt("janus", "trickle")
        message.putOpt("candidate", candidate)
        message.putOpt("transaction", IdBuildHelper.randomString(12))
        message.putOpt("session_id", sessionId)
        message.putOpt("handle_id", handleId)
        sendMessage(message.toString())
    }

    fun sendJoin(displayName: String,roomId: String, password: String, sessionId: String, handleId: String) {
        val body = JSONObject()
        val message = JSONObject()
        body.putOpt("display", displayName)
        body.putOpt("ptype", "publisher")
        body.putOpt("request","join")
        body.putOpt("room", roomId)
        if (TextUtils.isEmpty(password)) {
            body.putOpt("pin",password)
        }
        message.putOpt("body",body)
        message.putOpt("janus", "message")
        message.putOpt("transaction", IdBuildHelper.randomString(12))
        message.putOpt("session_id",sessionId)
        message.putOpt("handle_id" , handleId)
        sendMessage(message.toString())
    }

    fun sendMessage(toString: String)
}