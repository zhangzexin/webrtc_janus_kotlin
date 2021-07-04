package zzx.webrtc.rtc.utils

import android.content.Context
import android.util.Log
import org.webrtc.Camera1Enumerator
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerator
import org.webrtc.VideoCapturer

class CameraCapturerUtlis {


    companion object{
        @JvmStatic
        val TAG = "Camera"
        fun createVideoCapturer(context: Context): VideoCapturer? {
            var videoCapturer: VideoCapturer? = null
            if (useCamera2(context)) {
                Log.d(
                    TAG,
                    "Creating capturer using camera2 API."
                )
                videoCapturer = createCameraCapturer(Camera2Enumerator(context))
            } else {
                Log.d(
                    TAG,
                    "Creating capturer using camera1 API."
                )
                videoCapturer = createCameraCapturer(Camera1Enumerator(captureToTexture()))
            }
            if (videoCapturer == null) {
                Log.e(TAG, "Failed to open camera")
                return null
            }
            return videoCapturer
        }

        private fun useCamera2(context: Context): Boolean {
            return Camera2Enumerator.isSupported(context)
        }

        private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
            val deviceNames = enumerator.deviceNames

            // First, try to find front facing camera
            Log.d(TAG, "Looking for front facing cameras.")
            for (deviceName in deviceNames) {
                if (enumerator.isFrontFacing(deviceName)) {
                    Log.d(TAG,
                        "Creating front facing camera capturer."
                    )
                    val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                    if (videoCapturer != null) {
                        return videoCapturer
                    }
                }
            }

            // Front facing camera not found, try something else
            Log.d(TAG, "Looking for other cameras.")
            for (deviceName in deviceNames) {
                if (!enumerator.isFrontFacing(deviceName)) {
                    Log.d(TAG,
                        "Creating other camera capturer."
                    )
                    val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                    if (videoCapturer != null) {
                        return videoCapturer
                    }
                }
            }
            return null
        }

        private fun captureToTexture(): Boolean {
            return true
        }
    }



}