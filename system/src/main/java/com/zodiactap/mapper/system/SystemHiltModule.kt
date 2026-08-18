package com.zodiactap.mapper.system

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.zodiactap.mapper.system.airplanemode.AirplaneModeAdapter
import com.zodiactap.mapper.system.airplanemode.AndroidAirplaneModeAdapter
import com.zodiactap.mapper.system.apps.AndroidAppShortcutAdapter
import com.zodiactap.mapper.system.apps.AndroidPackageManagerAdapter
import com.zodiactap.mapper.system.apps.AppShortcutAdapter
import com.zodiactap.mapper.system.apps.PackageManagerAdapter
import com.zodiactap.mapper.system.bluetooth.AndroidBluetoothAdapter
import com.zodiactap.mapper.system.bluetooth.BluetoothAdapter
import com.zodiactap.mapper.system.camera.AndroidCameraAdapter
import com.zodiactap.mapper.system.camera.CameraAdapter
import com.zodiactap.mapper.system.clipboard.AndroidClipboardAdapter
import com.zodiactap.mapper.system.clipboard.ClipboardAdapter
import com.zodiactap.mapper.system.devices.AndroidDevicesAdapter
import com.zodiactap.mapper.system.devices.DevicesAdapter
import com.zodiactap.mapper.system.display.AndroidDisplayAdapter
import com.zodiactap.mapper.system.display.DisplayAdapter
import com.zodiactap.mapper.system.files.AndroidFileAdapter
import com.zodiactap.mapper.system.files.FileAdapter
import com.zodiactap.mapper.system.foldable.AndroidFoldableAdapter
import com.zodiactap.mapper.system.foldable.FoldableAdapter
import com.zodiactap.mapper.system.inputmethod.AndroidInputMethodAdapter
import com.zodiactap.mapper.system.inputmethod.InputMethodAdapter
import com.zodiactap.mapper.system.intents.IntentAdapter
import com.zodiactap.mapper.system.intents.IntentAdapterImpl
import com.zodiactap.mapper.system.leanback.LeanbackAdapter
import com.zodiactap.mapper.system.leanback.LeanbackAdapterImpl
import com.zodiactap.mapper.system.lock.AndroidLockScreenAdapter
import com.zodiactap.mapper.system.lock.LockScreenAdapter
import com.zodiactap.mapper.system.media.AndroidMediaAdapter
import com.zodiactap.mapper.system.media.MediaAdapter
import com.zodiactap.mapper.system.network.AndroidNetworkAdapter
import com.zodiactap.mapper.system.network.NetworkAdapter
import com.zodiactap.mapper.system.nfc.AndroidNfcAdapter
import com.zodiactap.mapper.system.nfc.NfcAdapter
import com.zodiactap.mapper.system.notifications.NotificationReceiverAdapter
import com.zodiactap.mapper.system.notifications.NotificationReceiverAdapterImpl
import com.zodiactap.mapper.system.permissions.AndroidPermissionAdapter
import com.zodiactap.mapper.system.permissions.PermissionAdapter
import com.zodiactap.mapper.system.permissions.SystemFeatureAdapter
import com.zodiactap.mapper.system.phone.AndroidPhoneAdapter
import com.zodiactap.mapper.system.phone.PhoneAdapter
import com.zodiactap.mapper.system.popup.AndroidToastAdapter
import com.zodiactap.mapper.system.popup.ToastAdapter
import com.zodiactap.mapper.system.power.AndroidPowerAdapter
import com.zodiactap.mapper.system.power.PowerAdapter
import com.zodiactap.mapper.system.ringtones.AndroidRingtoneAdapter
import com.zodiactap.mapper.system.ringtones.RingtoneAdapter
import com.zodiactap.mapper.system.root.SuAdapter
import com.zodiactap.mapper.system.root.SuAdapterImpl
import com.zodiactap.mapper.system.settings.AndroidSettingsAdapter
import com.zodiactap.mapper.system.settings.SettingsAdapter
import com.zodiactap.mapper.system.shell.ShellAdapter
import com.zodiactap.mapper.system.shell.StandardShellAdapter
import com.zodiactap.mapper.system.shizuku.ShizukuAdapter
import com.zodiactap.mapper.system.shizuku.ShizukuAdapterImpl
import com.zodiactap.mapper.system.url.AndroidOpenUrlAdapter
import com.zodiactap.mapper.system.url.OpenUrlAdapter
import com.zodiactap.mapper.system.vibrator.AndroidVibratorAdapter
import com.zodiactap.mapper.system.vibrator.VibratorAdapter
import com.zodiactap.mapper.system.volume.AndroidVolumeAdapter
import com.zodiactap.mapper.system.volume.VolumeAdapter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SystemHiltModule {
    @Singleton
    @Binds
    abstract fun provideLockscreenAdapter(impl: AndroidLockScreenAdapter): LockScreenAdapter

    @Singleton
    @Binds
    abstract fun provideAirplaneModeAdapter(impl: AndroidAirplaneModeAdapter): AirplaneModeAdapter

    @Singleton
    @Binds
    abstract fun provideAppShortcutAdapter(impl: AndroidAppShortcutAdapter): AppShortcutAdapter

    @Singleton
    @Binds
    abstract fun providePackageManagerAdapter(
        impl: AndroidPackageManagerAdapter,
    ): PackageManagerAdapter

    @Singleton
    @Binds
    abstract fun provideBluetoothAdapter(impl: AndroidBluetoothAdapter): BluetoothAdapter

    @Singleton
    @Binds
    abstract fun provideCameraAdapter(impl: AndroidCameraAdapter): CameraAdapter

    @Singleton
    @Binds
    abstract fun provideClipboardAdapter(impl: AndroidClipboardAdapter): ClipboardAdapter

    @Singleton
    @Binds
    abstract fun provideDevicesAdapter(impl: AndroidDevicesAdapter): DevicesAdapter

    @Singleton
    @Binds
    abstract fun provideDisplayAdapter(impl: AndroidDisplayAdapter): DisplayAdapter

    @Singleton
    @Binds
    abstract fun provideFileAdapter(impl: AndroidFileAdapter): FileAdapter

    @Singleton
    @Binds
    abstract fun provideFoldableAdapter(impl: AndroidFoldableAdapter): FoldableAdapter

    @Singleton
    @Binds
    abstract fun provideInputMethodAdapter(impl: AndroidInputMethodAdapter): InputMethodAdapter

    @Singleton
    @Binds
    abstract fun provideIntentAdapter(impl: IntentAdapterImpl): IntentAdapter

    @Singleton
    @Binds
    abstract fun provideLeanbackAdapter(impl: LeanbackAdapterImpl): LeanbackAdapter

    @Singleton
    @Binds
    abstract fun provideMediaAdapter(impl: AndroidMediaAdapter): MediaAdapter

    @Singleton
    @Binds
    abstract fun provideNetworkAdapter(impl: AndroidNetworkAdapter): NetworkAdapter

    @Singleton
    @Binds
    abstract fun provideNfcAdapter(impl: AndroidNfcAdapter): NfcAdapter

    @Singleton
    @Binds
    abstract fun providePermissionAdapter(impl: AndroidPermissionAdapter): PermissionAdapter

    @Singleton
    @Binds
    abstract fun providePhoneAdapter(impl: AndroidPhoneAdapter): PhoneAdapter

    @Singleton
    @Binds
    abstract fun providePopupMessageAdapter(impl: AndroidToastAdapter): ToastAdapter

    @Singleton
    @Binds
    abstract fun providePowerAdapter(impl: AndroidPowerAdapter): PowerAdapter

    @Singleton
    @Binds
    abstract fun provideRingtoneAdapter(impl: AndroidRingtoneAdapter): RingtoneAdapter

    @Singleton
    @Binds
    abstract fun provideSuAdapter(impl: SuAdapterImpl): SuAdapter

    @Singleton
    @Binds
    abstract fun provideOpenUrlAdapter(impl: AndroidOpenUrlAdapter): OpenUrlAdapter

    @Singleton
    @Binds
    abstract fun provideVibratorAdapter(impl: AndroidVibratorAdapter): VibratorAdapter

    @Singleton
    @Binds
    abstract fun provideVolumeAdapter(impl: AndroidVolumeAdapter): VolumeAdapter

    @Singleton
    @Binds
    abstract fun provideShellAdapter(impl: StandardShellAdapter): ShellAdapter

    @Singleton
    @Binds
    abstract fun provideShizukuAdapter(impl: ShizukuAdapterImpl): ShizukuAdapter

    @Singleton
    @Binds
    abstract fun provideNotificationReceiverAdapter(
        impl: NotificationReceiverAdapterImpl,
    ): NotificationReceiverAdapter

    @Singleton
    @Binds
    abstract fun provideSystemFeatureAdapter(
        impl: AndroidSystemFeatureAdapter,
    ): SystemFeatureAdapter

    @Singleton
    @Binds
    abstract fun provideSettingsAdapter(impl: AndroidSettingsAdapter): SettingsAdapter
}
