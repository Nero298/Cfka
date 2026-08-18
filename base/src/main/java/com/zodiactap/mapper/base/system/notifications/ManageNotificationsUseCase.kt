package com.zodiactap.mapper.base.system.notifications

import com.zodiactap.mapper.common.notifications.KMNotificationAction
import com.zodiactap.mapper.system.notifications.NotificationAdapter
import com.zodiactap.mapper.system.notifications.NotificationChannelModel
import com.zodiactap.mapper.system.notifications.NotificationModel
import com.zodiactap.mapper.system.notifications.NotificationRemoteInput
import com.zodiactap.mapper.system.permissions.Permission
import com.zodiactap.mapper.system.permissions.PermissionAdapter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ManageNotificationsUseCaseImpl @Inject constructor(
    private val notificationAdapter: NotificationAdapter,
    private val permissionAdapter: PermissionAdapter,
) : ManageNotificationsUseCase {

    override val onNotificationTextInput: Flow<NotificationRemoteInput> =
        notificationAdapter.onNotificationRemoteInput
    override val onActionClick: Flow<KMNotificationAction.IntentAction> =
        notificationAdapter.onNotificationActionClick

    override fun show(notification: NotificationModel) {
        notificationAdapter.showNotification(notification)
    }

    override fun dismiss(notificationId: Int) {
        notificationAdapter.dismissNotification(notificationId)
    }

    override fun createChannel(channel: NotificationChannelModel) {
        notificationAdapter.createChannel(channel)
    }

    override fun deleteChannel(channelId: String) {
        notificationAdapter.deleteChannel(channelId)
    }

    override fun isPermissionGranted(): Boolean {
        return permissionAdapter.isGranted(Permission.POST_NOTIFICATIONS)
    }
}

interface ManageNotificationsUseCase {

    /**
     * Emits text input from notification actions that support RemoteInput.
     */
    val onNotificationTextInput: Flow<NotificationRemoteInput>

    val onActionClick: Flow<KMNotificationAction.IntentAction>

    fun isPermissionGranted(): Boolean
    fun show(notification: NotificationModel)
    fun dismiss(notificationId: Int)
    fun createChannel(channel: NotificationChannelModel)
    fun deleteChannel(channelId: String)
}
