package com.example.composeapp.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.composeapp.navigation.Route
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController

sealed class Screen(val title: String, val icon: ImageVector) {
    object Home : Screen("Home", Icons.Default.Home)
    object Learn : Screen("Learn", Icons.AutoMirrored.Filled.MenuBook)
    object Quiz : Screen("Quiz", Icons.Default.Quiz)
    object Status : Screen("Progress", Icons.Default.Analytics)
    object Profile : Screen("Profile", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController
) {
    var selectedIndex by remember { mutableStateOf(0) }
    var isShowingEditProfile by remember { mutableStateOf(false) }
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(56.dp),
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    icon = { 
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = { 
                        Text(
                            "Home",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    selected = selectedIndex == 0,
                    onClick = { selectedIndex = 0 }
                )
                NavigationBarItem(
                    icon = { 
                        Icon(
                            Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "Learn",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = { 
                        Text(
                            "Learn",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    selected = selectedIndex == 1,
                    onClick = { selectedIndex = 1 }
                )
                NavigationBarItem(
                    icon = { 
                        Icon(
                            Icons.Default.Quiz,
                            contentDescription = "Quiz",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = { 
                        Text(
                            "Quiz",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    selected = selectedIndex == 2,
                    onClick = { selectedIndex = 2 }
                )
                NavigationBarItem(
                    icon = { 
                        Icon(
                            Icons.Default.Analytics,
                            contentDescription = "Progress",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = { 
                        Text(
                            "Progress",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    selected = selectedIndex == 3,
                    onClick = { selectedIndex = 3 }
                )
                NavigationBarItem(
                    icon = { 
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = { 
                        Text(
                            "Profile",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    selected = selectedIndex == 4,
                    onClick = { selectedIndex = 4 }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedIndex) {
                0 -> HomeScreen(
                    navigateToCategory = { categoryName ->
                        navController.navigate("category_level/$categoryName")
                    },
                    navigateToProfile = {
                        selectedIndex = 4
                    }
                )
                1 -> LearnScreen()
                2 -> QuizScreen()
                3 -> StatusScreen()
                4 -> ProfileScreen(
                    onNavigateBack = { selectedIndex = 0 },
                    onEditProfile = { isShowingEditProfile = true },
                    onLogout = { navController.navigate("login") }
                )
            }

            // Show edit profile screen as overlay when active
            if (isShowingEditProfile) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EditProfileScreen(
                        onNavigateBack = {
                            isShowingEditProfile = false
                        }
                    )
                }
            }
        }
    }
} 