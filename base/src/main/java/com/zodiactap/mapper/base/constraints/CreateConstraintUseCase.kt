package com.zodiactap.mapper.base.constraints

import android.content.pm.PackageManager
import android.os.Build
import com.zodiactap.mapper.common.utils.KMError
import com.zodiactap.mapper.common.utils.SizeKM
import com.zodiactap.mapper.data.Keys
import com.zodiactap.mapper.data.repositories.PreferenceRepository
import com.zodiactap.mapper.system.camera.CameraAdapter
import com.zodiactap.mapper.system.camera.CameraLens
import com.zodiactap.mapper.system.display.DisplayAdapter
import com.zodiactap.mapper.system.inputmethod.ImeInfo
import com.zodiactap.mapper.system.inputmethod.InputMethodAdapter
import com.zodiactap.mapper.system.network.NetworkAdapter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class CreateConstraintUseCaseImpl @Inject constructor(
    private val networkAdapter: NetworkAdapter,
    private val inputMethodAdapter: InputMethodAdapter,
    private val preferenceRepository: PreferenceRepository,
    private val cameraAdapter: CameraAdapter,
    private val displayAdapter: DisplayAdapter,
) : CreateConstraintUseCase {

    override fun isSupported(constraint: ConstraintId): KMError? {
        when (constraint) {
            ConstraintId.FLASHLIGHT_ON, ConstraintId.FLASHLIGHT_OFF -> {
                if (cameraAdapter.getFlashInfo(CameraLens.BACK) == null &&
                    cameraAdapter.getFlashInfo(CameraLens.FRONT) == null
                ) {
                    return KMError.SystemFeatureNotSupported(PackageManager.FEATURE_CAMERA_FLASH)
                }
            }

            ConstraintId.HINGE_CLOSED, ConstraintId.HINGE_OPEN -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    return KMError.SdkVersionTooLow(Build.VERSION_CODES.R)
                }
            }

            else -> Unit
        }

        return null
    }

    override fun getKnownWiFiSSIDs(): List<String> = networkAdapter.getKnownWifiSSIDs()

    override fun getEnabledInputMethods(): List<ImeInfo> = inputMethodAdapter.inputMethods.value

    override suspend fun saveWifiSSID(ssid: String) {
        val savedWifiSSIDsList = getSavedWifiSSIDs().first().toMutableList()

        if (!savedWifiSSIDsList.contains(ssid)) {
            if (savedWifiSSIDsList.size == 3) {
                savedWifiSSIDsList.removeAt(savedWifiSSIDsList.lastIndex)
            }

            if (savedWifiSSIDsList.isEmpty()) {
                savedWifiSSIDsList.add(ssid)
            } else {
                savedWifiSSIDsList.add(0, ssid)
            }
        }

        preferenceRepository.set(
            Keys.savedWifiSSIDs,
            savedWifiSSIDsList.toSet(),
        )
    }

    override fun getSavedWifiSSIDs(): Flow<List<String>> =
        preferenceRepository.get(Keys.savedWifiSSIDs)
            .map { it?.toList() ?: emptyList() }

    override fun getFlashlightLenses(): Set<CameraLens> {
        return CameraLens.entries.filter { cameraAdapter.getFlashInfo(it) != null }.toSet()
    }

    override fun getSupportedResolutions(): Set<SizeKM> = displayAdapter.supportedResolutions.value

    override fun getCurrentResolution(): SizeKM = displayAdapter.size
}

interface CreateConstraintUseCase {
    fun isSupported(constraint: ConstraintId): KMError?
    fun getKnownWiFiSSIDs(): List<String>
    fun getEnabledInputMethods(): List<ImeInfo>

    suspend fun saveWifiSSID(ssid: String)
    fun getSavedWifiSSIDs(): Flow<List<String>>

    fun getFlashlightLenses(): Set<CameraLens>

    fun getSupportedResolutions(): Set<SizeKM>
    fun getCurrentResolution(): SizeKM
}
