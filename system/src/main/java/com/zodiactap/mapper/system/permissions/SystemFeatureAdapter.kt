package com.zodiactap.mapper.system.permissions

interface SystemFeatureAdapter {
    fun hasSystemFeature(feature: String): Boolean
    fun getSystemFeatures(): List<String>
}
