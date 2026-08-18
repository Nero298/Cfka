package com.zodiactap.mapper.base.actions.sound

import com.zodiactap.mapper.common.utils.KMError
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.common.utils.Success
import com.zodiactap.mapper.system.files.FileAdapter
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

class ChooseSoundFileUseCaseImpl @Inject constructor(
    private val fileAdapter: FileAdapter,
    private val soundsManager: SoundsManager,
) : ChooseSoundFileUseCase {
    override val soundFiles = soundsManager.soundFiles

    override suspend fun saveSound(uri: String): KMResult<String> = soundsManager.saveNewSound(uri)

    override fun getSoundFileName(uri: String): KMResult<String> {
        val name = fileAdapter.getFileFromUri(uri).name

        return if (name == null) {
            KMError.NoFileName
        } else {
            Success(name)
        }
    }
}

interface ChooseSoundFileUseCase {

    /**
     * @return the sound file uid
     */
    suspend fun saveSound(uri: String): KMResult<String>
    val soundFiles: StateFlow<List<SoundFileInfo>>
    fun getSoundFileName(uri: String): KMResult<String>
}
