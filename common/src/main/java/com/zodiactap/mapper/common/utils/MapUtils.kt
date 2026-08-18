package com.zodiactap.mapper.common.utils

/**
 * Not for high speed stuff.
 */
fun <K, V> Map<K, V>.getKey(value: V) = entries.firstOrNull { it.value == value }?.key
