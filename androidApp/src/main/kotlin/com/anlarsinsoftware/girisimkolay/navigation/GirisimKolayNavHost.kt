package com.anlarsinsoftware.girisimkolay.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.anlarsinsoftware.girisimkolay.auth.ui.LoginScreen
import com.anlarsinsoftware.girisimkolay.auth.ui.RegisterScreen
import com.anlarsinsoftware.girisimkolay.auth.viewmodel.AuthViewModel
import com.anlarsinsoftware.girisimkolay.chat.ui.AIProfileChatScreen
import com.anlarsinsoftware.girisimkolay.chat.ui.ExpertAdviceDetailScreen
import com.anlarsinsoftware.girisimkolay.chat.ui.AskExpertScreen
import com.anlarsinsoftware.girisimkolay.chat.ui.AskExpertSuccessScreen
import com.anlarsinsoftware.girisimkolay.community.ui.CommunityHubScreen
import com.anlarsinsoftware.girisimkolay.dashboard.ui.DashboardNewsFeedScreen
import com.anlarsinsoftware.girisimkolay.roadmap.ui.RoadmapDocumentCenterScreen
import com.anlarsinsoftware.girisimkolay.notifications.ui.NotificationsScreen
import com.anlarsinsoftware.girisimkolay.advice.ui.RelatedQuestionsScreen
import com.anlarsinsoftware.girisimkolay.profile.ui.GirisimciProfileScreen
import com.anlarsinsoftware.girisimkolay.profile.ui.ExpertProfileScreen
import com.anlarsinsoftware.girisimkolay.profile.ui.SettingsPreferencesScreen
import com.anlarsinsoftware.girisimkolay.profile.ui.DeleteAccountScreen
import com.anlarsinsoftware.girisimkolay.profile.ui.SecurityVerificationScreen
import com.anlarsinsoftware.girisimkolay.profile.ui.SubscriptionManagementScreen
import com.anlarsinsoftware.girisimkolay.analytics.ui.MarketAnalysisGraphScreen
import com.anlarsinsoftware.girisimkolay.analytics.ui.MarketAnalysisDetailScreen
import org.koin.androidx.compose.koinViewModel

// ── Route constants ──────────────────────────────────────────
private const val AUTH_GRAPH   = "auth_graph"
private const val MAIN_GRAPH   = "main_graph"
private const val LOGIN_ROUTE  = "login"
private const val REGISTER_ROUTE = "register"

sealed class Screen(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Dashboard : Screen("dashboard", "Ana Sayfa",    Icons.Default.Dashboard)
    object Chat      : Screen("chat",      "AI Advice",    Icons.Default.SmartToy)
    object Community : Screen("community", "Hub",          Icons.Default.Groups)
    object Consult   : Screen("consult",   "Consult",      Icons.Default.SupportAgent)
    object Roadmap   : Screen("roadmap",   "Reports",      Icons.Default.Description)
    object Profile   : Screen("profile",   "Profile",      Icons.Default.Person)
}

private val bottomNavItems = listOf(
    Screen.Dashboard, Screen.Chat, Screen.Community, Screen.Consult, Screen.Roadmap
)

// ── Root composable ──────────────────────────────────────────
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = koinViewModel()
    val currentUser by authViewModel.currentUser.collectAsState()

    // Determine start destination based on Firebase auth state
    val startDestination = if (currentUser != null) MAIN_GRAPH else AUTH_GRAPH

    NavHost(navController = navController, startDestination = startDestination) {

        // ── AUTH GRAPH ──────────────────────────────────────
        navigation(startDestination = LOGIN_ROUTE, route = AUTH_GRAPH) {

            composable(LOGIN_ROUTE) {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(REGISTER_ROUTE)
                    }
                )
            }

            composable(REGISTER_ROUTE) {
                RegisterScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        // ── MAIN GRAPH (guarded by auth) ────────────────────
        navigation(startDestination = Screen.Dashboard.route, route = MAIN_GRAPH) {
            composable(Screen.Dashboard.route) {
                MainScaffold(navController) {
                    DashboardNewsFeedScreen(
                        onNavigateToNotifications = { navController.navigate("notifications") },
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
                    )
                }
            }
            composable(Screen.Chat.route)      { MainScaffold(navController) { AIProfileChatScreen() } }
            composable(Screen.Community.route) {
                MainScaffold(navController) {
                    CommunityHubScreen(
                        onNavigateToNotifications = { navController.navigate("notifications") },
                        onNavigateToAdviceDetail = { adviceId -> navController.navigate("expert_advice/$adviceId") },
                        onNavigateToExpertProfile = { expertId -> navController.navigate("expert_profile/$expertId") }
                    )
                }
            }
            composable(
                route = "consult?category={category}",
                arguments = listOf(navArgument("category") { defaultValue = ""; type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category")
                MainScaffold(navController) {
                    AskExpertScreen(
                        initialSpecialty = if (category.isNullOrEmpty()) null else category,
                        onNavigateBack = { navController.popBackStack() },
                        onSubmitSuccess = {
                            val randomRef = "#GK-2023-${(1000..9999).random()}"
                            navController.navigate("ask_expert_success/$randomRef") {
                                popUpTo("consult?category={category}") { inclusive = true }
                            }
                        }
                    )
                }
            }
            composable(Screen.Roadmap.route)   { MainScaffold(navController) { RoadmapDocumentCenterScreen() } }
            composable(Screen.Profile.route) {
                MainScaffold(navController) {
                    GirisimciProfileScreen(
                        onNavigateToNotifications = { navController.navigate("notifications") },
                        onNavigateToReports = {
                            navController.navigate(Screen.Roadmap.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToSettings = { navController.navigate("settings") },
                        onNavigateToMarketAnalysis = { navController.navigate("market_analysis_graph") }
                    )
                }
            }

            composable("settings") {
                SettingsPreferencesScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSubscription = { navController.navigate("subscription_management") },
                    onNavigateToDeleteAccount = { navController.navigate("delete_account") },
                    onLogout = { authViewModel.signOut() }
                )
            }

            composable("delete_account") {
                DeleteAccountScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onConfirmDelete = { navController.navigate("delete_account_verification") }
                )
            }

            composable("delete_account_verification") {
                SecurityVerificationScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onDeleteConfirmed = { password -> authViewModel.signOut() }
                )
            }

            composable("subscription_management") {
                SubscriptionManagementScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("market_analysis_graph") {
                MarketAnalysisGraphScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { navController.navigate("market_analysis_detail") }
                )
            }

            composable("market_analysis_detail") {
                MarketAnalysisDetailScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("notifications") {
                NotificationsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAdvice = { adviceId -> navController.navigate("expert_advice/$adviceId") },
                    onNavigateToExpertProfile = { expertId -> navController.navigate("expert_profile/$expertId") }
                )
            }
            composable("expert_advice/{adviceId}") { backStackEntry ->
                val adviceId = backStackEntry.arguments?.getString("adviceId") ?: "1"
                ExpertAdviceDetailScreen(
                    adviceId = adviceId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToRelatedQuestions = { navController.navigate("related_questions") },
                    onNavigateToExpertProfile = { expertId -> navController.navigate("expert_profile/$expertId") },
                    onAskExpert = { category ->
                        navController.navigate("consult?category=$category")
                    }
                )
            }
            composable("related_questions") {
                RelatedQuestionsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAdviceDetail = { adviceId ->
                        navController.navigate("expert_advice/$adviceId")
                    },
                    onNavigateToAIChat = {
                        navController.navigate(Screen.Chat.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable("expert_profile/{expertId}") { backStackEntry ->
                val expertId = backStackEntry.arguments?.getString("expertId") ?: "selin"
                ExpertProfileScreen(
                    expertId = expertId,
                    onNavigateBack = { navController.popBackStack() },
                    onAskQuestion = { category ->
                        navController.navigate("consult?category=$category")
                    }
                )
            }
            composable("ask_expert_success/{referenceId}") { backStackEntry ->
                val referenceId = backStackEntry.arguments?.getString("referenceId") ?: "#GK-2023-8472"
                AskExpertSuccessScreen(
                    referenceId = referenceId,
                    onNavigateToCommunity = {
                        navController.navigate(Screen.Community.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToDashboard = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }

    // React to auth state changes → navigate between graphs
    LaunchedEffect(currentUser) {
        val hasMainGraph = navController.currentBackStackEntry?.destination?.hierarchy?.any { it.route == MAIN_GRAPH } == true
        val hasAuthGraph = navController.currentBackStackEntry?.destination?.hierarchy?.any { it.route == AUTH_GRAPH } == true

        if (currentUser != null) {
            if (!hasMainGraph) {
                navController.navigate(MAIN_GRAPH) {
                    popUpTo(AUTH_GRAPH) { inclusive = true }
                }
            }
        } else {
            if (!hasAuthGraph) {
                navController.navigate(AUTH_GRAPH) {
                    popUpTo(MAIN_GRAPH) { inclusive = true }
                }
            }
        }
    }
}

// ── Shared bottom-bar scaffold ───────────────────────────────
@Composable
private fun MainScaffold(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon  = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title, fontSize = 9.sp) },
                        selected = currentDestination?.hierarchy
                            ?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState     = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(innerPadding)
        ) { content() }
    }
}
