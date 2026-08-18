package com.zodiactap.mapper.base.keymaps

import com.zodiactap.mapper.base.overlay.OverlayLogoManager
import com.zodiactap.mapper.data.Keys
import com.zodiactap.mapper.data.repositories.PreferenceRepository
import com.zodiactap.mapper.system.media.MediaAdapter
import com.zodiactap.mapper.system.ringtones.RingtoneAdapter
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

@Singleton
class PauseKeyMapsUseCaseImpl @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val mediaAdapter: MediaAdapter,
    private val ringtoneAdapter: RingtoneAdapter,
    private val overlayLogoManager: OverlayLogoManager,
) : PauseKeyMapsUseCase {

    override val isPaused: Flow<Boolean> =
        preferenceRepository.get(Keys.mappingsPaused).map { it ?: false }

    override fun pause() {
        preferenceRepository.set(Keys.mappingsPaused, true)
        mediaAdapter.stopFileMedia()
        ringtoneAdapter.stopPlaying()
        Timber.d("Pause mappings")
    }

    override fun resume() {
        preferenceRepository.set(Keys.mappingsPaused, false)
        overlayLogoManager.showBriefly()
        Timber.d("Resume mappings")
    }
}

interface PauseKeyMapsUseCase {
    val isPaused: Flow<Boolean>
    fun pause()
    fun resume()
}
