package com.zodiactap.mapper.base.expertmode

import com.zodiactap.mapper.sysbridge.service.SystemBridgeSetupStep

data class ExpertModeSetupState(
    val stepNumber: Int,
    val stepCount: Int,
    val step: SystemBridgeSetupStep,
    val stepContent: StepContent,
    val isSetupAssistantChecked: Boolean,
    val isSetupAssistantButtonEnabled: Boolean,
    val isStarting: Boolean,
)
