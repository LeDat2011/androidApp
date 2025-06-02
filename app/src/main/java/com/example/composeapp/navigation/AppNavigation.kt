package com.example.composeapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.composeapp.screens.*
import com.example.composeapp.viewmodels.UserProfileViewModel
import com.example.composeapp.viewmodels.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

// Enum cho các route chính
enum class Route(val route: String) {
    SPLASH("splash"),
    LOGIN("login"),
    REGISTER("register"),
    PROFILE_SETUP("profile_setup"),
    HOME("home"),
    MAIN("main"),
    PROFILE("profile"),
    EDIT_PROFILE("edit_profile"),
    CATEGORY_LEVEL("category_level/{categoryName}"),
    CATEGORY_DETAIL("category_detail/{categoryId}/{level}"),
    FLASHCARD_LEARNING("flashcard_learning/{categoryName}/{level}")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userProfileViewModel: UserProfileViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    
    // Observe auth state
    val currentUser by authViewModel.currentUser.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = Route.SPLASH.route
    ) {
        // Màn hình Splash
        composable(Route.SPLASH.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Route.LOGIN.route) {
                        popUpTo(Route.SPLASH.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    // Kiểm tra xem người dùng đã có profile chưa
                    userProfileViewModel.checkUserHasProfile { hasProfile ->
                        if (hasProfile) {
                            navController.navigate(Route.HOME.route) {
                                popUpTo(Route.SPLASH.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Route.PROFILE_SETUP.route) {
                                popUpTo(Route.SPLASH.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        // Màn hình Login
        composable(Route.LOGIN.route) {
            LoginScreen(
                onLoginSuccess = {
                    // Khi đăng nhập thành công, kiểm tra xem người dùng đã thiết lập hồ sơ chưa
                    userProfileViewModel.checkUserHasProfile { hasProfile ->
                        if (hasProfile) {
                            // Nếu đã có hồ sơ, chuyển đến màn hình Home
                            navController.navigate(Route.HOME.route) {
                                popUpTo(Route.LOGIN.route) { inclusive = true }
                            }
                        } else {
                            // Nếu chưa có hồ sơ, chuyển đến màn hình thiết lập hồ sơ
                            navController.navigate(Route.PROFILE_SETUP.route) {
                                popUpTo(Route.LOGIN.route) { inclusive = true }
                            }
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Route.REGISTER.route)
                }
            )
        }
        
        // Màn hình Đăng ký
        composable(Route.REGISTER.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    // Khi đăng ký thành công, chuyển đến màn hình thiết lập hồ sơ
                    navController.navigate(Route.PROFILE_SETUP.route) {
                        popUpTo(Route.LOGIN.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigateUp()
                }
            )
        }
        
        // Màn hình Thiết lập hồ sơ
        composable(Route.PROFILE_SETUP.route) {
            // Kiểm tra xem người dùng đã đăng nhập chưa
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(Route.LOGIN.route) {
                        popUpTo(Route.PROFILE_SETUP.route) { inclusive = true }
                    }
                }
                return@composable
            }
            
            UserProfileSetupScreen(
                onSetupComplete = {
                    // Khi thiết lập hồ sơ xong, chuyển đến màn hình Home
                    navController.navigate(Route.HOME.route) {
                        popUpTo(Route.PROFILE_SETUP.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Màn hình Home - Đã thay đổi thành MainScreen để hiển thị thanh điều hướng
        composable(Route.HOME.route) {
            // Kiểm tra xem người dùng đã đăng nhập chưa
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(Route.LOGIN.route) {
                        popUpTo(Route.HOME.route) { inclusive = true }
                    }
                }
                return@composable
            }
            
            MainScreen(navController = navController)
        }
        
        // Màn hình Main
        composable(Route.MAIN.route) {
            // Kiểm tra xem người dùng đã đăng nhập chưa
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(Route.LOGIN.route) {
                        popUpTo(Route.MAIN.route) { inclusive = true }
                    }
                }
                return@composable
            }
            
            MainScreen(navController = navController)
        }
        
        // Màn hình Profile
        composable(Route.PROFILE.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.navigate(Route.HOME.route) {
                        popUpTo(Route.HOME.route) { inclusive = true }
                    }
                },
                onEditProfile = { navController.navigate(Route.EDIT_PROFILE.route) },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(Route.LOGIN.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }
        
        // Màn hình Edit Profile
        composable(Route.EDIT_PROFILE.route) {
            // Kiểm tra xem người dùng đã đăng nhập chưa
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(Route.LOGIN.route) {
                        popUpTo(Route.EDIT_PROFILE.route) { inclusive = true }
                    }
                }
                return@composable
            }
            
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Màn hình chọn level của category
        composable(
            route = Route.CATEGORY_LEVEL.route,
            arguments = listOf(
                navArgument("categoryName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            CategoryLevelScreen(
                categoryName = categoryName,
                onBackPress = {
                    navController.navigateUp()
                },
                onLevelSelected = { level ->
                    navController.navigate(Route.FLASHCARD_LEARNING.route.replace("{categoryName}", categoryName).replace("{level}", level))
                }
            )
        }
        
        // Màn hình chi tiết category
        composable(
            route = Route.CATEGORY_DETAIL.route,
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.StringType
                },
                navArgument("level") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val level = backStackEntry.arguments?.getString("level") ?: ""
            
            CategoryDetailScreen(
                categoryId = categoryId,
                level = level,
                onBackPress = {
                    navController.navigateUp()
                },
                onNavigateToFlashcards = { category, selectedLevel ->
                    navController.navigate("flashcard_learning/$category/$selectedLevel")
                }
            )
        }

        // Màn hình học flashcard
        composable(
            route = Route.FLASHCARD_LEARNING.route,
            arguments = listOf(
                navArgument("categoryName") {
                    type = NavType.StringType
                },
                navArgument("level") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            val level = backStackEntry.arguments?.getString("level") ?: ""
            
            FlashcardLearningScreen(
                categoryName = categoryName,
                level = level,
                onBackPress = {
                    navController.navigateUp()
                }
            )
        }
    }
} 