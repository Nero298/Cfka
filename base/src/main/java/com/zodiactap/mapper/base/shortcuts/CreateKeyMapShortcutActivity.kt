package com.zodiactap.mapper.base.shortcuts

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import com.zodiactap.mapper.base.compose.ComposeColors
import com.zodiactap.mapper.base.compose.KeyMapperTheme
import com.zodiactap.mapper.base.onboarding.OnboardingUseCase
import com.zodiactap.mapper.base.system.accessibility.AccessibilityServiceAdapterImpl
import com.zodiactap.mapper.base.system.permissions.RequestPermissionDelegate
import com.zodiactap.mapper.base.trigger.RecordTriggerControllerImpl
import com.zodiactap.mapper.base.utils.navigation.NavigationProvider
import com.zodiactap.mapper.base.utils.ui.ResourceProviderImpl
import com.zodiactap.mapper.base.utils.ui.launchRepeatOnLifecycle
import com.zodiactap.mapper.common.BuildConfigProvider
import com.zodiactap.mapper.system.notifications.NotificationReceiverAdapterImpl
import com.zodiactap.mapper.system.permissions.AndroidPermissionAdapter
import com.zodiactap.mapper.system.shizuku.ShizukuAdapter
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class CreateKeyMapShortcutActivity : AppCompatActivity() {

    @Inject
    lateinit var permissionAdapter: AndroidPermissionAdapter

    @Inject
    lateinit var serviceAdapter: AccessibilityServiceAdapterImpl

    @Inject
    lateinit var resourceProvider: ResourceProviderImpl

    @Inject
    lateinit var onboardingUseCase: OnboardingUseCase

    @Inject
    lateinit var recordTriggerController: RecordTriggerControllerImpl

    @Inject
    lateinit var notificationReceiverAdapter: NotificationReceiverAdapterImpl

    @Inject
    lateinit var shizukuAdapter: ShizukuAdapter

    @Inject
    lateinit var buildConfigProvider: BuildConfigProvider

    @Inject
    lateinit var navigationProvider: NavigationProvider

    private lateinit var requestPermissionDelegate: RequestPermissionDelegate

    private val viewModel by viewModels<CreateKeyMapShortcutViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                ComposeColors.surfaceContainerLight.toArgb(),
                ComposeColors.surfaceContainerDark.toArgb(),
            ),
            navigationBarStyle = SystemBarStyle.auto(
                ComposeColors.surfaceContainerLight.toArgb(),
                ComposeColors.surfaceContainerDark.toArgb(),
            ),
        )
        super.onCreate(savedInstanceState)

        setContent {
            KeyMapperTheme {
                CreateKeyMapShortcutScreen(
                    viewModel = viewModel,
                    finishActivity = { finish() },
                )
            }
        }

        requestPermissionDelegate = RequestPermissionDelegate(
            this,
            showDialogs = true,
            permissionAdapter,
            notificationReceiverAdapter = notificationReceiverAdapter,
            buildConfigProvider = buildConfigProvider,
            shizukuAdapter = shizukuAdapter,
            navigationProvider = navigationProvider,
            coroutineScope = lifecycleScope,
        )

        launchRepeatOnLifecycle(Lifecycle.State.STARTED) {
            permissionAdapter.request
                .collectLatest { permission ->
                    requestPermissionDelegate.requestPermission(permission)
                }
        }

        launchRepeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.returnIntentResult.collectLatest { intent ->
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}
