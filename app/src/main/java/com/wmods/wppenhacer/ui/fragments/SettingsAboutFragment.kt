package com.wmods.wppenhacer.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wmods.wppenhacer.App
import com.wmods.wppenhacer.BuildConfig
import com.wmods.wppenhacer.R
import com.wmods.wppenhacer.adapter.LogLineAdapter
import com.wmods.wppenhacer.ads.AdHelper
import com.wmods.wppenhacer.databinding.DialogDiagnosticsLogBinding
import com.wmods.wppenhacer.databinding.FragmentSettingsAboutBinding
import com.wmods.wppenhacer.utils.FilePicker
import com.wmods.wppenhacer.utils.RootDiagnostics
import com.wmods.wppenhacer.xposed.core.FeatureLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import rikka.core.util.IOUtils
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.HashSet
import java.util.Locale

class SettingsAboutFragment : Fragment() {

    private var _binding: FragmentSettingsAboutBinding? = null
    private val binding get() = _binding!!

    companion object {
        val CONTRIBUTORS = arrayOf(
            arrayOf("Dev4Mod (Original Creator)", "https://github.com/Dev4Mod"),
            arrayOf("frknkrc44", "https://github.com/frknkrc44"),
            arrayOf("mubashardev", "https://github.com/mubashardev"),
            arrayOf("masbentoooredoo", "https://github.com/masbentoooredoo"),
            arrayOf("zhongerxll", "https://github.com/zhongerxll"),
            arrayOf("BryanGIG", "https://github.com/BryanGIG"),
            arrayOf("rizqi-developer", "https://github.com/rizqi-developer"),
            arrayOf("pedroborraz", "https://github.com/pedroborraz"),
            arrayOf("ahmedtohamy1", "https://github.com/ahmedtohamy1"),
            arrayOf("mohdafix", "https://github.com/mohdafix"),
            arrayOf("maulana-kurniawan", "https://github.com/maulana-kurniawan"),
            arrayOf("erzachn", "https://github.com/erzachn"),
            arrayOf("cvnertnc", "https://github.com/cvnertnc"),
            arrayOf("rkorossy", "https://github.com/rkorossy"),
            arrayOf("StupidRepo", "https://github.com/StupidRepo"),
            arrayOf("Blank517", "https://github.com/Blank517"),
            arrayOf("astola-studio", "https://github.com/astola-studio"),
            arrayOf("Strange-IPmart", "https://github.com/Strange-IPmart"),
            arrayOf("chiteroman (Bootloader Spoofer)", "https://github.com/chiteroman"),
            arrayOf("LSPosed Team", "https://github.com/LSPosed"),
            arrayOf("rhunk (Bridge Client/Server)", "https://github.com/rhunk")
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appVersionText.text = "v${BuildConfig.VERSION_NAME} • Developed by Owais"

        // Community & Repository Links
        binding.btnTelegramPrime.setOnClickListener {
            openUrl("https://t.me/primewa")
        }

        binding.btnGithubPrime.setOnClickListener {
            openUrl("https://github.com/owais/prime-wa")
        }

        binding.btnTelegramUpstream.setOnClickListener {
            openUrl("https://t.me/waenhancer")
        }

        binding.btnGithubUpstream.setOnClickListener {
            openUrl("https://github.com/Dev4Mod/WaEnhancer")
        }

        // Contributors & License Dialogs
        binding.btnContributors.setOnClickListener {
            showContributorsDialog()
        }

        binding.btnLicense.setOnClickListener {
            showLicenseDialog()
        }

        // Load Native Ad
        context?.let { ctx ->
            AdHelper.loadNativeAd(ctx, binding.nativeAdContainer)
        }

        // Export Preferences
        binding.exportBtn.setOnClickListener {
            context?.let { ctx -> saveConfigs(ctx) }
        }

        // Import Preferences
        binding.importBtn.setOnClickListener {
            context?.let { ctx -> importConfigs(ctx) }
        }

        // Reset Settings
        binding.resetBtn.setOnClickListener {
            context?.let { ctx -> showResetConfirmationDialog(ctx) }
        }

        // Diagnostics Dialog
        binding.diagBtn.setOnClickListener {
            showDiagnosticsDialog()
        }
    }

    private fun showContributorsDialog() {
        val context = requireContext()
        val scrollView = ScrollView(context)
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val pad = (16 * resources.displayMetrics.density).toInt()
            setPadding(pad, pad, pad, pad)
        }

        val descText = TextView(context).apply {
            text = getString(R.string.contributors_dialog_desc)
            setTextColor(requireContext().getColor(R.color.text_secondary))
            textSize = 13f
            setPadding(0, 0, 0, (12 * resources.displayMetrics.density).toInt())
        }
        container.addView(descText)

        for (contributor in CONTRIBUTORS) {
            val button = MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                text = contributor[0]
                setIconResource(R.drawable.ic_github)
                iconSize = (20 * resources.displayMetrics.density).toInt()
                iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
                iconPadding = (10 * resources.displayMetrics.density).toInt()
                textSize = 13f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = (6 * resources.displayMetrics.density).toInt()
                }
                setOnClickListener { openUrl(contributor[1]) }
            }
            container.addView(button)
        }

        scrollView.addView(container)

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.contributors_dialog_title)
            .setView(scrollView)
            .setPositiveButton(R.string.diag_close, null)
            .show()
    }

    private fun showLicenseDialog() {
        val context = requireContext()
        val message = """
            PrimeWA is licensed under the GNU General Public License v3.0 (GPL-3.0).

            • Freedom to use: You are free to run this program for any purpose.
            • Freedom to study & modify: You can study how the program works and change it.
            • Copyleft distribution: If you distribute modified versions or binaries, you MUST provide the complete corresponding source code under the exact same GPL-3.0 license.
            • Preserved attribution: All original copyright notices and contributor credits are preserved.

            Warranty Disclaimer:
            THIS SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED.
        """.trimIndent()

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.gpl_license_dialog_title)
            .setMessage(message)
            .setPositiveButton(R.string.diag_close, null)
            .setNeutralButton("GNU Website") { _, _ ->
                openUrl("https://www.gnu.org/licenses/gpl-3.0.html")
            }
            .show()
    }

    private fun showResetConfirmationDialog(context: Context) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.reset_settings)
            .setMessage("Are you sure you want to reset all customization and enhancement settings to defaults?")
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                val editor = prefs.edit()
                prefs.all.keys.forEach { key -> editor.remove(key) }
                editor.apply()
                App.instance.restartApp(FeatureLoader.PACKAGE_WPP)
                App.instance.restartApp(FeatureLoader.PACKAGE_BUSINESS)
                Toast.makeText(context, context.getString(R.string.configs_reset), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun saveConfigs(context: Context) {
        FilePicker.setOnUriPickedListener { uri ->
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    context.contentResolver.openOutputStream(uri)?.use { output ->
                        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                        val jsonObject = getJsonObject(prefs)
                        output.write(jsonObject.toString(4).toByteArray())
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, context.getString(R.string.configs_saved), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
        val formattedDate = dateFormat.format(Date())
        FilePicker.fileSalve.launch("primewa_configs_$formattedDate.json")
    }

    private fun importConfigs(context: Context) {
        FilePicker.setOnUriPickedListener { uri ->
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        val data = rikka.core.util.IOUtils.toString(input)
                        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                        val jsonObject = JSONObject(data)

                        val editor = prefs.edit()
                        prefs.all.keys.forEach { key -> editor.remove(key) }

                        val keys = jsonObject.keys()
                        while (keys.hasNext()) {
                            val keyName = keys.next()
                            var value = jsonObject.get(keyName)
                            var type = value.javaClass.simpleName
                            if (value is JSONObject) {
                                type = value.getString("type")
                                value = value.get("value")
                            }

                            when (type) {
                                "JSONArray" -> {
                                    val jsonArray = value as JSONArray
                                    val hashSet = HashSet<String>()
                                    for (i in 0 until jsonArray.length()) {
                                        hashSet.add(jsonArray.getString(i))
                                    }
                                    editor.putStringSet(keyName, hashSet)
                                }
                                "String" -> editor.putString(keyName, value as String)
                                "Boolean", "boolean" -> editor.putBoolean(keyName, value as Boolean)
                                "Integer", "int" -> editor.putInt(keyName, value as Int)
                                "Long", "long" -> editor.putLong(keyName, (value as Number).toLong())
                                "Double", "double", "Float", "float" -> editor.putFloat(keyName, (value as Number).toFloat())
                            }
                        }
                        editor.apply()
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, context.getString(R.string.configs_imported), Toast.LENGTH_SHORT).show()
                        App.instance.restartApp(FeatureLoader.PACKAGE_WPP)
                        App.instance.restartApp(FeatureLoader.PACKAGE_BUSINESS)
                    }
                } catch (e: Exception) {
                    Log.e("importConfigs", e.message ?: "", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        FilePicker.fileCapture.launch(arrayOf("application/json"))
    }

    private fun getJsonObject(prefs: android.content.SharedPreferences): JSONObject {
        val jsonObject = JSONObject()
        for ((key, value) in prefs.all) {
            val type = JSONObject()
            var keyValue: Any? = value
            if (keyValue is HashSet<*>) {
                keyValue = JSONArray(ArrayList(keyValue))
            }
            if (keyValue != null) {
                type.put("type", keyValue.javaClass.simpleName)
                type.put("value", keyValue)
                jsonObject.put(key, type)
            }
        }
        return jsonObject
    }

    private fun showDiagnosticsDialog() {
        val context = requireContext()
        val dialogBinding = DialogDiagnosticsLogBinding.inflate(LayoutInflater.from(context))
        val logAdapter = LogLineAdapter()

        dialogBinding.logRecycler.layoutManager = LinearLayoutManager(context)
        dialogBinding.logRecycler.adapter = logAdapter

        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(R.string.diag_dialog_title)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.diag_close, null)
            .setCancelable(true)
            .show()

        val handler = Handler(Looper.getMainLooper())
        val queue = ArrayList<RootDiagnostics.LogEntry>()

        RootDiagnostics.runDiagnostics(context) { entry ->
            if (!isAdded) return@runDiagnostics
            queue.add(entry)
        }

        val poller = object : Runnable {
            private var emptyCycles = 0

            override fun run() {
                if (!isAdded || _binding == null || !dialog.isShowing) return

                if (queue.isNotEmpty()) {
                    emptyCycles = 0
                    logAdapter.add(queue.removeAt(0))
                    dialogBinding.logRecycler.smoothScrollToPosition(logAdapter.itemCount - 1)
                    handler.postDelayed(this, 120)
                } else if (emptyCycles < 50) {
                    emptyCycles++
                    handler.postDelayed(this, 120)
                }
            }
        }
        handler.postDelayed(poller, 120)
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
