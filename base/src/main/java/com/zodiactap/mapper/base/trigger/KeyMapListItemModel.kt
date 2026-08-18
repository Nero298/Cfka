package com.zodiactap.mapper.base.trigger

import androidx.compose.ui.graphics.vector.ImageVector
import com.zodiactap.mapper.base.constraints.ConstraintMode
import com.zodiactap.mapper.base.utils.ui.compose.ComposeChipModel

data class KeyMapListItemModel(val isSelected: Boolean, val content: Content) {
    val uid = content.uid

    data class Content(
        val uid: String,
        val triggerKeys: List<String>,
        val triggerErrors: List<TriggerError>,
        val triggerSeparatorIcon: ImageVector,
        val actions: List<ComposeChipModel>,
        val constraintMode: ConstraintMode,
        val constraints: List<ComposeChipModel>,
        val options: List<String>,
        val extraInfo: String?,
    )
}
