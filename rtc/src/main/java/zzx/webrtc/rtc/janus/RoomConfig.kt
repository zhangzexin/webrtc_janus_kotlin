package zzx.webrtc.rtc.janus

import org.webrtc.SurfaceTextureHelper
import org.webrtc.VideoCapturer
import org.webrtc.VideoSource

class RoomConfig {

    var roomHandleId: String? = null
    var sessionId: String? = null
    var surfaceTextureHelper: SurfaceTextureHelper? = null
    var videoSource: VideoSource? = null
    var videoCapturer: VideoCapturer? = null
}