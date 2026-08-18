package com.zodiactap.mapper.base

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.UserManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import dagger.Lazy
import com.zodiactap.mapper.base.expertmode.SystemBridgeAutoStarter
import com.zodiactap.mapper.base.expertmode.SystemBridgeConfigSync
import com.zodiactap.mapper.base.logging.KeyMapperLoggingTree
import com.zodiactap.mapper.base.logging.SystemBridgeLogger
import com.zodiactap.mapper.base.settings.Theme
import com.zodiactap.mapper.base.system.accessibility.AccessibilityServiceAdapterImpl
import com.zodiactap.mapper.base.system.notifications.NotificationController
import com.zodiactap.mapper.base.system.permissions.AutoGrantPermissionController
import com.zodiactap.mapper.data.Keys
import com.zodiactap.mapper.data.entities.LogEntryEntity
import com.zodiactap.mapper.data.repositories.LogRepository
import com.zodiactap.mapper.data.repositories.PreferenceRepositoryImpl
import com.zodiactap.mapper.sysbridge.manager.SystemBridgeConnectionManagerImpl
import com.zodiactap.mapper.sysbridge.manager.SystemBridgeConnectionState
import com.zodiactap.mapper.sysbridge.manager.isConnected
import com.zodiactap.mapper.system.apps.AndroidPackageManagerAdapter
import com.zodiactap.mapper.system.devices.AndroidDevicesAdapter
import com.zodiactap.mapper.system.inputmethod.KeyEventRelayServiceWrapperImpl
import com.zodiactap.mapper.system.permissions.AndroidPermissionAdapter
import com.zodiactap.mapper.system.permissions.Permission
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

@SuppressLint("LogNotTimber")
abstract class BaseKeyMapperApp : MultiDexApplication() {
    private val tag = BaseKeyMapperApp::class.simpleName

    @Inject
    lateinit var appCoroutineScope: Lazy<CoroutineScope>

    @Inject
    lateinit var notificationController: Lazy<NotificationController>

    @Inject
    lateinit var packageManagerAdapter: Lazy<AndroidPackageManagerAdapter>

    @Inject
    lateinit var devicesAdapter: Lazy<AndroidDevicesAdapter>

    @Inject
    lateinit var permissionAdapter: Lazy<AndroidPermissionAdapter>

    @Inject
    lateinit var accessibilityServiceAdapter: Lazy<AccessibilityServiceAdapterImpl>

    @Inject
    lateinit var autoGrantPermissionController: Lazy<AutoGrantPermissionController>

    @Inject
    lateinit var loggingTree: Lazy<KeyMapperLoggingTree>

    @Inject
    lateinit var settingsRepository: Lazy<PreferenceRepositoryImpl>

    @Inject
    lateinit var logRepository: Lazy<LogRepository>

    @Inject
    lateinit var keyEventRelayServiceWrapper: Lazy<KeyEventRelayServiceWrapperImpl>

    @Inject
    lateinit var systemBridgeAutoStarter: Lazy<SystemBridgeAutoStarter>

    @Inject
    lateinit var systemBridgeConnectionManager: Lazy<SystemBridgeConnectionManagerImpl>

    @Inject
    lateinit var systemBridgeLogger: Lazy<SystemBridgeLogger>

    @Inject
    lateinit var systemBridgeConfigSync: Lazy<SystemBridgeConfigSync>

    private val processLifecycleOwner by lazy { ProcessLifecycleOwner.get() }

    private val userManager: UserManager? by lazy { getSystemService<UserManager>() }

    private val initLock: Any = Any()
    private var initialized = false

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context ?: return
            intent ?: return

            when (intent.action) {
                Intent.ACTION_SHUTDOWN -> {
                    Timber.i("Clean shutdown")
                }
            }
        }
    }

    override fun onCreate() {
        val priorExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

        Log.i(tag, "KeyMapperApp: OnCreate")

        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            if (userManager?.isUserUnlocked != false) {
                // log in a blocking manner and always log regardless of whether the setting is turned on
                val entry = LogEntryEntity(
                    id = 0,
                    time = Calendar.getInstance().timeInMillis,
                    severity = LogEntryEntity.SEVERITY_ERROR,
                    message = exception.stackTraceToString(),
                )

                runBlocking {
                    logRepository.get().insertSuspend(entry)
                }
            }

            priorExceptionHandler?.uncaughtException(thread, exception)
        }

        super.onCreate()

        if (userManager?.isUserUnlocked == false) {
            Log.i(tag, "KeyMapperApp: Delay init because locked.")
            // If the device is still encrypted and locked do not initialize anything that
            // may potentially need the encrypted app storage like databases.
            return
        }

        synchronized(initLock) {
            init()
            initialized = true
        }
    }

    fun onBootUnlocked() {
        Log.i(tag, "KeyMapperApp: onBootUnlocked")

        synchronized(initLock) {
            if (!initialized) {
                init()
            }
            initialized = true
        }
    }

    private fun init() {
        Log.i(tag, "KeyMapperApp: Init")

        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SHUTDOWN)
        }

        registerReceiver(broadcastReceiver, intentFilter)

        settingsRepository.get().get(Keys.darkTheme)
            .map { it?.toIntOrNull() }
            .map {
                when (it) {
                    Theme.DARK.value -> AppCompatDelegate.MODE_NIGHT_YES
                    Theme.LIGHT.value -> AppCompatDelegate.MODE_NIGHT_NO
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            }
            .onEach { mode -> AppCompatDelegate.setDefaultNightMode(mode) }
            .launchIn(appCoroutineScope.get())

        if (BuildConfig.BUILD_TYPE == "debug" || BuildConfig.BUILD_TYPE == "debug_release") {
            Timber.plant(Timber.DebugTree())
        }

        Timber.plant(loggingTree.get())

        notificationController.get().init()

        processLifecycleOwner.lifecycle.addObserver(
            object : LifecycleObserver {
                @Suppress("DEPRECATION")
                @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
                fun onResume() {
                    // when the user returns to the app let everything know that the permissions could have changed
                    notificationController.get().onOpenApp()

                    if (BuildConfig.DEBUG &&
                        permissionAdapter.get().isGranted(Permission.WRITE_SECURE_SETTINGS)
                    ) {
                        accessibilityServiceAdapter.get().start()
                    }
                }
            },
        )

        appCoroutineScope.get().launch {
            notificationController.get().openApp.collectLatest { intentAction ->
                Intent(this@BaseKeyMapperApp, getMainActivityClass()).apply {
                    action = intentAction
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK

                    startActivity(this)
                }
            }
        }

        notificationController.get().showToast.onEach { toast ->
            Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
        }.launchIn(appCoroutineScope.get())

        autoGrantPermissionController.get().start()
        keyEventRelayServiceWrapper.get().bind()

        if (systemBridgeConnectionManager.get().isConnected()) {
            Timber.i("KeyMapperApp: System bridge is connected")
        } else {
            Timber.i("KeyMapperApp: System bridge is disconnected")
        }

        systemBridgeAutoStarter.get().init()

        // Initialize SystemBridgeLogger to start receiving log messages from SystemBridge.
        // Using Lazy<> to avoid circular dependency issues and ensure it's only created
        // when the API level requirement is met.
        systemBridgeLogger.get().start()

        // Push system bridge config preferences (e.g. the power button emergency stop
        // toggle) down to the SystemBridge process on connect and whenever they change.
        systemBridgeConfigSync.get().start()

        appCoroutineScope.get().launch {
            systemBridgeConnectionManager.get().connectionState.collect { state ->
                if (state is SystemBridgeConnectionState.Connected) {
                    val isUsed =
                        settingsRepository.get().get(Keys.isSystemBridgeUsed).first() ?: false

                    // Enable the setting to use PRO mode for key event actions the first time they use PRO mode.
                    if (!isUsed) {
                        settingsRepository.get().set(Keys.keyEventActionsUseSystemBridge, true)
                    }

                    settingsRepository.get().set(Keys.isSystemBridgeUsed, true)
                }
            }
        }
    }

    abstract fun getMainActivityClass(): Class<*>
}
