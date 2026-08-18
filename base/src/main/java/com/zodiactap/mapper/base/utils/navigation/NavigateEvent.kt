package com.zodiactap.mapper.base.utils.navigation

import androidx.navigation.NavOptions

data class NavigateEvent(
    val key: String,
    val destination: NavDestination<*>,
    val navOptions: NavOptions? = null,
)
