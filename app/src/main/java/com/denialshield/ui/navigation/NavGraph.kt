package com.denialshield.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.denialshield.ui.screens.HomeScreen
import com.denialshield.ui.screens.UserInfoScreen
import com.denialshield.ui.screens.ClaimIntakeScreen
import com.denialshield.ui.screens.ClaimDetailScreen
import com.denialshield.ui.viewmodel.MainViewModel

@Composable
fun DenialShieldNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onAddClaim = { navController.navigate(Screen.ClaimIntake.route) },
                onEditUserInfo = { navController.navigate(Screen.UserInfo.route) },
                onClaimClick = { claimId -> 
                    navController.navigate(Screen.ClaimDetail.createRoute(claimId))
                }
            )
        }
        composable(Screen.UserInfo.route) {
            UserInfoScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.ClaimIntake.route) {
            ClaimIntakeScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { claimId ->
                    navController.navigate(Screen.ClaimDetail.createRoute(claimId)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
        composable(
            route = Screen.ClaimDetail.route,
            arguments = listOf(navArgument("claimId") { type = NavType.LongType })
        ) { backStackEntry ->
            val claimId = backStackEntry.arguments?.getLong("claimId") ?: 0L
            ClaimDetailScreen(
                claimId = claimId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
