package com.zodiactap.mapper.base.onboarding

data class OnboardingTipModel(
    val id: String,
    val title: String,
    val message: String,
    val isDismissable: Boolean,
    val buttonText: String? = null,
)
