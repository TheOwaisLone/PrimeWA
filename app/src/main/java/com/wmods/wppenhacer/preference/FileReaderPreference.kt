package com.wmods.wppenhacer.preference

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wmods.wppenhacer.R
import com.wmods.wppenhacer.utils.FilePicker
import com.wmods.wppenhacer.utils.RealPathUtil
import com.wmods.wppenhacer.xposed.utils.Utils
import org.w3c.dom.Document
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class FileReaderPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : Preference(context, attrs, defStyleAttr, defStyleRes),
    Preference.OnPreferenceClickListener,
    FilePicker.OnFilePickedListener,
    FilePicker.OnUriPickedListener {

    private val xmlMimeType = arrayOf("text/xml", "application/xml", "text/plain", "*/*")
    private var xmlContent: String? = null
    private var filePath: String? = null

    init {
        init(context)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun showAlertPermission() {
        MaterialAlertDialogBuilder(context).apply {
            setTitle(R.string.storage_permission)
            setMessage(R.string.permission_storage)
            setPositiveButton(R.string.allow) { _, _ ->
                val intent = Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
            setNegativeButton(R.string.deny) { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            showAlertPermission()
            return true
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                (context as? Activity)?.requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1)
                return true
            }
        } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            (context as? Activity)?.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            return true
        }

        FilePicker.setOnFilePickedListener(this)
        FilePicker.setOnUriPickedListener(this)
        FilePicker.fileCapture.launch(xmlMimeType)
        return true
    }

    override fun onFilePicked(file: File) {
        if (!file.canRead()) {
            Toast.makeText(context, R.string.unable_to_read_this_file, Toast.LENGTH_SHORT).show()
            return
        }
        processXmlFile(file)
    }

    override fun onUriPicked(uri: Uri) {
        Utils.executor.execute {
            try {
                val realPath = RealPathUtil.getRealFilePath(context, uri)
                if (realPath != null) {
                    val file = File(realPath)
                    processXmlFileInBg(file)
                } else {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    if (inputStream != null) {
                        processXmlStreamInBg(inputStream, uri.lastPathSegment ?: "XML")
                    }
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Error processing XML file: " + e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun processXmlFile(file: File) {
        Utils.executor.execute {
            processXmlFileInBg(file)
        }
    }

    private fun processXmlFileInBg(file: File) {
        try {
            FileInputStream(file).use { fis ->
                processXmlStreamInBg(fis, file.absolutePath)
            }
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "Error reading XML file: " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processXmlStreamInBg(inputStream: InputStream, path: String) {
        try {
            val dbFactory = DocumentBuilderFactory.newInstance().apply {
                isExpandEntityReferences = false
            }
            val dBuilder = dbFactory.newDocumentBuilder()
            val doc: Document = dBuilder.parse(inputStream)
            doc.documentElement.normalize()

            val transformerFactory = TransformerFactory.newInstance()
            val transformer = transformerFactory.newTransformer()
            val source = DOMSource(doc)
            val writer = StringWriter()
            val result = StreamResult(writer)
            transformer.transform(source, result)

            val content = writer.toString()

            Handler(Looper.getMainLooper()).post {
                this.xmlContent = content
                this.filePath = path

                sharedPreferences?.edit()
                    ?.putString(key, content)
                    ?.putString(key + "_path", path)
                    ?.apply()
                val fileName = File(path).name
                summary = "$fileName\n$path"
                Toast.makeText(context, "Keybox XML loaded: $fileName", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "Error parsing XML: " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBindViewHolder(holder: androidx.preference.PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val summaryView = holder.findViewById(android.R.id.summary) as? android.widget.TextView
        summaryView?.maxLines = 2
        summaryView?.ellipsize = android.text.TextUtils.TruncateAt.MIDDLE
    }

    private fun init(context: Context) {
        onPreferenceClickListener = this
        isIconSpaceReserved = false

        val savedXml = PreferenceManager.getDefaultSharedPreferences(context).getString(key, null)
        val savedPath = PreferenceManager.getDefaultSharedPreferences(context).getString(key + "_path", null)
        if (savedPath != null) {
            filePath = savedPath
            val fileName = File(savedPath).name
            summary = "$fileName\n$savedPath"
        } else if (savedXml != null) {
            xmlContent = savedXml
            summary = context.getString(R.string.custom_keybox_file_sum)
        } else {
            summary = context.getString(R.string.custom_keybox_file_sum)
        }
    }
}
