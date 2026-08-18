package com.zodiactap.mapper.common

interface BuildConfigProvider {
    val minApi: Int
    val maxApi: Int
    val packageName: String
    val version: String
    val versionCode: Int
    val sdkInt: Int
}
