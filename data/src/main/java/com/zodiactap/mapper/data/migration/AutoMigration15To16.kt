package com.zodiactap.mapper.data.migration

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec

@DeleteColumn("keymaps", "folder_name")
class AutoMigration15To16 : AutoMigrationSpec
