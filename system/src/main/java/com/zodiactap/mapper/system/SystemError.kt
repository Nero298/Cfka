package com.zodiactap.mapper.system

import com.zodiactap.mapper.common.utils.KMError
import com.zodiactap.mapper.system.inputmethod.ImeInfo
import com.zodiactap.mapper.system.permissions.Permission

sealed class SystemError : KMError() {
    data class PermissionDenied(val permission: Permission) : KMError()
    data class ImeDisabled(val ime: ImeInfo) : KMError()
}
