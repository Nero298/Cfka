package com.zodiactap.mapper.base.purchasing

import com.zodiactap.mapper.common.utils.KMError

sealed class PurchasingError : KMError() {
    data object PurchasingNotImplemented : PurchasingError()

    data class ProductNotPurchased(val entitlement: RevenueCatEntitlementId) : PurchasingError()

    sealed class PurchasingProcessError : PurchasingError() {
        data object ProductNotFound : PurchasingProcessError()
        data object Cancelled : PurchasingProcessError()
        data object StoreProblem : PurchasingProcessError()
        data object NetworkError : PurchasingProcessError()
        data object PaymentPending : PurchasingProcessError()
        data object PurchaseInvalid : PurchasingProcessError()
        data object EntitlementNotGrantedAfterPurchase : PurchasingProcessError()
        data class Unexpected(val message: String) : PurchasingProcessError()
    }
}
