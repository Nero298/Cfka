package com.zodiactap.mapper.base.backup

import com.zodiactap.mapper.system.files.FileUtils

object BackupUtils {

    const val DEFAULT_AUTOMATIC_BACKUP_NAME = "key_mapper.zip"

    fun createBackupFileName(): String {
        val formattedDate = FileUtils.createFileDate()
        return "key_maps_$formattedDate.zip"
    }
}
