package zzx.webrtc.rtc.janus

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.text.TextUtils
import org.json.JSONObject
import org.webrtc.*
import zzx.webrtc.rtc.interfaces.ILocalDescription
import zzx.webrtc.rtc.socket.IJanusListener
import zzx.webrtc.rtc.socket.JanusSocket
import zzx.webrtc.rtc.socket.PeerHandle
import zzx.webrtc.rtc.utils.CameraCapturerUtlis

//处理Janus长链中的逻辑
class JanusClient: PeerHandle {
    var mJanusSocke: JanusSocket? = null
    var mEglBaseContext: EglBase.Context = EglBase.create().eglBaseContext
    var mPeerConnection: PeerConnection? = null
    var mPeerConnectionFactory: PeerConnectionFactory?= null
    var mRoomConfig = RoomConfig()


    constructor() {
        mJanusSocke = JanusSocket()
    }

    fun init(context: Context) {
        mJanusSocke?.setJanusListener(object : IJanusListener {
            override fun onCreateSession(sessionId: String) {
                mRoomConfig.sessionId = sessionId
            }

            override fun onAttached(id: String?) {
                mRoomConfig.roomHandleId = id
                if (!TextUtils.isEmpty(mRoomConfig.roomHandleId) && !TextUtils.isEmpty(id)) {
                    mJanusSocke?.sendJoin(
                        "房间名称", "房间ID", "房间密码",
                        mRoomConfig.sessionId!!, id!!
                    )
                }
            }

            override fun onDispatch(
                sender: String,
                handleId: String,
                data: JSONObject?,
                jsep: JSONObject?
            ) {
              if (data?.getString("configured")?.equals("ok") == true && jsep != null) {
                  jsep.getString("sdp")?.let {
                      createLocalSdp(mPeerConnection!!,it,object: ILocalDescription{
                          override fun onAddStream() {
                              //TODO 添加本地视频UI
                          }

                          override fun onFailuer(error: String) {
                              //TODO 本地连接sdp失败。
                          }

                      })
                  }
              } else if (data?.has("unpublished") == true) {
                  //取消发布
              } else if (data?.has("leaving") == true) {
                  //离开消息
              }

            }

            override fun onDetached(handleId: String) {
                mRoomConfig.roomHandleId = null
            }

        })
        mPeerConnectionFactory = createPeerConnectionFactory(context, mEglBaseContext)
        mPeerConnectionFactory?.let {
            mPeerConnection =
                createPeerConnection(it, object : PeerConnection.Observer {
                    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
                    }

                    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
                    }

                    override fun onIceConnectionReceivingChange(p0: Boolean) {
                    }

                    override fun onIceGatheringChange(iceGatheringState: PeerConnection.IceGatheringState?) {
                        if (iceGatheringState == PeerConnection.IceGatheringState.COMPLETE) {
                            //TODO 添加RoomHandlerID
//                   mJanusSocke?.sendTrickleCandidateComplete()
                        }
                    }

                    override fun onIceCandidate(iceCandidate: IceCandidate?) {
                        //TODO 发送trickleCandidate消息
//                mJanusSocke?.sendTrickleCandidate(,iceCandidate,mSession)
                    }

                    override fun onIceCandidatesRemoved(iceCandidates: Array<out IceCandidate>?) {
                        mPeerConnection?.removeIceCandidates(iceCandidates)
                    }

                    override fun onAddStream(p0: MediaStream?) {
                    }

                    override fun onRemoveStream(p0: MediaStream?) {
                    }

                    override fun onDataChannel(p0: DataChannel?) {
                    }

                    override fun onRenegotiationNeeded() {
                    }

                    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
                    }

                })
        }

        createLocalCapture(context)
        createLocalAudio()
    }

    private fun createLocalAudio() {
        val audioConstraints = MediaConstraints()
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googEchoCancellation","true"))
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl","true"))
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googHighpassFilter","true"))
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googNoiseSuppression","true"))
        val audioSource = mPeerConnectionFactory?.createAudioSource(audioConstraints)
        val audioTrack = mPeerConnectionFactory?.createAudioTrack("101", audioSource)
        audioTrack?.setEnabled(true)
    }

    private fun createLocalCapture(context: Context) {
        val videoCapturer = CameraCapturerUtlis.createVideoCapturer(context)
        videoCapturer?.let {
            mRoomConfig.videoCapturer = it
            val surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", mEglBaseContext)
            val videoSource = mPeerConnectionFactory?.createVideoSource(it.isScreencast)
            it.initialize(surfaceTextureHelper,context,videoSource?.capturerObserver)
            it.startCapture(1280,720, 30)
            mRoomConfig.videoSource = videoSource
            mRoomConfig.surfaceTextureHelper = surfaceTextureHelper
            val videoTrack = mPeerConnectionFactory?.createVideoTrack("100", videoSource)
            videoTrack?.setEnabled(true)
            val mediaStream =
                mPeerConnectionFactory?.createLocalMediaStream("mediaStream")
            mediaStream?.addTrack(videoTrack)
            mPeerConnection?.addTrack(videoTrack)
            mPeerConnection?.addStream(mediaStream)
        }
    }

    fun onResultScreenCapture(requestCode: Int, resultCode: Int, data: Intent, surfaceViewRenderer: SurfaceViewRenderer) {
        if (resultCode != RESULT_OK) {
            return
        }
        mRoomConfig.videoCapturer?.run {
            stopCapture()
            dispose()
        }
        mRoomConfig.videoCapturer = ScreenCapturerAndroid(data, object : MediaProjection.Callback() {
            override fun onStop() {
                super.onStop()
            }
        })
        mRoomConfig.videoCapturer?.let {
            it.initialize(mRoomConfig.surfaceTextureHelper,surfaceViewRenderer.context,mRoomConfig.videoSource?.capturerObserver)
            it.startCapture(1280,720, 30)
            surfaceViewRenderer.invalidate()
        }
    }

    fun connect(url: String) {
        mJanusSocke?.connect(url)
    }
}
