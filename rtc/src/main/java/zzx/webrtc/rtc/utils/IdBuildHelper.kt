package zzx.webrtc.rtc.utils

import java.util.*

class IdBuildHelper {
    companion object{
        fun randomString(length: Int): String {
            val str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            val rnd = Random()
            val sb = StringBuilder(length)
            for (i in 0 until length) {
                sb.append(str[rnd.nextInt(str.length)])
            }
            return sb.toString()
        }
    }
}