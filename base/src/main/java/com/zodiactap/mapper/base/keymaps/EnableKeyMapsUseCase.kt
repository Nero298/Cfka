package com.zodiactap.mapper.base.keymaps

import com.zodiactap.mapper.data.repositories.KeyMapRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnableKeyMapsUseCaseImpl @Inject constructor(private val keyMapRepository: KeyMapRepository) :
    EnableKeyMapsUseCase {

    override fun enable(uid: String) {
        keyMapRepository.enableById(uid)
    }

    override fun toggle(uid: String) {
        keyMapRepository.toggleById(uid)
    }

    override fun disable(uid: String) {
        keyMapRepository.disableById(uid)
    }
}

interface EnableKeyMapsUseCase {
    fun enable(uid: String)
    fun toggle(uid: String)
    fun disable(uid: String)
}
