package com.enlistech.baseutils.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import com.enlistech.baseutils.BuildConfig
import java.net.NetworkInterface
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * @author Jaydip Umaretiya.
 * Email - jaydip@enlistech.com
 */
object AppUtils {
    //Current Android version data
    fun currentAppVersion(): String {
        return "v" + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")"
    }

    //Current Android version data
    fun currentOSVersion(): String {
        val release = Build.VERSION.RELEASE
        return "v" + release + ", API Level: " + Build.VERSION.SDK_INT
    }

    val deviceInfo: String
        get() {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                model
            } else {
                "$manufacturer $model"
            }
        }
    val macAddress: String
        get() {
            try {
                val all: List<NetworkInterface> =
                    Collections.list(NetworkInterface.getNetworkInterfaces())
                for (nif in all) {
                    if (!nif.name.equals("wlan0", ignoreCase = true)) continue
                    val macBytes = nif.hardwareAddress ?: return ""
                    val res1 = StringBuilder()
                    for (b in macBytes) {
                        res1.append(String.format("%02X:", b))
                    }
                    if (res1.length > 0) {
                        res1.deleteCharAt(res1.length - 1)
                    }
                    return res1.toString()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return "02:00:00:00:00:00"
        }

    /**
     * The app version code (not the version name!) that was used on the last
     * start of the app.
     */
    private const val LAST_APP_VERSION = "last_app_version"

    /**
     * Finds out started for the first time (ever or in the current version).<br></br>
     * <br></br>
     * Note: This method is **not idempotent** only the first call will
     * determine the proper result. Any subsequent calls will only return
     * [AppStart.NORMAL] until the app is started again. So you might want
     * to consider caching the result!
     *
     * @return the type of app start
     */
    fun checkAppStart(context: Context): AppStart {
        val pInfo: PackageInfo
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        var appStart = AppStart.NORMAL
        try {
            pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val lastVersionCode = sharedPreferences.getInt(LAST_APP_VERSION, -1)
            val currentVersionCode = pInfo.versionCode
            appStart = checkAppStart(currentVersionCode, lastVersionCode)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w(
                "App Start",
                "Unable to determine current app version from pacakge manager. Defensively assuming normal app start."
            )
        }
        return appStart
    }

    private fun checkAppStart(currentVersionCode: Int, lastVersionCode: Int): AppStart {
        return if (lastVersionCode == -1) {
            AppStart.FIRST_TIME
        } else if (lastVersionCode < currentVersionCode) {
            AppStart.FIRST_TIME_VERSION
        } else if (lastVersionCode > currentVersionCode) {
            Log.w(
                "App Start", "Current version code (" + currentVersionCode
                        + ") is less then the one recognized on last startup ("
                        + lastVersionCode
                        + "). Defensively assuming normal app start."
            )
            AppStart.NORMAL
        } else {
            AppStart.NORMAL
        }
    }

    fun updateVersionAppStart(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val currentVersionCode = pInfo.versionCode
            // Update version in preferences
            sharedPreferences.edit().putInt(LAST_APP_VERSION, currentVersionCode).apply()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    fun md5(string: String): String {
        try {
            // Create MD5 Hash
            val digest = MessageDigest.getInstance("MD5")
            digest.update(string.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                val h = StringBuilder(Integer.toHexString(0xFF and aMessageDigest.toInt()))
                while (h.length < 2) h.insert(0, "0")
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    fun rateApp(context: Context, packageName: String) {
        val uri = Uri.parse("market://details?id=$packageName")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
        } else {
            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
        }
        try {
            context.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }

    fun moreApp(context: Context, developerName: String) {
        val uri = Uri.parse("market://search?q=pub:$developerName")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
        } else {
            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
        }
        try {
            context.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/dev?id=$developerName")
                )
            )
        }
    }

    fun shareApp(context: Context, packageName: String, subject: String?, message: String) {
        try {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_SUBJECT, subject)
            val sAux =
                "\n$message\n\nhttps://play.google.com/store/apps/details?id=$packageName\n\n"
            i.putExtra(Intent.EXTRA_TEXT, sAux)
            context.startActivity(Intent.createChooser(i, "choose one"))
        } catch (e: Exception) {
            //e.toString();
        }
    }
}