package com.example.pc02lira24100302.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import com.example.pc02lira24100302.data.remote.FirebaseAuthManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerScaffold(navController: NavController,
                   content: @Composable () -> Unit) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Menu principal", modifier = Modifier.padding(16.dp))
                //Home
                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = false,
                    onClick = {
                        navController.navigate("home")
                    }
                )
                //Permisions
                NavigationDrawerItem(
                    label = { Text("Permissions") },
                    selected = false,
                    onClick = {
                        navController.navigate("permissions")
                    }
                )
                //Favorites
                NavigationDrawerItem(
                    label = { Text("Favorites") },
                    selected = false,
                    onClick = {
                        navController.navigate("favorites")
                    }
                )
                //Football
                NavigationDrawerItem(
                    label = { Text("Football") },
                    selected = false,
                    onClick = {
                        navController.navigate("football")
                    }
                )
                //Chat
                NavigationDrawerItem(
                    label = { Text("Chat") },
                    selected = false,
                    onClick = {
                        navController.navigate("chat")
                    }
                )
                //Logout
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    onClick = {
                        FirebaseAuthManager.logout()
                        navController.navigate("login")
                    }
                )
            }
        }
    ){
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("UESAN App")},
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ){
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }})
            }
        ){
            paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)){
                content()
            }
        }
    }



}