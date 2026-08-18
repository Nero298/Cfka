package com.zodiactap.mapper.system.notifications

import kotlinx.serialization.Serializable

sealed class NotificationServiceEvent {

    @Serializable
    data object DismissLastNotification : NotificationServiceEvent()

    @Serializable
    data object DismissAllNotifications : NotificationServiceEvent()
}
