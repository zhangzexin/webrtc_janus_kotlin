package zzx.webrtc.rtc.janus

import org.json.JSONObject

class JanusTransaction {
    var tid: String? = null
    var success: TransactionCallbackSuccess? = null
    var error: TransactionCallbackError? = null
}

interface TransactionCallbackSuccess {
    fun success(jo: JSONObject?)
}

interface TransactionCallbackError {
    fun error(jo: JSONObject?)
}
