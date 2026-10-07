package com.example.assistantx.engine

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo

data class AppInfo(val packageName: String, val label: String)

class AppResolver(private val context: Context) {

    fun resolve(appName: String): List<AppInfo> {
        val pm = context.packageManager
        
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        
        val activities: List<ResolveInfo> = pm.queryIntentActivities(mainIntent, 0)
        val targetQuery = appName.trim().lowercase()
        
        val exactMatches = mutableListOf<AppInfo>()
        val partialMatches = mutableListOf<AppInfo>()
        
        for (resolveInfo in activities) {
            val label = resolveInfo.loadLabel(pm).toString()
            val packageName = resolveInfo.activityInfo.packageName
            
            val normalizedLabel = label.trim().lowercase()
            
            if (normalizedLabel == targetQuery) {
                exactMatches.add(AppInfo(packageName, label))
            } else if (normalizedLabel.contains(targetQuery)) {
                partialMatches.add(AppInfo(packageName, label))
            }
        }
        
        // Prefer exact matches. If none, return partial matches.
        val results = if (exactMatches.isNotEmpty()) exactMatches else partialMatches
        
        return results.distinctBy { it.packageName }
    }
}
