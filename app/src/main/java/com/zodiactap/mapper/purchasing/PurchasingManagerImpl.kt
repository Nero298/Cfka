package com.zodiactap.mapper.purchasing

import com.zodiactap.mapper.base.purchasing.PurchasingError
import com.zodiactap.mapper.base.purchasing.PurchasingManager
import com.zodiactap.mapper.base.purchasing.RevenueCatEntitlementId
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.common.utils.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class PurchasingManagerImpl : PurchasingManager {
    override val onCompleteProductPurchase: MutableSharedFlow<RevenueCatEntitlementId> =
        MutableSharedFlow()
    override val entitlements: Flow<State<KMResult<Set<RevenueCatEntitlementId>>>> =
        MutableStateFlow(State.Data(PurchasingError.PurchasingNotImplemented))

    override suspend fun launchPurchasingFlow(
        packageId: String,
        verifyEntitlements: Array<RevenueCatEntitlementId>,
    ): KMResult<Unit> {
        return PurchasingError.PurchasingNotImplemented
    }

    override suspend fun isPackagePurchased(packageId: String): KMResult<Boolean> {
        return PurchasingError.PurchasingNotImplemented
    }

    override suspend fun getNonSubscriptionPurchaseCount(packageId: String): KMResult<Int> {
        return PurchasingError.PurchasingNotImplemented
    }

    override suspend fun getPackagePrice(packageId: String): KMResult<String> {
        return PurchasingError.PurchasingNotImplemented
    }

    override suspend fun hasEntitlement(entitlement: RevenueCatEntitlementId): KMResult<Boolean> {
        return PurchasingError.PurchasingNotImplemented
    }

    override suspend fun getCurrentOfferingId(): KMResult<String?> {
        return PurchasingError.PurchasingNotImplemented
    }

    override suspend fun restorePurchases(): KMResult<Set<RevenueCatEntitlementId>> {
        return PurchasingError.PurchasingNotImplemented
    }

    override suspend fun getCustomerId(): KMResult<String> {
        return PurchasingError.PurchasingNotImplemented
    }

    override fun refresh() {}

    override fun trackCustomPaywallImpression(paywallIdentifier: String) {
        // Purchasing is not available in FOSS.
    }
}
