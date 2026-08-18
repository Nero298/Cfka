package com.zodiactap.mapper.base.keymaps

import android.graphics.drawable.Drawable
import dagger.hilt.android.scopes.ViewModelScoped
import com.zodiactap.mapper.base.actions.DisplayActionUseCase
import com.zodiactap.mapper.base.actions.GetActionErrorUseCase
import com.zodiactap.mapper.base.constraints.DisplayConstraintUseCase
import com.zodiactap.mapper.base.constraints.GetConstraintErrorUseCase
import com.zodiactap.mapper.base.input.EvdevDevicesDelegate
import com.zodiactap.mapper.base.purchasing.PurchasingError.ProductNotPurchased
import com.zodiactap.mapper.base.purchasing.PurchasingManager
import com.zodiactap.mapper.base.purchasing.RevenueCatEntitlementId
import com.zodiactap.mapper.base.system.inputmethod.KeyMapperImeHelper
import com.zodiactap.mapper.base.system.inputmethod.SwitchImeInterface
import com.zodiactap.mapper.base.trigger.TriggerError
import com.zodiactap.mapper.base.trigger.TriggerErrorSnapshot
import com.zodiactap.mapper.base.utils.navigation.NavDestination
import com.zodiactap.mapper.base.utils.navigation.NavigationProvider
import com.zodiactap.mapper.base.utils.navigation.navigate
import com.zodiactap.mapper.common.BuildConfigProvider
import com.zodiactap.mapper.common.models.EvdevDeviceInfo
import com.zodiactap.mapper.common.utils.KMError
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.common.utils.State
import com.zodiactap.mapper.common.utils.Success
import com.zodiactap.mapper.common.utils.dataOrNull
import com.zodiactap.mapper.common.utils.otherwise
import com.zodiactap.mapper.common.utils.then
import com.zodiactap.mapper.common.utils.valueIfFailure
import com.zodiactap.mapper.data.Keys
import com.zodiactap.mapper.data.repositories.PreferenceRepository
import com.zodiactap.mapper.sysbridge.manager.SystemBridgeConnectionManager
import com.zodiactap.mapper.sysbridge.manager.SystemBridgeConnectionState
import com.zodiactap.mapper.sysbridge.utils.SystemBridgeError
import com.zodiactap.mapper.system.SystemError.ImeDisabled
import com.zodiactap.mapper.system.SystemError.PermissionDenied
import com.zodiactap.mapper.system.accessibility.AccessibilityServiceAdapter
import com.zodiactap.mapper.system.apps.PackageManagerAdapter
import com.zodiactap.mapper.system.inputmethod.InputMethodAdapter
import com.zodiactap.mapper.system.permissions.Permission
import com.zodiactap.mapper.system.permissions.PermissionAdapter
import com.zodiactap.mapper.system.ringtones.RingtoneAdapter
import com.zodiactap.mapper.system.shizuku.ShizukuUtils
import javax.inject.Inject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withTimeout

@ViewModelScoped
class DisplayKeyMapUseCaseImpl @Inject constructor(
    private val permissionAdapter: PermissionAdapter,
    private val switchImeInterface: SwitchImeInterface,
    private val inputMethodAdapter: InputMethodAdapter,
    private val packageManagerAdapter: PackageManagerAdapter,
    private val settingsRepository: PreferenceRepository,
    private val accessibilityServiceAdapter: AccessibilityServiceAdapter,
    private val purchasingManager: PurchasingManager,
    private val ringtoneAdapter: RingtoneAdapter,
    private val getActionErrorUseCase: GetActionErrorUseCase,
    private val getConstraintErrorUseCase: GetConstraintErrorUseCase,
    private val buildConfigProvider: BuildConfigProvider,
    private val navigationProvider: NavigationProvider,
    private val systemBridgeConnectionManager: SystemBridgeConnectionManager,
    private val grabbedEvdevDeviceCache: EvdevDevicesDelegate,
) : DisplayKeyMapUseCase,
    GetActionErrorUseCase by getActionErrorUseCase,
    GetConstraintErrorUseCase by getConstraintErrorUseCase {
    private val keyMapperImeHelper =
        KeyMapperImeHelper(switchImeInterface, inputMethodAdapter, buildConfigProvider.packageName)

    private val showDpadImeSetupError: Flow<Boolean> =
        settingsRepository.get(Keys.neverShowDpadImeTriggerError).map { neverShow ->
            if (neverShow == null) {
                true
            } else {
                !neverShow
            }
        }

    /**
     * This waits for the purchases to be processed with a timeout so the UI doesn't
     * say there are no purchases while it is loading.
     */
    private val purchasesFlow: Flow<State<KMResult<Set<RevenueCatEntitlementId>>>> = callbackFlow {
        try {
            val value = withTimeout(5000L) {
                purchasingManager.entitlements.filterIsInstance<
                    State.Data<KMResult<Set<RevenueCatEntitlementId>>>,
                    >()
                    .first()
            }

            send(value)
        } catch (_: TimeoutCancellationException) {
        }

        purchasingManager.entitlements.collect(this::send)
    }

    private val systemBridgeConnectionState: Flow<SystemBridgeConnectionState?> =
        systemBridgeConnectionManager.connectionState

    private val evdevDevices: Flow<List<EvdevDeviceInfo>?> =
        grabbedEvdevDeviceCache.allDevices

    /**
     * Cache the data required for checking errors to reduce the latency of repeatedly checking
     * the errors.
     */
    override val triggerErrorSnapshot: Flow<TriggerErrorSnapshot> = combine(
        merge(
            permissionAdapter.onPermissionsUpdate.onStart { emit(Unit) },
            inputMethodAdapter.chosenIme,
        ),
        purchasesFlow,
        showDpadImeSetupError,
        systemBridgeConnectionState,
        evdevDevices,
    ) { _, purchases, showDpadImeSetupError, sysBridgeState, evdevDevices ->
        TriggerErrorSnapshot(
            isKeyMapperImeChosen = keyMapperImeHelper.isCompatibleImeChosen(),
            isDndAccessGranted = permissionAdapter.isGranted(Permission.ACCESS_NOTIFICATION_POLICY),
            isRootGranted = permissionAdapter.isGranted(Permission.ROOT),
            purchases = purchases.dataOrNull() ?: Success(emptySet()),
            showDpadImeSetupError = showDpadImeSetupError,
            isSystemBridgeConnected = sysBridgeState is SystemBridgeConnectionState.Connected,
            evdevDevices = evdevDevices,
        )
    }

    override val showDeviceDescriptors: Flow<Boolean> =
        settingsRepository.get(Keys.showDeviceDescriptors).map { it == true }

    override fun neverShowDpadImeSetupError() {
        settingsRepository.set(Keys.neverShowDpadImeTriggerError, true)
    }

    override suspend fun isFloatingButtonsPurchased(): Boolean {
        return purchasingManager.hasEntitlement(RevenueCatEntitlementId.FLOATING_BUTTONS)
            .valueIfFailure { false }
    }

    override suspend fun fixTriggerError(error: TriggerError) {
        when (error) {
            TriggerError.DND_ACCESS_DENIED -> fixError(
                PermissionDenied(Permission.ACCESS_NOTIFICATION_POLICY),
            )

            TriggerError.CANT_DETECT_IN_PHONE_CALL -> fixError(
                KMError.CantDetectKeyEventsInPhoneCall,
            )

            TriggerError.ASSISTANT_TRIGGER_NOT_PURCHASED -> fixError(
                ProductNotPurchased(
                    RevenueCatEntitlementId.ASSISTANT_TRIGGER,
                ),
            )

            TriggerError.DPAD_IME_NOT_SELECTED -> fixError(KMError.DpadTriggerImeNotSelected)

            TriggerError.FLOATING_BUTTONS_NOT_PURCHASED -> fixError(
                ProductNotPurchased(
                    RevenueCatEntitlementId.FLOATING_BUTTONS,
                ),
            )

            TriggerError.PURCHASE_VERIFICATION_FAILED -> purchasingManager.refresh()

            TriggerError.SYSTEM_BRIDGE_DISCONNECTED -> fixError(SystemBridgeError.Disconnected)

            TriggerError.EVDEV_DEVICE_NOT_FOUND,
            TriggerError.FLOATING_BUTTON_DELETED,
            TriggerError.SYSTEM_BRIDGE_UNSUPPORTED,
            TriggerError.MIGRATE_SCREEN_OFF_TRIGGER,
                -> {
            }
        }
    }

    override fun getAppName(packageName: String): KMResult<String> =
        packageManagerAdapter.getAppName(packageName)

    override fun getAppIcon(packageName: String): KMResult<Drawable> =
        packageManagerAdapter.getAppIcon(packageName)

    override fun getInputMethodLabel(imeId: String): KMResult<String> =
        inputMethodAdapter.getInfoById(imeId).then {
            Success(it.label)
        }

    override suspend fun fixError(error: KMError) {
        when (error) {
            is KMError.AppDisabled -> packageManagerAdapter.enableApp(error.packageName)

            is KMError.AppNotFound -> packageManagerAdapter.downloadApp(error.packageName)

            KMError.NoCompatibleImeChosen ->
                keyMapperImeHelper.chooseCompatibleInputMethod().otherwise {
                    inputMethodAdapter.showImePicker(fromForeground = true)
                }

            KMError.NoCompatibleImeEnabled -> keyMapperImeHelper.enableCompatibleInputMethod()

            is ImeDisabled -> switchImeInterface.enableIme(error.ime.id)

            is PermissionDenied -> permissionAdapter.request(error.permission)

            is KMError.ShizukuNotStarted -> packageManagerAdapter.openApp(
                ShizukuUtils.SHIZUKU_PACKAGE,
            )

            is KMError.CantDetectKeyEventsInPhoneCall -> {
                if (!keyMapperImeHelper.isCompatibleImeEnabled()) {
                    keyMapperImeHelper.enableCompatibleInputMethod()
                }

                // wait for compatible ime to be enabled then choose it.
                keyMapperImeHelper.isCompatibleImeEnabledFlow.first { it }

                keyMapperImeHelper.chooseCompatibleInputMethod().otherwise {
                    inputMethodAdapter.showImePicker(fromForeground = true)
                }
            }

            is SystemBridgeError.Disconnected -> navigationProvider.navigate(
                "fix_system_bridge",
                NavDestination.ExpertMode,
            )

            is KMError.DpadTriggerImeNotSelected -> {
                if (keyMapperImeHelper.isCompatibleImeEnabled()) {
                    keyMapperImeHelper.chooseCompatibleInputMethod()
                } else {
                    keyMapperImeHelper.enableCompatibleInputMethod()
                }
            }

            else -> Unit
        }
    }

    override fun startAccessibilityService(): Boolean = accessibilityServiceAdapter.start()

    override fun restartAccessibilityService(): Boolean = accessibilityServiceAdapter.restart()

    override fun neverShowDndTriggerError() {
        settingsRepository.set(Keys.neverShowDndAccessError, true)
    }

    override fun getRingtoneLabel(uri: String): KMResult<String> {
        return ringtoneAdapter.getLabel(uri)
    }
}

interface DisplayKeyMapUseCase :
    DisplayActionUseCase,
    DisplayConstraintUseCase {

    val triggerErrorSnapshot: Flow<TriggerErrorSnapshot>
    suspend fun isFloatingButtonsPurchased(): Boolean
    suspend fun fixTriggerError(error: TriggerError)
    override val showDeviceDescriptors: Flow<Boolean>

    fun neverShowDpadImeSetupError()
}
