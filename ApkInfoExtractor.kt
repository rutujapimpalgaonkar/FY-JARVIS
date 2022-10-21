package com.plgpl.jarvis.helpers

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager.NameNotFoundException
import android.content.pm.ResolveInfo
import java.util.*


class ApkInfoExtractor(var context: Context) {
    private fun getAllInstalledApkInfo(): List<String> {
        val ApkPackageName: MutableList<String> = ArrayList()
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        val resolveInfoList = context.packageManager.queryIntentActivities(intent, 0)
        for (resolveInfo in resolveInfoList) {
            val activityInfo = resolveInfo.activityInfo
//            if (!isSystemPackage(resolveInfo)) {
                ApkPackageName.add(activityInfo.applicationInfo.packageName)
//            }
        }
        return ApkPackageName
    }

    private fun isSystemPackage(resolveInfo: ResolveInfo): Boolean {
        return resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    fun getAppName(ApkPackageName: String?): String {
        var Name = ""
        val applicationInfo: ApplicationInfo
        val packageManager = context.packageManager
        try {
            applicationInfo = packageManager.getApplicationInfo(ApkPackageName, 0)
            if (applicationInfo != null) {
                Name = packageManager.getApplicationLabel(applicationInfo) as String
            }
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
        }
        return Name.toLowerCase()
    }

    fun getAllName(): Map<String, String> {
        return getAllInstalledApkInfo().map { packageName -> getAppName(packageName) to packageName }
            .toMap()
    }
}