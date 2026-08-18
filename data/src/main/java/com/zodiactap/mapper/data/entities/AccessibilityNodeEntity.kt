package com.zodiactap.mapper.data.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zodiactap.mapper.common.utils.NodeInteractionType
import com.zodiactap.mapper.data.db.dao.AccessibilityNodeDao.Companion.KEY_ACTIONS
import com.zodiactap.mapper.data.db.dao.AccessibilityNodeDao.Companion.KEY_CLASS_NAME
import com.zodiactap.mapper.data.db.dao.AccessibilityNodeDao.Companion.KEY_CONTENT_DESCRIPTION
import com.zodiactap.mapper.data.db.dao.AccessibilityNodeDao.Companion.KEY_HINT
import com.zodiactap.mapper.data.db.dao.AccessibilityNodeDao.Companion.KEY_ID
import com.zodiactap.mapper.data.db.dao.AccessibilityNodeDao.Companion.KEY_INTERACTED
import com.zodiactap.mapper.data.db.dao.AccessibilityNodeDao.Companion.KEY_PACKAGE_NAME
import com.zodiactap.mapper.data.db.dao.AccessibilityNodeDao.Companion.KEY_TEXT
import com.zodiactap.mapper.data.db.dao.AccessibilityNodeDao.Companion.KEY_TOOLTIP
import com.zodiactap.mapper.data.db.dao.AccessibilityNodeDao.Companion.KEY_UNIQUE_ID
import com.zodiactap.mapper.data.db.dao.AccessibilityNodeDao.Companion.KEY_VIEW_RESOURCE_ID
import com.zodiactap.mapper.data.db.dao.AccessibilityNodeDao.Companion.TABLE_NAME
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
@Entity(tableName = TABLE_NAME)
data class AccessibilityNodeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = KEY_ID)
    val id: Long = 0L,

    @ColumnInfo(name = KEY_PACKAGE_NAME)
    val packageName: String,

    @ColumnInfo(name = KEY_TEXT)
    val text: String?,

    @ColumnInfo(name = KEY_CONTENT_DESCRIPTION)
    val contentDescription: String?,

    @ColumnInfo(name = KEY_CLASS_NAME)
    val className: String?,

    @ColumnInfo(name = KEY_VIEW_RESOURCE_ID)
    val viewResourceId: String?,

    @ColumnInfo(name = KEY_UNIQUE_ID)
    val uniqueId: String?,

    @ColumnInfo(name = KEY_ACTIONS)
    val actions: Set<NodeInteractionType>,

    /**
     * Whether the user interacted with this node.
     */
    @ColumnInfo(name = KEY_INTERACTED, defaultValue = false.toString())
    val interacted: Boolean,

    @ColumnInfo(name = KEY_TOOLTIP, defaultValue = "NULL")
    val tooltip: String?,

    @ColumnInfo(name = KEY_HINT, defaultValue = "NULL")
    val hint: String?,
) : Parcelable
