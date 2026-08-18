package com.zodiactap.mapper.base

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zodiactap.mapper.base.actions.ChooseActionScreen
import com.zodiactap.mapper.base.actions.ChooseActionViewModel
import com.zodiactap.mapper.base.actions.ChooseSettingScreen
import com.zodiactap.mapper.base.actions.ConfigShellCommandViewModel
import com.zodiactap.mapper.base.actions.ShellCommandActionScreen
import com.zodiactap.mapper.base.actions.uielement.InteractUiElementScreen
import com.zodiactap.mapper.base.actions.uielement.InteractUiElementViewModel
import com.zodiactap.mapper.base.constraints.ChooseConstraintScreen
import com.zodiactap.mapper.base.constraints.ChooseConstraintViewModel
import com.zodiactap.mapper.base.debug.GetEventScreen
import com.zodiactap.mapper.base.expertmode.ExpertModeScreen
import com.zodiactap.mapper.base.expertmode.ExpertModeSetupScreen
import com.zodiactap.mapper.base.logging.LogScreen
import com.zodiactap.mapper.base.onboarding.HandleAccessibilityServiceDialogs
import com.zodiactap.mapper.base.onboarding.SetupAccessibilityServiceDelegateImpl
import com.zodiactap.mapper.base.settings.AutomaticChangeImeSettingsScreen
import com.zodiactap.mapper.base.settings.DefaultOptionsSettingsScreen
import com.zodiactap.mapper.base.settings.SettingsScreen
import com.zodiactap.mapper.base.settings.SettingsViewModel
import com.zodiactap.mapper.base.utils.navigation.NavDestination
import com.zodiactap.mapper.base.utils.navigation.handleRouteArgs
import kotlinx.serialization.json.Json

@Composable
fun BaseMainNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    setupAccessibilityServiceDelegate: SetupAccessibilityServiceDelegateImpl,
    composableDestinations: NavGraphBuilder.() -> Unit = {},
) {
    HandleAccessibilityServiceDialogs(setupAccessibilityServiceDelegate)

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavDestination.Home,
        enterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left)
        },
        exitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
        },
        popEnterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
        },
        popExitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
        },
    ) {
        composable<NavDestination.InteractUiElement> { backStackEntry ->
            val viewModel: InteractUiElementViewModel = hiltViewModel()

            backStackEntry.handleRouteArgs<NavDestination.InteractUiElement> { destination ->
                destination.actionJson?.let { viewModel.loadAction(Json.decodeFromString(it)) }
            }

            InteractUiElementScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
            )
        }

        composable<NavDestination.ConfigShellCommand> { backStackEntry ->
            val viewModel: ConfigShellCommandViewModel = hiltViewModel()

            backStackEntry.handleRouteArgs<NavDestination.ConfigShellCommand> { destination ->
                destination.actionJson?.let { viewModel.loadAction(Json.decodeFromString(it)) }
            }

            ShellCommandActionScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
            )
        }

        composable<NavDestination.ChooseConstraint> {
            val viewModel: ChooseConstraintViewModel = hiltViewModel()

            ChooseConstraintScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
            )
        }

        composable<NavDestination.ChooseAction> {
            val viewModel: ChooseActionViewModel = hiltViewModel()

            ChooseActionScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
            )
        }

        composable<NavDestination.Settings> {
            val viewModel: SettingsViewModel = hiltViewModel()

            SettingsScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
            )
        }

        composable<NavDestination.DefaultOptionsSettings> {
            val viewModel: SettingsViewModel = hiltViewModel()

            DefaultOptionsSettingsScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
            )
        }

        composable<NavDestination.AutomaticChangeImeSettings> {
            val viewModel: SettingsViewModel = hiltViewModel()

            AutomaticChangeImeSettingsScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
            )
        }

        composable<NavDestination.ExpertMode> {
            ExpertModeScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(
                        WindowInsets.systemBars.only(sides = WindowInsetsSides.Horizontal)
                            .add(
                                WindowInsets.displayCutout.only(
                                    sides = WindowInsetsSides.Horizontal,
                                ),
                            ),
                    ),
                viewModel = hiltViewModel(),
            )
        }

        composable<NavDestination.ExpertModeSetup> {
            ExpertModeSetupScreen(
                viewModel = hiltViewModel(),
            )
        }

        composable<NavDestination.Log> {
            LogScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = hiltViewModel(),
                onBackClick = { navController.popBackStack() },
            )
        }

        composable<NavDestination.GetEvent> {
            GetEventScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = hiltViewModel(),
                onBackClick = { navController.popBackStack() },
            )
        }

        composable<NavDestination.ChooseSetting> {
            ChooseSettingScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = hiltViewModel(),
            )
        }

        composableDestinations()
    }
}
