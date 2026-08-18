package com.zodiactap.mapper.data
import com.zodiactap.mapper.common.utils.KMError
object DataError {
    data class ExtraNotFound(val extraId: String) : KMError()
}
