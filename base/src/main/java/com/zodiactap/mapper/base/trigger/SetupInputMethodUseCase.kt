package com.zodiactap.mapper.base.trigger

import android.os.Build
import dagger.hilt.android.scopes.ViewModelScoped
import com.zodiactap.mapper.base.system.inputmethod.KeyMapperImeHelper
import com.zodiactap.mapper.base.system.inputmethod.SwitchImeInterface
import com.zodiactap.mapper.common.BuildConfigProvider
import com.zodiactap.mapper.common.utils.KMError
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.common.utils.onFailure
import com.zodiactap.mapper.common.utils.suspendThen
import com.zodiactap.mapper.system.inputmethod.InputMethodAdapter
import javax.inject.Inject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import timber.log.Timber

@ViewModelScoped
class SetupInputMethodUseCaseImpl @Inject constructor(
    private val switchImeInterface: SwitchImeInterface,
    private val inputMethodAdapter: InputMethodAdapter,
    private val buildConfigProvider: BuildConfigProvider,
) : SetupInputMethodUseCase {
    private val keyMapperImeHelper =
        KeyMapperImeHelper(switchImeInterface, inputMethodAdapter, buildConfigProvider.packageName)

    override val isEnabled: Flow<Boolean> = keyMapperImeHelper.isCompatibleImeEnabledFlow

    override suspend fun enableInputMethod(): KMResult<Unit> {
        return keyMapperImeHelper.enableCompatibleInputMethod()
    }

    override val isChosen: Flow<Boolean> = keyMapperImeHelper.isCompatibleImeChosenFlow

    override suspend fun chooseInputMethod(): KMResult<String> {
        // On Android 13+, the accessibility service can enable the input method without
        // any user input
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return keyMapperImeHelper.enableCompatibleInputMethod()
                .onFailure {
                    Timber.e("Failed to enable compatible input method: $it")
                }
                .suspendThen {
                    // Wait for the ime to be enabled asynchronously.
                    try {
                        withTimeout(3000L) {
                            isEnabled.first { it }
                            keyMapperImeHelper.chooseCompatibleInputMethod()
                        }
                    } catch (e: TimeoutCancellationException) {
                        KMError.Exception(e)
                    }
                }
        } else {
            return keyMapperImeHelper.chooseCompatibleInputMethod()
        }
    }
}

interface SetupInputMethodUseCase {
    val isEnabled: Flow<Boolean>
    suspend fun enableInputMethod(): KMResult<Unit>

    val isChosen: Flow<Boolean>
    suspend fun chooseInputMethod(): KMResult<String>
}
