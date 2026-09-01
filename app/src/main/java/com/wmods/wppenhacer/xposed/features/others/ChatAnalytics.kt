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

            // Query existing column names dynamically
            val columns = mutableSetOf<String>()
            val tableName = try {
                db.rawQuery("SELECT name FROM sqlite_master WHERE type IN ('table','view') AND name IN ('message','message_view')", null).use { cursor ->
                    val names = mutableListOf<String>()
                    while (cursor.moveToNext()) {
                        names.add(cursor.getString(0))
                    }
                    if (names.contains("message")) "message" else names.firstOrNull() ?: "message"
                }
            } catch (_: Throwable) {
                "message"
            }

            try {
                db.rawQuery("PRAGMA table_info($tableName)", null).use { cursor ->
                    val nameIdx = cursor.getColumnIndex("name")
                    while (cursor.moveToNext()) {
                        if (nameIdx != -1) {
                            columns.add(cursor.getString(nameIdx).lowercase())
                        }
                    }
                }
            } catch (_: Throwable) {}

            val fromMeCol = if (columns.contains("from_me")) "from_me" else null
            val mediaCol = when {
                columns.contains("media_wa_type") -> "media_wa_type"
                columns.contains("message_type") -> "message_type"
                columns.contains("message_sub_type") -> "message_sub_type"
                else -> null
            }

            val sentExpr = if (fromMeCol != null) "COALESCE(SUM(CASE WHEN $fromMeCol = 1 THEN 1 ELSE 0 END), 0)" else "0"
            val recvExpr = if (fromMeCol != null) "COALESCE(SUM(CASE WHEN $fromMeCol = 0 THEN 1 ELSE 0 END), 0)" else "0"
            val mediaExpr = if (mediaCol != null) "COALESCE(SUM(CASE WHEN $mediaCol <> 0 AND $mediaCol IS NOT NULL THEN 1 ELSE 0 END), 0)" else "0"

            val sql = "SELECT COUNT(*), $sentExpr, $recvExpr, $mediaExpr FROM $tableName"

            db.rawQuery(sql, null).use { cursor ->
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
                • Text Ratio: ${if (totalMessages > 0) String.format(java.util.Locale.US, "%.1f%%", (sentMessages.toFloat() / totalMessages) * 100) else "0%"} Sent
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
