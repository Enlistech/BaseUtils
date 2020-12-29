package com.enlistech.baseutils.util

import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager

/**
 * @author Jaydip Umaretiya.
 * Email - jaydip@enlistech.com
 */
object NetworkUtils {

    const val TYPE_WIFI = 1
    const val TYPE_4G = 2
    const val TYPE_3G = 3
    const val TYPE_2G = 4

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    /**
     * To get device consuming network type like wifi, 4g, 3g, 2g
     *
     * @param context
     * @return one of network type [NetworkUtils.TYPE_WIFI], [NetworkUtils.TYPE_4G],
     * [NetworkUtils.TYPE_3G] or [NetworkUtils.TYPE_2G]
     */
    fun getNetworkType(context: Context): Int {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        when {
            activeNetwork == null -> {
                return 0
            }
            activeNetwork.type == ConnectivityManager.TYPE_WIFI -> {
                return TYPE_WIFI
            }
            activeNetwork.type == ConnectivityManager.TYPE_MOBILE -> {
                val mTelephonyManager =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val networkType = mTelephonyManager.networkType
                return when (networkType) {
                    TelephonyManager.NETWORK_TYPE_LTE ->                     //No specification for the 4g but from wiki
                        //I found(LTE (Long-Term Evolution, commonly marketed as 4G LTE))
                        //https://goo.gl/9t7yrR
                        TYPE_4G
                    TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP ->                     //For 3g HSDPA , HSPAP(HSPA+) are menu_normal  networktype which are under 3g Network
                        //But from other constants also it will 3g like HSPA,HSDPA etc which are in 3g case.
                        //Some cases are added after  testing(real) in device with 3g enable data
                        //and speed also matters to decide 3g network type
                        //http://goo.gl/bhtVT
                        TYPE_3G
                    TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> TYPE_2G
                    else -> 0
                }
            }
            else -> return 0
        }
    }
}