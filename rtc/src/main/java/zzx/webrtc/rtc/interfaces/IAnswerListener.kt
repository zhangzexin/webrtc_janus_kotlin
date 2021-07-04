package zzx.webrtc.rtc.interfaces

import org.webrtc.SessionDescription

interface IAnswerListener {
    fun onAnswerSuccess(sdp: SessionDescription?)

    fun onAnswerFailure(error: String?)
}