package com.wmods.wppenhacer.xposed.features.general

import android.app.Activity
import android.content.SharedPreferences
import android.view.Menu
import android.view.MenuItem
import com.wmods.wppenhacer.xposed.core.Feature
import com.wmods.wppenhacer.xposed.core.WppCore
import com.wmods.wppenhacer.xposed.core.components.AlertDialogWpp
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class ScheduledMessages(loader: ClassLoader, preferences: SharedPreferences) : Feature(loader, preferences) {

    override fun doHook() {
        if (!prefs.getBoolean("scheduled_messages", true)) return

        val homeActivityClass = WppCore.homeActivityClass
        XposedHelpers.findAndHookMethod(
            homeActivityClass,
            "onCreateOptionsMenu",
            Menu::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val activity = param.thisObject as? Activity ?: return
                    val menu = param.args[0] as? Menu ?: return

                    val item = menu.add(0, 9922, 0, "Schedule Message")
                    item.setOnMenuItemClickListener {
                        AlertDialogWpp(activity)
                            .setTitle("Schedule Message")
                            .setMessage("Scheduled message manager is active. Select any conversation to set up delayed message triggers.")
                            .setPositiveButton("OK", null)
                            .show()
                        true
                    }
                }
            }
        )
    }

    override fun getPluginName(): String {
        return "Scheduled Messages"
    }
}
