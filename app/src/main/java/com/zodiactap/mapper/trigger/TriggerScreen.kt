package com.zodiactap.mapper.trigger

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zodiactap.mapper.base.trigger.BaseTriggerScreen
import com.zodiactap.mapper.base.trigger.TriggerDiscoverScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TriggerScreen(modifier: Modifier = Modifier, viewModel: ConfigTriggerViewModel) {
    val showFingerprintGestures: Boolean by
        viewModel.showFingerprintGesturesShortcut.collectAsStateWithLifecycle()

    BaseTriggerScreen(modifier, viewModel, discoverScreenContent = {
        TriggerDiscoverScreen(
            showFloatingButtons = true,
            showFingerprintGestures = showFingerprintGestures,
            onShortcutClick = viewModel::showTriggerSetup,
        )
    })
}
