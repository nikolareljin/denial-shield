package com.denialshield.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object UserInfo : Screen("user_info")
    object ClaimIntake : Screen("claim_intake")
    object ClaimDetail : Screen("claim_detail/{claimId}") {
        fun createRoute(claimId: Long) = "claim_detail/$claimId"
    }
}
