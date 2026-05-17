package com.anlarsinsoftware.girisimkolay.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.anlarsinsoftware.girisimkolay.analytics.ui.AnalyticsDashboardScreen
import com.anlarsinsoftware.girisimkolay.auth.ui.LoginScreen
import com.anlarsinsoftware.girisimkolay.auth.ui.RegisterScreen
import com.anlarsinsoftware.girisimkolay.auth.viewmodel.AuthViewModel
import com.anlarsinsoftware.girisimkolay.calendar.ui.BusinessCalendarScreen
import com.anlarsinsoftware.girisimkolay.chat.ui.AIProfileChatScreen
import com.anlarsinsoftware.girisimkolay.community.ui.CommunityHubScreen
import com.anlarsinsoftware.girisimkolay.dashboard.ui.DashboardNewsFeedScreen
import com.anlarsinsoftware.girisimkolay.roadmap.ui.RoadmapDocumentCenterScreen
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
    object Chat      : Screen("chat",      "AI Danışman",  Icons.Default.Chat)
    object Dashboard : Screen("dashboard", "Dashboard",    Icons.Default.Dashboard)
    object Calendar  : Screen("calendar",  "Takvim",       Icons.Default.CalendarMonth)
    object Analytics : Screen("analytics", "Analiz",       Icons.Default.Analytics)
    object Community : Screen("community", "Topluluk",     Icons.Default.Group)
    object Roadmap   : Screen("roadmap",   "Yol Haritası", Icons.Default.Map)
}

private val bottomNavItems = listOf(
    Screen.Chat, Screen.Dashboard, Screen.Calendar,
    Screen.Analytics, Screen.Community, Screen.Roadmap
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
        navigation(startDestination = Screen.Chat.route, route = MAIN_GRAPH) {
            composable(Screen.Chat.route)      { MainScaffold(navController) { AIProfileChatScreen() } }
            composable(Screen.Dashboard.route) { MainScaffold(navController) { DashboardNewsFeedScreen() } }
            composable(Screen.Calendar.route)  { MainScaffold(navController) { BusinessCalendarScreen() } }
            composable(Screen.Analytics.route) { MainScaffold(navController) { AnalyticsDashboardScreen() } }
            composable(Screen.Community.route) { MainScaffold(navController) { CommunityHubScreen() } }
            composable(Screen.Roadmap.route)   { MainScaffold(navController) { RoadmapDocumentCenterScreen() } }
        }
    }

    // React to auth state changes → navigate between graphs
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate(MAIN_GRAPH) {
                popUpTo(AUTH_GRAPH) { inclusive = true }
            }
        } else {
            navController.navigate(AUTH_GRAPH) {
                popUpTo(MAIN_GRAPH) { inclusive = true }
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

