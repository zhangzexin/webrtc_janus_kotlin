package zzx.webrtc.rtc.interfaces

interface ILocalDescription {

    //远端建链成功，可以添加自己的本地层级
    fun onAddStream()

    fun onFailuer(error: String)
}