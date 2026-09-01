package com.wmods.wppenhacer.utils

import android.app.Activity
import android.widget.ScrollView
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wmods.wppenhacer.R

object LegalDisclaimerDialog {

    const val PREF_DISCLAIMER_ACCEPTED = "disclaimer_accepted_v1"

    const val DISCLAIMER_TEXT = """
⚠️ TERMS OF USE, PRIVACY POLICY & ABSOLUTE LIABILITY WAIVER

Please read this legal notice carefully before using PrimeWA.

1. Educational & Research Purpose
PrimeWA is an independent, open-source customization suite and Xposed framework module developed strictly for educational, security research, and personal customization purposes.

2. Complete Disclaimer of Liability & Warranty
THIS SOFTWARE IS PROVIDED ON AN "AS IS" AND "AS AVAILABLE" BASIS WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO FITNESS FOR A PARTICULAR PURPOSE, RELIABILITY, SECURITY, OR NON-INFRINGEMENT.

3. Assumption of Full Risk
You explicitly understand, acknowledge, and agree that installing, enabling, or using this application and its underlying Xposed hooks is done entirely at your own discretion and risk.

Under no circumstances and under no legal theory (whether in contract, tort, negligence, strict liability, or otherwise) shall:
• The maintainer, creator, or contributors of PrimeWA (including Owais / TheOwaisLone), OR
• The original creator, maintainers, or contributors of WaEnhancer (including Dev4Mod and contributors), OR
• Any developers of underlying libraries (including LSPosed, Bootloader Spoofer, etc.)

be held liable or responsible for:
a) Device Failures: Any hardware malfunction, soft-bricks, bootloops, operating system crashes, or electronic defects occurring on your device.
b) Data Loss & Corruption: Any loss, corruption, alteration, accidental deletion, or inability to recover messages, media files, call recordings, databases, encryption keys, or system data.
c) Third-Party Compromise & Security: Any security vulnerabilities, data leaks, unauthorized access, or malicious interference caused by third-party software, root tools, malicious modules, or network interceptors.
d) Account Restrictions & Bans: Any temporary or permanent account suspensions, bans, or enforcement actions taken by WhatsApp LLC, Meta Platforms, Inc., or other service providers resulting from modified client behavior.

4. User Responsibility & Agreement
You are solely and fully responsible for ensuring compliance with all applicable laws and terms of service. If you do not agree to these terms, you must exit this application and uninstall it immediately.

By clicking "I Accept & Agree", you acknowledge that you have read, understood, and agreed to this waiver in its entirety.
"""

    @JvmStatic
    fun checkAndShow(activity: Activity) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val accepted = prefs.getBoolean(PREF_DISCLAIMER_ACCEPTED, false)
        if (!accepted) {
            show(activity, isFirstLaunch = true)
        }
    }

    @JvmStatic
    fun show(activity: Activity, isFirstLaunch: Boolean = false) {
        val pad = (16 * activity.resources.displayMetrics.density).toInt()
        val scrollView = ScrollView(activity)
        val textView = TextView(activity).apply {
            text = DISCLAIMER_TEXT.trimIndent()
            setPadding(pad, pad, pad, pad)
            textSize = 13f
            setLineSpacing(0f, 1.25f)
            setTextColor(activity.getColor(R.color.text_primary))
        }
        scrollView.addView(textView)

        val builder = MaterialAlertDialogBuilder(activity)
            .setTitle(if (isFirstLaunch) "⚠️ Terms of Use & Disclaimer" else "Legal Disclaimer & Terms")
            .setView(scrollView)
            .setCancelable(!isFirstLaunch)

        if (isFirstLaunch) {
            builder.setPositiveButton("I Accept & Agree") { dialog, _ ->
                val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
                prefs.edit().putBoolean(PREF_DISCLAIMER_ACCEPTED, true).apply()
                dialog.dismiss()
            }
            builder.setNegativeButton("Exit") { _, _ ->
                activity.finishAffinity()
            }
        } else {
            builder.setPositiveButton("Close", null)
        }

        builder.show()
    }
}
