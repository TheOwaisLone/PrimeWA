package com.wmods.wppenhacer.xposed.features.others

import android.app.Activity
import android.content.SharedPreferences
import android.view.Menu
import android.view.MenuItem
import com.wmods.wppenhacer.xposed.core.Feature
import com.wmods.wppenhacer.xposed.core.WppCore
import com.wmods.wppenhacer.xposed.core.components.AlertDialogWpp
import com.wmods.wppenhacer.xposed.core.db.MessageStore
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class ChatAnalytics(loader: ClassLoader, preferences: SharedPreferences) : Feature(loader, preferences) {

    override fun doHook() {
        if (!prefs.getBoolean("chat_analytics", true)) return

        val homeActivityClass = WppCore.homeActivityClass
        XposedHelpers.findAndHookMethod(
            homeActivityClass,
            "onCreateOptionsMenu",
            Menu::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val activity = param.thisObject as? Activity ?: return
                    val menu = param.args[0] as? Menu ?: return

                    val item = menu.add(0, 9921, 0, "Chat Analytics")
                    item.setOnMenuItemClickListener {
                        showAnalyticsDialog(activity)
                        true
                    }
                }
            }
        )
    }

    private fun showAnalyticsDialog(activity: Activity) {
        val db = MessageStore.getInstance().getDatabase()
        if (db == null || !db.isOpen) {
            AlertDialogWpp(activity)
                .setTitle("Chat Analytics")
                .setMessage("Database unavailable. Please open a chat thread first.")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        try {
            var totalMessages = 0
            var sentMessages = 0
            var receivedMessages = 0
            var mediaCount = 0

            db.rawQuery(
                "SELECT COUNT(*), SUM(CASE WHEN from_me = 1 THEN 1 ELSE 0 END), SUM(CASE WHEN from_me = 0 THEN 1 ELSE 0 END), SUM(CASE WHEN media_wa_type <> 0 THEN 1 ELSE 0 END) FROM message",
                null
            ).use { cursor ->
                if (cursor.moveToFirst()) {
                    totalMessages = cursor.getInt(0)
                    sentMessages = cursor.getInt(1)
                    receivedMessages = cursor.getInt(2)
                    mediaCount = cursor.getInt(3)
                }
            }

            val report = """
                📊 Overall Conversation Insights
                
                • Total Messages: $totalMessages
                • Messages Sent: $sentMessages
                • Messages Received: $receivedMessages
                • Media & Files Exchanged: $mediaCount
                • Text Ratio: ${if (totalMessages > 0) String.format("%.1f%%", (sentMessages.toFloat() / totalMessages) * 100) else "0%"} Sent
            """.trimIndent()

            AlertDialogWpp(activity)
                .setTitle("Chat Analytics & Insights")
                .setMessage(report)
                .setPositiveButton("Close", null)
                .show()
        } catch (e: Throwable) {
            XposedBridge.log(e)
            AlertDialogWpp(activity)
                .setTitle("Chat Analytics")
                .setMessage("Error gathering analytics: ${e.message}")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    override fun getPluginName(): String {
        return "Chat Analytics"
    }
}
