package com.zodiactap.mapper.base.utils.ui.compose

sealed class ComposeChipModel {
    abstract val id: String
    abstract val text: String

    data class Normal(
        override val id: String,
        val icon: ComposeIconInfo?,
        override val text: String,
    ) : ComposeChipModel()

    data class Error(
        override val id: String,
        override val text: String,
        val error: com.zodiactap.mapper.common.utils.KMError,
        val isFixable: Boolean = true,
    ) : ComposeChipModel()
}
