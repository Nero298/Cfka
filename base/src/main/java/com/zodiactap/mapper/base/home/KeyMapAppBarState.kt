package com.zodiactap.mapper.base.home

import com.zodiactap.mapper.base.constraints.ConstraintMode
import com.zodiactap.mapper.base.groups.GroupListItemModel
import com.zodiactap.mapper.base.utils.ui.compose.ComposeChipModel

sealed class KeyMapAppBarState {
    data class RootGroup(
        val subGroups: List<GroupListItemModel> = emptyList(),
        val warnings: List<HomeWarningListItem> = emptyList(),
        val isPaused: Boolean = false,
    ) : KeyMapAppBarState()

    data class ChildGroup(
        val groupName: String,
        val constraints: List<ComposeChipModel>,
        val constraintMode: ConstraintMode,
        val parentConstraintCount: Int,
        val subGroups: List<GroupListItemModel>,
        val breadcrumbs: List<GroupListItemModel>,
        val isEditingGroupName: Boolean,
        val isNewGroup: Boolean,
        /**
         * If it is null then the Switch should be disabled.
         */
        val keyMapsEnabled: SelectedKeyMapsEnabled?,
    ) : KeyMapAppBarState()

    data class Selecting(
        val selectionCount: Int,
        val selectedKeyMapsEnabled: SelectedKeyMapsEnabled,
        val isAllSelected: Boolean,
        val groups: List<GroupListItemModel>,
        val breadcrumbs: List<GroupListItemModel>,
        val showThisGroup: Boolean,
    ) : KeyMapAppBarState()
}
