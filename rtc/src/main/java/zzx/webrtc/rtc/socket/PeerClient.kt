package zzx.webrtc.rtc.socket

import android.content.Context
import org.webrtc.*
import zzx.webrtc.rtc.`interface`.IAnswerListener

class PeerClient {


    fun createOffer(peerconnection: PeerConnection) {
        val mediaConstraints = MediaConstraints()
        peerconnection.createOffer(object : SdpObserver {
            override fun onCreateSuccess(sessionDescription: SessionDescription?) {
                createOfferSuccess(peerconnection, sessionDescription)
            }

            override fun onSetSuccess() {
                TODO("Not yet implemented")
            }

            override fun onCreateFailure(p0: String?) {
                TODO("Not yet implemented")
            }

            override fun onSetFailure(p0: String?) {
                TODO("Not yet implemented")
            }
        }, mediaConstraints)

    }

    private fun createOfferSuccess(
        peerconnection: PeerConnection,
        sessionDescription: SessionDescription?
    ) {
        peerconnection.setLocalDescription(object : SdpObserver {
            override fun onCreateSuccess(p0: SessionDescription?) {
                TODO("Not yet implemented")
            }

            override fun onSetSuccess() {
                TODO("Not yet implemented")
            }

            override fun onCreateFailure(p0: String?) {
                TODO("Not yet implemented")
            }

            override fun onSetFailure(p0: String?) {
                TODO("Not yet implemented")
            }
        }, sessionDescription)
    }

    fun createPeerConnectionFactory(
        context: Context,
        eglBasecontext: EglBase.Context
    ): PeerConnectionFactory {
        if (context == null) {
            throw NullPointerException("cantext connot be null")
        }
        if (eglBasecontext == null) {
            throw NullPointerException("eglBaseContext cannot be null")
        }
        val defaultVideoDecoderFactory = DefaultVideoDecoderFactory(eglBasecontext)
        val defaultVideoEncoderFactory = DefaultVideoEncoderFactory(eglBasecontext, false, true)
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context.applicationContext)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        )
        val peerConnectionFactoryBuilder =
            PeerConnectionFactory.builder().setVideoDecoderFactory(defaultVideoDecoderFactory)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)
                .setOptions(null)
        return peerConnectionFactoryBuilder.createPeerConnectionFactory();
    }

    fun createPeerConnection(
        peerConnectionFactory: PeerConnectionFactory,
        listener: PeerConnection.Observer
    ): PeerConnection? {
        val iceCandidateList = ArrayList<PeerConnection.IceServer>()
        iceCandidateList.add(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        )
        return peerConnectionFactory.createPeerConnection(
            PeerConnection.RTCConfiguration(
                iceCandidateList
            ), listener
        )
    }

    fun attachedPeerConnection(
        peerConnectionFactory: PeerConnectionFactory,
        listener: PeerConnection.Observer, sdp: String
    ): PeerConnection? {
        val createPeerConnection = createPeerConnection(peerConnectionFactory, listener)
        createPeerConnection?.setRemoteDescription(object : SdpObserver {
            override fun onCreateSuccess(p0: SessionDescription?) {
                TODO("Not yet implemented")
            }

            override fun onSetSuccess() {
                createAnswer(createPeerConnection, object : IAnswerListener {
                    override fun onAnswerSuccess(sdp: SessionDescription?) {
                        TODO("Not yet implemented")
                    }

                    override fun onAnswerFailure(error: String?) {
                        TODO("Not yet implemented")
                    }

                })
            }

            override fun onCreateFailure(p0: String?) {
                TODO("Not yet implemented")
            }

            override fun onSetFailure(p0: String?) {
                TODO("Not yet implemented")
            }
        }, SessionDescription(SessionDescription.Type.OFFER, sdp))

        return createPeerConnection
    }

    private fun createAnswer(
        peerconnection: PeerConnection,
        iAnswerListener: IAnswerListener
    ) {
        val mediaConstraints = MediaConstraints()
        peerconnection.createAnswer(object :SdpObserver{
            override fun onCreateSuccess(sdp: SessionDescription?) {
                //建链本地视频
                peerconnection.setLocalDescription(object :SdpObserver{
                    override fun onCreateSuccess(p0: SessionDescription?) {
                        TODO("Not yet implemented")
                    }

                    override fun onSetSuccess() {
                        iAnswerListener.onAnswerSuccess(sdp)
                    }

                    override fun onCreateFailure(error: String?) {
                        iAnswerListener.onAnswerFailure(error)
                    }

                    override fun onSetFailure(error: String?) {
                        iAnswerListener.onAnswerFailure(error)
                    }
                },sdp)

            }

            override fun onSetSuccess() {
                TODO("Not yet implemented")
            }

            override fun onCreateFailure(p0: String?) {
                TODO("Not yet implemented")
            }

            override fun onSetFailure(p0: String?) {
                TODO("Not yet implemented")
            }
        },mediaConstraints)
    }
}