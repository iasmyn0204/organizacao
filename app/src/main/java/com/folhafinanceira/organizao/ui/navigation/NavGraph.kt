package com.folhafinanceira.organizao.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.folhafinanceira.organizao.ui.screens.*
import com.folhafinanceira.organizao.ui.viewmodel.AuthState
import com.folhafinanceira.organizao.ui.viewmodel.FinanceViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Início", Icons.Default.Home)
    object Transactions : Screen("transactions", "Transações", Icons.Default.Receipt)
    object Scanner : Screen("scanner", "Notas", Icons.Default.QrCodeScanner)
    object Assistant : Screen("assistant", "Agente IA", Icons.Default.SmartToy)
    object Limits : Screen("limits", "Limites", Icons.Default.PieChart)
    object Planning : Screen("planning", "Metas", Icons.Default.Savings)
    object Reports : Screen("reports", "Relatórios", Icons.Default.BarChart)
    object Settings : Screen("settings", "Ajustes", Icons.Default.Settings)
}

@Composable
fun NavGraph(viewModel: FinanceViewModel) {
    val authState by viewModel.authState.collectAsState()

    if (authState == AuthState.LOGGED_OUT) {
        AuthScreen(viewModel = viewModel)
    } else {
        var currentTabRoute by remember { mutableStateOf(Screen.Dashboard.route) }

        val items = listOf(
            Screen.Dashboard,
            Screen.Transactions,
            Screen.Scanner,
            Screen.Assistant,
            Screen.Limits,
            Screen.Planning,
            Screen.Reports,
            Screen.Settings
        )

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentTabRoute == screen.route,
                            onClick = { currentTabRoute = screen.route },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            val innerModifier = Modifier.padding(innerPadding)
            when (currentTabRoute) {
                Screen.Dashboard.route -> DashboardScreen(viewModel) { currentTabRoute = Screen.Transactions.route }
                Screen.Transactions.route -> TransactionsScreen(viewModel)
                Screen.Scanner.route -> ReceiptScannerScreen(viewModel)
                Screen.Assistant.route -> AssistantScreen(viewModel)
                Screen.Limits.route -> LimitsScreen(viewModel)
                Screen.Planning.route -> PlanningScreen(viewModel)
                Screen.Reports.route -> ReportsScreen(viewModel)
                Screen.Settings.route -> SettingsScreen(viewModel)
            }
        }
    }
}
