package com.zodiactap.mapper.base.actions

import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.system.accessibility.AccessibilityServiceAdapter
import javax.inject.Inject

class TestActionUseCaseImpl @Inject constructor(
    private val serviceAdapter: AccessibilityServiceAdapter,
) : TestActionUseCase {
    override suspend fun invoke(action: ActionData): KMResult<*> =
        serviceAdapter.send(TestActionEvent(action))
}

interface TestActionUseCase {
    suspend operator fun invoke(action: ActionData): KMResult<*>
}
