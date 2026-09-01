package com.wmods.wppenhacer.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.wmods.wppenhacer.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FolderPickerDialog(
    context: Context,
    private var initialPath: String? = null,
    private val dialogTitle: String? = null,
    private val onFolderSelected: (File) -> Unit
) : Dialog(context, com.google.android.material.R.style.Theme_Material3_DayNight_Dialog) {

    private var currentDirectory: File
    private val folderList = ArrayList<FolderItem>()
    private val filteredFolderList = ArrayList<FolderItem>()
    private lateinit var adapter: FolderAdapter

    private val tvDialogTitle: TextView
    private val tvCurrentPath: TextView
    private val tvSelectedPreview: TextView
    private val btnNavUp: ImageButton
    private val btnCreateFolder: MaterialButton
    private val btnCancel: MaterialButton
    private val btnSelectThisFolder: MaterialButton
    private val etSearch: TextInputEditText
    private val rvFolders: RecyclerView
    private val emptyView: LinearLayout
    private val progressLoading: ProgressBar
    private val chipGroupShortcuts: ChipGroup

    data class FolderItem(
        val file: File,
        val name: String,
        val subfolderCount: Int,
        val lastModified: Long
    )

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_folder_picker, null)
        setContentView(view)

        window?.let { win ->
            val displayMetrics = context.resources.displayMetrics
            win.setLayout(
                (displayMetrics.widthPixels * 0.94).toInt(),
                (displayMetrics.heightPixels * 0.85).toInt()
            )
            win.setBackgroundDrawableResource(android.R.color.transparent)
        }

        tvDialogTitle = view.findViewById(R.id.tv_dialog_title)
        tvCurrentPath = view.findViewById(R.id.tv_current_path)
        tvSelectedPreview = view.findViewById(R.id.tv_selected_preview)
        btnNavUp = view.findViewById(R.id.btn_nav_up)
        btnCreateFolder = view.findViewById(R.id.btn_create_folder)
        btnCancel = view.findViewById(R.id.btn_cancel)
        btnSelectThisFolder = view.findViewById(R.id.btn_select_this_folder)
        etSearch = view.findViewById(R.id.et_search)
        rvFolders = view.findViewById(R.id.rv_folders)
        emptyView = view.findViewById(R.id.empty_folder_view)
        progressLoading = view.findViewById(R.id.progress_loading)
        chipGroupShortcuts = view.findViewById(R.id.chip_group_shortcuts)

        if (!dialogTitle.isNullOrEmpty()) {
            tvDialogTitle.text = dialogTitle
        }

        // Set initial path
        val defaultRoot = Environment.getExternalStorageDirectory()
        var targetDir = defaultRoot
        if (!initialPath.isNullOrEmpty()) {
            val candidate = File(initialPath!!)
            if (candidate.exists() && candidate.isDirectory) {
                targetDir = candidate
            }
        }
        currentDirectory = targetDir

        initRecyclerView()
        initListeners()
        initShortcuts()

        navigateTo(currentDirectory)
    }

    private fun initRecyclerView() {
        adapter = FolderAdapter()
        rvFolders.layoutManager = LinearLayoutManager(context)
        rvFolders.adapter = adapter
    }

    private fun initListeners() {
        btnNavUp.setOnClickListener {
            val parent = currentDirectory.parentFile
            if (parent != null && parent.canRead() && parent.absolutePath.startsWith("/storage") || parent?.absolutePath?.startsWith("/sdcard") == true) {
                navigateTo(parent)
            } else {
                Toast.makeText(context, "Cannot navigate higher", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnSelectThisFolder.setOnClickListener {
            confirmSelection(currentDirectory)
        }

        btnCreateFolder.setOnClickListener {
            showCreateFolderDialog()
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterFolders(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun initShortcuts() {
        val rootChip = findViewById<Chip>(R.id.chip_shortcut_root)
        val dlChip = findViewById<Chip>(R.id.chip_shortcut_downloads)
        val docChip = findViewById<Chip>(R.id.chip_shortcut_documents)
        val musicChip = findViewById<Chip>(R.id.chip_shortcut_music)
        val waChip = findViewById<Chip>(R.id.chip_shortcut_whatsapp)

        rootChip?.setOnClickListener {
            navigateTo(Environment.getExternalStorageDirectory())
        }
        dlChip?.setOnClickListener {
            navigateTo(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
        }
        docChip?.setOnClickListener {
            navigateTo(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS))
        }
        musicChip?.setOnClickListener {
            navigateTo(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC))
        }
        waChip?.setOnClickListener {
            val waDir = File(Environment.getExternalStorageDirectory(), "Android/media/com.whatsapp/WhatsApp/Media")
            if (waDir.exists()) {
                navigateTo(waDir)
            } else {
                val legacyWa = File(Environment.getExternalStorageDirectory(), "WhatsApp")
                if (legacyWa.exists()) {
                    navigateTo(legacyWa)
                } else {
                    navigateTo(File(Environment.getExternalStorageDirectory(), "Android/media/com.whatsapp"))
                }
            }
        }
    }

    private fun navigateTo(dir: File) {
        if (!dir.exists() || !dir.isDirectory) {
            Toast.makeText(context, "Directory not accessible", Toast.LENGTH_SHORT).show()
            return
        }

        currentDirectory = dir
        tvCurrentPath.text = formatDisplayPath(dir)
        tvSelectedPreview.text = "Selected: ${dir.name} (${dir.absolutePath})"

        // Check if up navigation is available
        val parent = dir.parentFile
        val canGoUp = parent != null && parent.canRead() &&
                (parent.absolutePath.startsWith("/storage") || parent.absolutePath.startsWith("/sdcard"))
        btnNavUp.isEnabled = canGoUp
        btnNavUp.alpha = if (canGoUp) 1.0f else 0.4f

        loadSubfolders(dir)
    }

    private fun formatDisplayPath(dir: File): String {
        val path = dir.absolutePath
        val rootPath = Environment.getExternalStorageDirectory().absolutePath
        return when {
            path == rootPath -> "📁 Internal Storage"
            path.startsWith(rootPath) -> "📁 Internal Storage" + path.substring(rootPath.length)
            else -> path
        }
    }

    private fun loadSubfolders(dir: File) {
        folderList.clear()
        filteredFolderList.clear()

        val files = dir.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isDirectory && !file.name.startsWith(".")) {
                    var subCount = 0
                    try {
                        subCount = file.listFiles { f -> f.isDirectory && !f.name.startsWith(".") }?.size ?: 0
                    } catch (ignored: Exception) {}

                    folderList.add(FolderItem(file, file.name, subCount, file.lastModified()))
                }
            }
            folderList.sortBy { it.name.lowercase(Locale.getDefault()) }
        }

        filterFolders(etSearch.text?.toString().orEmpty())
    }

    private fun filterFolders(query: String) {
        filteredFolderList.clear()
        if (query.isBlank()) {
            filteredFolderList.addAll(folderList)
        } else {
            val q = query.lowercase(Locale.getDefault())
            for (item in folderList) {
                if (item.name.lowercase(Locale.getDefault()).contains(q)) {
                    filteredFolderList.add(item)
                }
            }
        }

        adapter.notifyDataSetChanged()

        if (filteredFolderList.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            rvFolders.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            rvFolders.visibility = View.VISIBLE
        }
    }

    private fun showCreateFolderDialog() {
        val input = EditText(context).apply {
            hint = "Folder name"
            setSingleLine()
            setPadding(48, 32, 48, 32)
        }

        MaterialAlertDialogBuilder(context)
            .setTitle("Create New Folder")
            .setMessage("Enter the name of the new folder inside:\n${currentDirectory.name}")
            .setView(input)
            .setPositiveButton("Create") { _, _ ->
                val folderName = input.text.toString().trim()
                if (folderName.isNotEmpty()) {
                    val newDir = File(currentDirectory, folderName)
                    if (newDir.exists()) {
                        Toast.makeText(context, "Folder already exists", Toast.LENGTH_SHORT).show()
                    } else if (newDir.mkdirs()) {
                        Toast.makeText(context, "Folder created", Toast.LENGTH_SHORT).show()
                        navigateTo(newDir)
                    } else {
                        Toast.makeText(context, "Failed to create folder", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmSelection(dir: File) {
        if (!dir.exists()) {
            dir.mkdirs()
        }

        // Test write permission
        try {
            val testFile = File(dir, ".wae_perm_test_${System.currentTimeMillis()}")
            if (testFile.createNewFile()) {
                testFile.delete()
                onFolderSelected(dir)
                dismiss()
                return
            }
        } catch (ignored: Exception) {}

        if (dir.canWrite()) {
            onFolderSelected(dir)
            dismiss()
        } else {
            Toast.makeText(context, "Selected folder is not writable. Please choose another location.", Toast.LENGTH_LONG).show()
        }
    }

    private inner class FolderAdapter : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

        private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

        inner class FolderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvFolderName: TextView = view.findViewById(R.id.tv_folder_name)
            val tvFolderDetails: TextView = view.findViewById(R.id.tv_folder_details)

            init {
                itemView.setOnClickListener {
                    val pos = adapterPosition
                    if (pos != RecyclerView.NO_POSITION && pos < filteredFolderList.size) {
                        val item = filteredFolderList[pos]
                        navigateTo(item.file)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_folder_picker, parent, false)
            return FolderViewHolder(view)
        }

        override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
            val item = filteredFolderList[position]
            holder.tvFolderName.text = item.name

            val countText = if (item.subfolderCount == 1) "1 subfolder" else "${item.subfolderCount} subfolders"
            val dateText = if (item.lastModified > 0) dateFormat.format(Date(item.lastModified)) else ""
            holder.tvFolderDetails.text = if (dateText.isNotEmpty()) "$countText • $dateText" else countText
        }

        override fun getItemCount(): Int = filteredFolderList.size
    }
}
