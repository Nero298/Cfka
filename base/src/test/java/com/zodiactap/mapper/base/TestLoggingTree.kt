package com.zodiactap.mapper.base

import timber.log.Timber

class TestLoggingTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        t?.printStackTrace()
        println(message)
    }
}
