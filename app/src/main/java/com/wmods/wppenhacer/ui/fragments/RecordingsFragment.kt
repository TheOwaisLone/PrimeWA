package com.wmods.wppenhacer.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.wmods.wppenhacer.R
import com.wmods.wppenhacer.adapter.RecordingsAdapter
import com.wmods.wppenhacer.databinding.FragmentRecordingsBinding
import com.wmods.wppenhacer.model.Recording
import com.wmods.wppenhacer.ui.dialogs.AudioPlayerDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.LinkedHashSet

class RecordingsFragment : Fragment(), RecordingsAdapter.OnRecordingActionListener {

    private var _binding: FragmentRecordingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RecordingsAdapter
    private val allRecordings = ArrayList<Recording>()
    private var isGroupByContact = false
    private var currentSortType = 1 // 1=date, 2=name, 3=duration, 4=contact
    private var currentAppFilter = 0 // 0=All, 1=WA, 2=W4B
    private var isSettingsExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecorderSettings()
        initRecordingsList()
        initSelectionBar()

        loadRecordings()
    }

    private fun initRecorderSettings() {
        val context = requireContext()
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val isEnabled = prefs.getBoolean("call_recording_enable", false)
        val useRoot = prefs.getBoolean("call_recording_use_root", false)
        val showToast = prefs.getBoolean("call_recording_toast", false)
        val customPath = prefs.getString("call_recording_path", null)
            ?: File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "WA Call Recordings").absolutePath

        binding.switchCallRecording.isChecked = isEnabled
        binding.controlsContainer.visibility = if (isEnabled) View.VISIBLE else View.GONE
        binding.switchRecordingToast.isChecked = showToast
        binding.tvRecordingPath.text = customPath
        binding.settingsBody.visibility = if (isSettingsExpanded) View.VISIBLE else View.GONE
        binding.ivExpandIcon.rotation = if (isSettingsExpanded) 180f else 0f

        if (useRoot) {
            binding.chipRootMode.isChecked = true
            binding.tvModeDesc.text = getString(R.string.root_mode_description)
            binding.btnCheckRoot.visibility = View.VISIBLE
        } else {
            binding.chipNativeMode.isChecked = true
            binding.tvModeDesc.text = getString(R.string.non_root_mode_description)
            binding.btnCheckRoot.visibility = View.GONE
        }

        // Toggle card collapse/expand
        binding.headerSettingsToggle.setOnClickListener {
            isSettingsExpanded = !isSettingsExpanded
            binding.settingsBody.visibility = if (isSettingsExpanded) View.VISIBLE else View.GONE
            binding.ivExpandIcon.animate().rotation(if (isSettingsExpanded) 180f else 0f).setDuration(200).start()
        }

        // Master Switch
        binding.switchCallRecording.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("call_recording_enable", checked).apply()
            binding.controlsContainer.visibility = if (checked) View.VISIBLE else View.GONE
            try {
                val intent = Intent(com.wmods.wppenhacer.BuildConfig.APPLICATION_ID + ".MANUAL_RESTART")
                com.wmods.wppenhacer.App.instance.sendBroadcast(intent)
            } catch (_: Throwable) {}
        }

        // Mode Chips
        binding.chipGroupMode.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.contains(R.id.chip_root_mode)) {
                binding.tvModeDesc.text = getString(R.string.root_mode_description)
                binding.btnCheckRoot.visibility = View.VISIBLE
                checkRootAccess(autoSave = true)
            } else {
                prefs.edit().putBoolean("call_recording_use_root", false).apply()
                binding.tvModeDesc.text = getString(R.string.non_root_mode_description)
                binding.btnCheckRoot.visibility = View.GONE
                Toast.makeText(context, R.string.non_root_mode_enabled, Toast.LENGTH_SHORT).show()
                try {
                    val intent = Intent(com.wmods.wppenhacer.BuildConfig.APPLICATION_ID + ".MANUAL_RESTART")
                    com.wmods.wppenhacer.App.instance.sendBroadcast(intent)
                } catch (_: Throwable) {}
            }
        }

        // Root check button
        binding.btnCheckRoot.setOnClickListener {
            checkRootAccess(autoSave = true)
        }

        // Toast switch
        binding.switchRecordingToast.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("call_recording_toast", checked).apply()
        }

        // Change Folder
        binding.btnChangeFolder.setOnClickListener {
            showSelectDirectoryDialog()
        }
    }

    private fun checkRootAccess(autoSave: Boolean) {
        val context = context ?: return
        Toast.makeText(context, "Checking root access...", Toast.LENGTH_SHORT).show()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            var hasRoot = false
            var rootOutput = ""

            try {
                val process = Runtime.getRuntime().exec("su")
                val os = java.io.DataOutputStream(process.getOutputStream())
                val reader = java.io.BufferedReader(java.io.InputStreamReader(process.getInputStream()))

                os.writeBytes("id\n")
                os.writeBytes("exit\n")
                os.flush()

                val sb = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                rootOutput = sb.toString()

                val exitCode = process.waitFor()
                hasRoot = (exitCode == 0 && rootOutput.contains("uid=0"))
            } catch (e: Exception) {
                hasRoot = false
            }

            val rootGranted = hasRoot
            withContext(Dispatchers.Main) {
                if (_binding == null) return@withContext
                val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                if (rootGranted) {
                    if (autoSave) {
                        prefs.edit().putBoolean("call_recording_use_root", true).apply()
                    }
                    binding.tvModeDesc.text = "✓ " + getString(R.string.root_access_granted)
                    Toast.makeText(requireContext(), R.string.root_access_granted, Toast.LENGTH_SHORT).show()
                } else {
                    prefs.edit().putBoolean("call_recording_use_root", false).apply()
                    binding.chipNativeMode.isChecked = true
                    binding.tvModeDesc.text = "⚠️ " + getString(R.string.root_access_denied)
                    binding.btnCheckRoot.visibility = View.GONE
                    Toast.makeText(requireContext(), R.string.root_access_denied, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showSelectDirectoryDialog() {
        val context = context ?: return
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val currentPath = prefs.getString("call_recording_path", null)
        com.wmods.wppenhacer.ui.dialogs.FolderPickerDialog(
            context = context,
            initialPath = currentPath,
            dialogTitle = "Select Recordings Folder"
        ) { selectedDir ->
            val chosenPath = selectedDir.absolutePath
            prefs.edit().putString("call_recording_path", chosenPath).apply()
            binding.tvRecordingPath.text = chosenPath
            loadRecordings()
        }.show()
    }

    private fun initRecordingsList() {
        adapter = RecordingsAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        adapter.setSelectionChangeListener { count ->
            if (count > 0) {
                binding.selectionBar.visibility = View.VISIBLE
                binding.tvSelectionCount.text = getString(R.string.selected_count, count)
            } else {
                binding.selectionBar.visibility = View.GONE
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            loadRecordings()
        }

        binding.chipGroupViewMode.setOnCheckedStateChangeListener { _, checkedIds ->
            isGroupByContact = checkedIds.contains(R.id.chip_group_by_contact)
            applyViewMode()
        }

        binding.chipGroupAppFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            currentAppFilter = when {
                checkedIds.contains(R.id.chip_filter_wa) -> 1
                checkedIds.contains(R.id.chip_filter_w4b) -> 2
                else -> 0
            }
            applyViewMode()
        }

        binding.fabSort.setOnClickListener {
            showSortMenu()
        }
    }

    private fun initSelectionBar() {
        binding.btnCloseSelection.setOnClickListener {
            adapter.clearSelection()
        }

        binding.btnSelectAll.setOnClickListener {
            adapter.selectAll()
        }

        binding.btnShareSelected.setOnClickListener {
            shareSelectedRecordings()
        }

        binding.btnDeleteSelected.setOnClickListener {
            deleteSelectedRecordings()
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding == null) return
        loadRecordings()
    }

    private fun getBaseDirs(): List<File> {
        val context = context ?: return emptyList()
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val configuredPath = prefs.getString("call_recording_path", null)

        val dirs = ArrayList<File>()
        val addedPaths = LinkedHashSet<String>()

        if (!configuredPath.isNullOrEmpty()) {
            addBaseDir(dirs, addedPaths, File(configuredPath))
        }

        addBaseDir(dirs, addedPaths, File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "WA Call Recordings"
        ))
        addBaseDir(dirs, addedPaths, File(Environment.getExternalStorageDirectory(), "WA Call Recordings"))
        addBaseDir(dirs, addedPaths, File("/sdcard/Android/data/com.whatsapp/files/Recordings"))
        addBaseDir(dirs, addedPaths, File("/sdcard/Android/data/com.whatsapp.w4b/files/Recordings"))
        addBaseDir(dirs, addedPaths, File(Environment.getExternalStorageDirectory(), "Music/WaEnhancer/Recordings"))
        return dirs
    }

    private fun addBaseDir(dirs: MutableList<File>, addedPaths: MutableSet<String>, dir: File) {
        val normalizedPath = normalizePath(dir)
        if (addedPaths.add(normalizedPath)) {
            dirs.add(dir)
        }
    }

    private fun normalizePath(dir: File): String {
        return try {
            dir.canonicalPath
        } catch (ignored: IOException) {
            dir.absolutePath
        }
    }

    private fun loadRecordings() {
        if (_binding == null) return

        binding.swipeRefresh.isRefreshing = true
        val baseDirs = getBaseDirs()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val loaded = ArrayList<Recording>()
            val seenPaths = HashSet<String>()
            for (baseDir in baseDirs) {
                if (baseDir.exists() && baseDir.isDirectory) {
                    traverseDirectory(baseDir, seenPaths, loaded)
                }
            }

            applySort(loaded)

            withContext(Dispatchers.Main) {
                if (_binding == null) return@withContext
                allRecordings.clear()
                allRecordings.addAll(loaded)
                binding.swipeRefresh.isRefreshing = false

                if (allRecordings.isEmpty()) {
                    binding.emptyView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.emptyView.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    applyViewMode()
                }
            }
        }
    }

    private fun applyViewMode() {
        if (allRecordings.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
            adapter.setRecordings(emptyList())
            return
        }

        var list = when (currentAppFilter) {
            1 -> allRecordings.filter { it.appSource == Recording.AppSource.WHATSAPP }
            2 -> allRecordings.filter { it.appSource == Recording.AppSource.WA_BUSINESS }
            else -> allRecordings
        }.toMutableList()

        if (list.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
            adapter.setRecordings(emptyList())
            return
        }

        binding.emptyView.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE

        applySort(list)
        if (isGroupByContact) {
            val grouped = list.groupBy { it.contactName }
            val flatList = ArrayList<Recording>()
            for ((_, items) in grouped) {
                flatList.addAll(items)
            }
            adapter.setRecordings(flatList)
        } else {
            adapter.setRecordings(list)
        }
    }

    private fun traverseDirectory(dir: File, seenPaths: MutableSet<String>, result: MutableList<Recording>) {
        val files = dir.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory) {
                traverseDirectory(file, seenPaths, result)
            } else {
                val canonical = try { file.canonicalPath } catch (_: Exception) { file.absolutePath }
                if (seenPaths.add(canonical)) {
                    val name = file.name.lowercase()
                    if (name.endsWith(".wav") || name.endsWith(".mp3") || name.endsWith(".aac") || name.endsWith(".m4a")) {
                        result.add(Recording(file))
                    }
                }
            }
        }
    }

    private fun applySort(list: MutableList<Recording>) {
        when (currentSortType) {
            1 -> list.sortWith { r1, r2 -> r2.date.compareTo(r1.date) }
            2 -> list.sortWith(compareBy { it.contactName })
            3 -> list.sortWith { r1, r2 -> r2.duration.compareTo(r1.duration) }
            4 -> list.sortWith(compareBy<Recording> { it.contactName }.thenByDescending { it.date })
        }
    }

    private fun showSortMenu() {
        val popup = PopupMenu(requireContext(), binding.fabSort)
        popup.menu.add(0, 1, 0, R.string.sort_date)
        popup.menu.add(0, 2, 0, R.string.sort_name)
        popup.menu.add(0, 3, 0, R.string.sort_duration)
        popup.menu.add(0, 4, 0, R.string.sort_contact)

        popup.setOnMenuItemClickListener { item ->
            currentSortType = item.itemId
            loadRecordings()
            true
        }
        popup.show()
    }

    override fun onPlay(recording: Recording) {
        val dialog = AudioPlayerDialog(requireContext(), recording.file)
        dialog.show()
    }

    override fun onShare(recording: Recording) {
        shareRecording(recording.file)
    }

    override fun onDelete(recording: Recording) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_confirmation)
            .setMessage(recording.file.name)
            .setPositiveButton(android.R.string.yes) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    val deleted = recording.file.delete()
                    withContext(Dispatchers.Main) {
                        if (deleted) {
                            loadRecordings()
                        } else {
                            Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton(android.R.string.no, null)
            .show()
    }

    override fun onLongPress(recording: Recording, position: Int) {
        adapter.setSelectionMode(true)
        adapter.toggleSelection(position)
    }

    private fun shareRecording(file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName + ".fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "audio/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, getString(R.string.share_recording)))
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error sharing: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareSelectedRecordings() {
        val selected = adapter.selectedRecordings
        if (selected.isEmpty()) return

        if (selected.size == 1) {
            shareRecording(selected[0].file)
            adapter.clearSelection()
            return
        }

        val uris = ArrayList<Uri>()
        for (rec in selected) {
            try {
                val uri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().packageName + ".fileprovider",
                    rec.file
                )
                uris.add(uri)
            } catch (ignored: Exception) {}
        }

        if (uris.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "audio/*"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, getString(R.string.share_recordings)))
        }
        adapter.clearSelection()
    }

    private fun deleteSelectedRecordings() {
        val selected = adapter.selectedRecordings
        if (selected.isEmpty()) return

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_confirmation)
            .setMessage(getString(R.string.delete_multiple_confirmation, selected.size))
            .setPositiveButton(android.R.string.yes) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    var deleted = 0
                    for (rec in selected) {
                        if (rec.file.delete()) {
                            deleted++
                        }
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Deleted $deleted recordings", Toast.LENGTH_SHORT).show()
                        adapter.clearSelection()
                        loadRecordings()
                    }
                }
            }
            .setNegativeButton(android.R.string.no, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
