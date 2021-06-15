package zzx.webrtc.rtc.`interface`

import org.webrtc.SessionDescription

interface IAnswerListener {
    fun onAnswerSuccess(sdp: SessionDescription?)

    fun onAnswerFailure(error: String?)
}