package com.wmods.wppenhacer.model

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import java.io.File
import java.util.regex.Pattern

/**
 * Model class representing a call recording with metadata and app source.
 */
data class Recording(
    val file: File
) {
    enum class AppSource(val label: String, val shortTag: String) {
        WHATSAPP("WhatsApp", "WA"),
        WA_BUSINESS("WA Business", "W4B")
    }

    var contactName: String = "Unknown"
        private set
    var duration: Long = 0
        private set
    var appSource: AppSource = AppSource.WHATSAPP
        private set

    val date: Long = file.lastModified()
    val size: Long = file.length()

    init {
        extractMetadata()
        parseDuration()
    }

    private fun extractMetadata() {
        val filename = file.name
        val fullPath = file.absolutePath.lowercase()
        val parentName = file.parentFile?.name?.lowercase() ?: ""

        val matcher = RECORDING_PATTERN.matcher(filename)
        if (matcher.matches()) {
            val tag = matcher.group(1)
            val extractedName = matcher.group(2)

            contactName = if (!extractedName.isNullOrEmpty()) extractedName else "Unknown"

            appSource = when {
                tag?.equals("W4B", ignoreCase = true) == true -> AppSource.WA_BUSINESS
                tag?.equals("WA", ignoreCase = true) == true -> AppSource.WHATSAPP
                fullPath.contains("w4b") || fullPath.contains("business") || parentName.contains("business") -> AppSource.WA_BUSINESS
                else -> AppSource.WHATSAPP
            }
        } else {
            var raw = filename.substringBeforeLast(".")
            if (raw.startsWith("Call_", ignoreCase = true)) {
                raw = raw.substring(5)
            }
            raw = raw.replace(Regex("_\\d{8}_\\d{6}$"), "")
            raw = raw.replace(Regex("^(WA|W4B)_", RegexOption.IGNORE_CASE), "")
            contactName = raw.trim().ifEmpty { "Unknown" }
            appSource = when {
                fullPath.contains("w4b") || fullPath.contains("business") || parentName.contains("business") -> AppSource.WA_BUSINESS
                else -> AppSource.WHATSAPP
            }
        }
    }

    private fun parseDuration() {
        if (!file.exists() || file.length() == 0L) {
            duration = 0
            return
        }

        try {
            MediaMetadataRetriever().use { retriever ->
                retriever.setDataSource(file.absolutePath)
                val timeStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                duration = if (!timeStr.isNullOrEmpty()) {
                    timeStr.toLongOrNull() ?: 0L
                } else {
                    0L
                }
            }
        } catch (_: Exception) {
            duration = 0
        }
    }

    @SuppressLint("DefaultLocale")
    fun getFormattedDuration(): String {
        var seconds = duration / 1000
        var minutes = seconds / 60
        seconds %= 60

        if (minutes >= 60) {
            val hours = minutes / 60
            minutes %= 60
            return String.format("%d:%02d:%02d", hours, minutes, seconds)
        }
        return String.format("%d:%02d", minutes, seconds)
    }

    @SuppressLint("DefaultLocale")
    fun getFormattedSize(): String {
        if (size < 1024) return "$size B"
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0)
        return String.format("%.1f MB", size / (1024.0 * 1024.0))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Recording) return false
        return file == other.file
    }

    override fun hashCode(): Int {
        return file.hashCode()
    }

    companion object {
        private val RECORDING_PATTERN = Pattern.compile("Call_(?:(WA|W4B)_)?(.+?)_\\d{8}_\\d{6}\\.(?:wav|m4a|aac|mp3)", Pattern.CASE_INSENSITIVE)
    }
}
