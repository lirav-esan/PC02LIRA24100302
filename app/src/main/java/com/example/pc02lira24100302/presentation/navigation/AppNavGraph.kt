package com.example.pc02lira24100302.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pc02lira24100302.presentation.auth.LoginScreen
import com.example.pc02lira24100302.presentation.home.HomeScreen
import com.example.pc02lira24100302.presentation.permissions.GalleryPermissionsScreen

@Composable
fun AppNavGraph(){
    val navController = rememberNavController()

    NavHost(navController = navController,
            startDestination = "login"){
        composable("login"){ LoginScreen(navController) }
        composable("home"){
            DrawerScaffold(navController) {
                HomeScreen()
            }
        }
        composable("permissions"){
            DrawerScaffold(navController) {
                GalleryPermissionsScreen()
            }
        }

    }
}