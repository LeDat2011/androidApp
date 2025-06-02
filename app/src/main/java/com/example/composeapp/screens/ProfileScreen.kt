package com.example.composeapp.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeapp.models.*
import com.example.composeapp.ui.theme.ComposeAppTheme
import com.example.composeapp.viewmodels.UserProfileViewModel
import com.example.composeapp.viewmodels.AuthViewModel
import com.example.composeapp.viewmodels.LoadProfileState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val viewModel: UserProfileViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val profileData by viewModel.profileData.collectAsState()
    val loadProfileState by viewModel.loadProfileState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Load user profile data
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }
    
    // Observe auth state for logout
    val currentUser by authViewModel.currentUser.collectAsState()
    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            onLogout()
        }
    }
    
    // Handle error messages
    LaunchedEffect(loadProfileState) {
        if (loadProfileState is LoadProfileState.Error) {
            snackbarHostState.showSnackbar(
                message = (loadProfileState as LoadProfileState.Error).message,
                duration = SnackbarDuration.Short
            )
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ cá nhân") },
                navigationIcon = {
                    IconButton(onClick = {
                        onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditProfile) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                    IconButton(onClick = {
                        authViewModel.signOut()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.95f)
                )
            )
        }
    ) { paddingValues ->
        when (loadProfileState) {
            is LoadProfileState.Loading -> {
                LoadingScreen(paddingValues)
            }
            is LoadProfileState.Error -> {
                ErrorScreen(
                    padding = paddingValues,
                    message = (loadProfileState as LoadProfileState.Error).message,
                    onRetry = { viewModel.loadUserProfile() }
                )
            }
            is LoadProfileState.Success -> {
                ProfileContent(
                    padding = paddingValues,
                    scrollState = scrollState,
                    profileData = profileData
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ErrorScreen(
    padding: PaddingValues,
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Thử lại")
            }
        }
    }
}

@Composable
private fun ProfileContent(
    padding: PaddingValues,
    scrollState: ScrollState,
    profileData: UserProfileData?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with gradient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Avatar with border animation
                val rotation by rememberInfiniteTransition().animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(5000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(
                            width = 3.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.tertiary
                                ),
                                center = Offset(60f, 60f)
                            ),
                            shape = CircleShape
                        )
                        .rotate(rotation)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(24.dp)
                                .size(72.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User name with animation
                Text(
                    text = profileData?.name ?: "Chưa cập nhật",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Text(
                    text = "Tuổi: ${profileData?.age ?: "Chưa cập nhật"}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }

        // Stats Cards with animation
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + expandVertically()
        ) {
            StatsGrid(profileData = profileData)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Learning Progress
        LearningProgressSection(profileData = profileData)

        Spacer(modifier = Modifier.height(16.dp))

        // Statistics
        StatisticsSection(profileData = profileData)
    }
}

@Composable
private fun StatsGrid(profileData: UserProfileData?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                icon = Icons.Default.CalendarMonth,
                label = "Ngày học",
                value = "${profileData?.daysActive ?: 0}",
                color = MaterialTheme.colorScheme.primary
            )
            StatCard(
                icon = Icons.Default.Book,
                label = "Từ vựng",
                value = "${profileData?.wordsLearned ?: 0}",
                color = MaterialTheme.colorScheme.secondary
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                icon = Icons.Default.Whatshot,
                label = "Streak",
                value = "${profileData?.streak ?: 0}",
                color = MaterialTheme.colorScheme.error
            )
            StatCard(
                icon = Icons.Default.School,
                label = "Bài học",
                value = "${profileData?.lessonsCompleted ?: 0}",
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    var animatedValue by remember { mutableStateOf(0) }
    val targetValue = value.toIntOrNull() ?: 0
    
    LaunchedEffect(targetValue) {
        animate(
            initialValue = animatedValue.toFloat(),
            targetValue = targetValue.toFloat(),
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        ) { currentValue, _ ->
            animatedValue = currentValue.toInt()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = animatedValue.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LearningProgressSection(profileData: UserProfileData?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Tiến độ học tập",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            LearningProgressItem(
                icon = Icons.Default.School,
                label = "Trình độ hiện tại",
                value = profileData?.currentLevel?.displayName ?: "N5",
                color = MaterialTheme.colorScheme.primary
            )

            LearningProgressItem(
                icon = Icons.Default.Flag,
                label = "Mục tiêu",
                value = profileData?.targetLevel?.displayName ?: "N4",
                color = MaterialTheme.colorScheme.secondary
            )

            LearningProgressItem(
                icon = Icons.Default.Timer,
                label = "Thời gian học mỗi ngày",
                value = "${profileData?.studyTimeMinutes ?: 30} phút",
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
private fun LearningProgressItem(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = color.copy(alpha = 0.1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StatisticsSection(profileData: UserProfileData?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Chi tiết thống kê",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatisticRow(
                icon = Icons.Default.Whatshot,
                label = "Streak hiện tại",
                value = "${profileData?.streak ?: 0} ngày",
                color = MaterialTheme.colorScheme.error
            )

            StatisticRow(
                icon = Icons.AutoMirrored.Filled.Assignment,
                label = "Từ vựng đã học",
                value = "${profileData?.wordsLearned ?: 0} từ",
                color = MaterialTheme.colorScheme.secondary
            )

            StatisticRow(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                label = "Bài học đã hoàn thành",
                value = "${profileData?.lessonsCompleted ?: 0} bài",
                color = MaterialTheme.colorScheme.tertiary
            )

            StatisticRow(
                icon = Icons.Default.CalendarMonth,
                label = "Số ngày hoạt động",
                value = "${profileData?.daysActive ?: 0} ngày",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun StatisticRow(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = color.copy(alpha = 0.1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Preview(showBackground = true, name = "Profile Screen")
@Composable
fun ProfileScreenPreview() {
    ComposeAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ProfileScreen(
                onNavigateBack = {},
                onEditProfile = {},
                onLogout = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Profile Screen - Dark")
@Composable
fun ProfileScreenDarkPreview() {
    ComposeAppTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ProfileScreen(
                onNavigateBack = {},
                onEditProfile = {},
                onLogout = {}
            )
        }
    }
} 