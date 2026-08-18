package com.zodiactap.mapper.common.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class EvdevDeviceInfo(val name: String, val bus: Int, val vendor: Int, val product: Int) :
    Parcelable
