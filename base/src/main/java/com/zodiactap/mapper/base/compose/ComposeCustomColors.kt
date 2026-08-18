package com.zodiactap.mapper.base.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.zodiactap.mapper.base.compose.ComposeColors.amberContainerDark
import com.zodiactap.mapper.base.compose.ComposeColors.amberContainerLight
import com.zodiactap.mapper.base.compose.ComposeColors.amberDark
import com.zodiactap.mapper.base.compose.ComposeColors.amberLight
import com.zodiactap.mapper.base.compose.ComposeColors.onAmberContainerDark
import com.zodiactap.mapper.base.compose.ComposeColors.onAmberContainerLight
import com.zodiactap.mapper.base.compose.ComposeColors.onAmberDark
import com.zodiactap.mapper.base.compose.ComposeColors.onAmberLight
import com.zodiactap.mapper.base.compose.ComposeColors.onOrangeContainerDark
import com.zodiactap.mapper.base.compose.ComposeColors.onOrangeContainerLight
import com.zodiactap.mapper.base.compose.ComposeColors.onOrangeDark
import com.zodiactap.mapper.base.compose.ComposeColors.onOrangeLight
import com.zodiactap.mapper.base.compose.ComposeColors.orangeContainerDark
import com.zodiactap.mapper.base.compose.ComposeColors.orangeContainerLight
import com.zodiactap.mapper.base.compose.ComposeColors.orangeDark
import com.zodiactap.mapper.base.compose.ComposeColors.orangeLight
import com.zodiactap.mapper.base.compose.ComposeColors.primaryContainerDarkerDark
import com.zodiactap.mapper.base.compose.ComposeColors.primaryContainerDarkerLight

/**
 * Stores the custom colors in a palette that changes
 * depending on the light/dark theme. A CompositionLocalProvider
 * is used in the KeyMapperTheme to provide the correct palette in a similar
 * way to how MaterialTheme.current works.
 */
@Immutable
data class ComposeCustomColors(
    val red: Color = Color.Unspecified,
    val onRed: Color = Color.Unspecified,
    val green: Color = Color.Unspecified,
    val onGreen: Color = Color.Unspecified,
    val greenContainer: Color = Color.Unspecified,
    val onGreenContainer: Color = Color.Unspecified,
    val magiskTeal: Color = Color.Unspecified,
    val onMagiskTeal: Color = Color.Unspecified,
    val shizukuBlue: Color = Color.Unspecified,
    val onShizukuBlue: Color = Color.Unspecified,
    val orange: Color = Color.Unspecified,
    val onOrange: Color = Color.Unspecified,
    val orangeContainer: Color = Color.Unspecified,
    val onOrangeContainer: Color = Color.Unspecified,
    val amber: Color = Color.Unspecified,
    val onAmber: Color = Color.Unspecified,
    val amberContainer: Color = Color.Unspecified,
    val onAmberContainer: Color = Color.Unspecified,
    val discord: Color = Color.Unspecified,
    val onDiscord: Color = Color.Unspecified,
    val primaryContainerDarker: Color = Color.Unspecified,
) {
    companion object {
        val LightPalette = ComposeCustomColors(
            red = ComposeColors.redLight,
            onRed = ComposeColors.onRedLight,
            green = ComposeColors.greenLight,
            onGreen = ComposeColors.onGreenLight,
            greenContainer = ComposeColors.greenContainerLight,
            onGreenContainer = ComposeColors.onGreenContainerLight,
            magiskTeal = ComposeColors.magiskTealLight,
            onMagiskTeal = ComposeColors.onMagiskTealLight,
            shizukuBlue = ComposeColors.shizukuBlueLight,
            onShizukuBlue = ComposeColors.onShizukuBlueLight,
            orange = orangeLight,
            onOrange = onOrangeLight,
            orangeContainer = orangeContainerLight,
            onOrangeContainer = onOrangeContainerLight,
            amber = amberLight,
            onAmber = onAmberLight,
            amberContainer = amberContainerLight,
            onAmberContainer = onAmberContainerLight,
            discord = ComposeColors.discordLight,
            onDiscord = ComposeColors.onDiscordLight,
            primaryContainerDarker = primaryContainerDarkerLight,
        )

        val DarkPalette = ComposeCustomColors(
            red = ComposeColors.redDark,
            onRed = ComposeColors.onRedDark,
            green = ComposeColors.greenDark,
            onGreen = ComposeColors.onGreenDark,
            greenContainer = ComposeColors.greenContainerDark,
            onGreenContainer = ComposeColors.onGreenContainerDark,
            magiskTeal = ComposeColors.magiskTealDark,
            onMagiskTeal = ComposeColors.onMagiskTealDark,
            shizukuBlue = ComposeColors.shizukuBlueDark,
            onShizukuBlue = ComposeColors.onShizukuBlueDark,
            orange = orangeDark,
            onOrange = onOrangeDark,
            orangeContainer = orangeContainerDark,
            onOrangeContainer = onOrangeContainerDark,
            amber = amberDark,
            onAmber = onAmberDark,
            amberContainer = amberContainerDark,
            onAmberContainer = onAmberContainerDark,
            discord = ComposeColors.discordDark,
            onDiscord = ComposeColors.onDiscordDark,
            primaryContainerDarker = primaryContainerDarkerDark,
        )
    }

    @Composable
    @Stable
    fun contentColorFor(color: Color): Color {
        return when (color) {
            red -> onRed
            green -> onGreen
            greenContainer -> onGreenContainer
            magiskTeal -> onMagiskTeal
            shizukuBlue -> onShizukuBlue
            amber -> onAmber
            amberContainer -> onAmberContainer
            discord -> onDiscord
            else -> MaterialTheme.colorScheme.contentColorFor(color)
        }
    }
}
