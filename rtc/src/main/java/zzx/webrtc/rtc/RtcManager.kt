package zzx.webrtc.rtc

abstract class RtcManager{

   companion object {
       val instance: RtcManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RtcManagerImpl()
       }
   }

    abstract fun init()

    abstract fun create();

    abstract fun join();

    abstract fun leave();

    abstract fun setRtcListener()

    abstract fun onActivityResult();
    
}