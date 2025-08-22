package com.mod.thmanyah_android_challenge.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mod.thmanyah_android_challenge.ui.home.HomeScreen
import com.mod.thmanyah_android_challenge.ui.search.SearchScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
}

@Composable
fun ThmanyahNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onContentClick = { contentItem ->
                    println("Clicked on: ${contentItem.name}")
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onContentClick = { searchResult ->
                    println("Clicked on search result: ${searchResult.name}")
                }
            )
        }
    }
}