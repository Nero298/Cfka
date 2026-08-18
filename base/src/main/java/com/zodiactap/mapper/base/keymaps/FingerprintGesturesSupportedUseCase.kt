package com.zodiactap.mapper.base.keymaps

import android.os.Build
import com.zodiactap.mapper.data.Keys
import com.zodiactap.mapper.data.repositories.PreferenceRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class FingerprintGesturesSupportedUseCaseImpl @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
) : FingerprintGesturesSupportedUseCase {
    override val isSupported: Flow<Boolean?> =
        preferenceRepository.get(Keys.fingerprintGesturesAvailable).map {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return@map false

            it
        }

    override fun setSupported(supported: Boolean) {
        preferenceRepository.set(Keys.fingerprintGesturesAvailable, supported)
    }
}

interface FingerprintGesturesSupportedUseCase {
    /**
     * Is null if support is unknown
     */
    val isSupported: Flow<Boolean?>

    fun setSupported(supported: Boolean)
}
