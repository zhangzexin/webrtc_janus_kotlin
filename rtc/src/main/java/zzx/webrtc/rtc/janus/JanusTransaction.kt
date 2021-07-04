package zzx.webrtc.rtc.janus

import org.json.JSONObject

class JanusTransaction {
    constructor(tid: String) {
        this.tid = tid
    }
    constructor(tid: String, feedId: String) {
        this.tid = tid
        this.feedId = feedId
    }
    var tid: String? = null
    var feedId: String? = null
    var success: TransactionCallbackSuccess? = null
    var joined: OnJoined? = null
    var error: TransactionCallbackError? = null
}

interface TransactionCallbackSuccess {
    fun success(jo: JSONObject?)
}

interface OnJoined {
    fun onJoined(jo: JSONObject, feedId :String)
}

interface TransactionCallbackError {
    fun error(jo: JSONObject?)
}
