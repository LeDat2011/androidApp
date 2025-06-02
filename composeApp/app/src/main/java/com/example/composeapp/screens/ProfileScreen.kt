package com.example.composeapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.composeapp.models.StudyTimeOptions
import androidx.compose.ui.tooling.preview.Preview
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
    val userProfileViewModel: UserProfileViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val profileData by userProfileViewModel.profileData.collectAsState()
    val loadProfileState by userProfileViewModel.loadProfileState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Load user profile data
    LaunchedEffect(Unit) {
        userProfileViewModel.loadUserProfile()
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
                }
            )
        }
    ) { paddingValues ->
        when (loadProfileState) {
            is LoadProfileState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is LoadProfileState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = (loadProfileState as LoadProfileState.Error).message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { userProfileViewModel.loadUserProfile() }) {
                            Text("Thử lại")
                        }
                    }
                }
            }
            is LoadProfileState.Success -> {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            Surface(
                modifier = Modifier.size(120.dp),
                shape = MaterialTheme.shapes.large,
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
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // User Info
            Text(
                text = profileData?.name ?: "Chưa cập nhật",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Tuổi: ${profileData?.age ?: "Chưa cập nhật"}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Japanese Level Info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Thông tin học tập",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    StatisticRow(
                        icon = Icons.Default.School,
                        label = "Trình độ hiện tại",
                        value = profileData?.currentLevel?.displayName ?: "N5"
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    StatisticRow(
                        icon = Icons.Default.EmojiEvents,
                        label = "Mục tiêu",
                        value = profileData?.targetLevel?.displayName ?: "N4"
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    StatisticRow(
                        icon = Icons.Default.Timer,
                        label = "Thời gian học mỗi ngày",
                        value = profileData?.studyTimeMinutes?.let { minutes ->
                            StudyTimeOptions.options.find { it.first == minutes }?.second
                        } ?: "30 phút"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Learning Statistics
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Thống kê",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    StatisticRow(
                        icon = Icons.Default.Whatshot,
                        label = "Streak",
                        value = "${profileData?.streak ?: 0} ngày"
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    StatisticRow(
                                icon = Icons.AutoMirrored.Filled.Assignment,
                        label = "Từ vựng đã học",
                        value = "${profileData?.wordsLearned ?: 0} từ"
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    StatisticRow(
                                icon = Icons.AutoMirrored.Filled.MenuBook,
                        label = "Bài học đã hoàn thành",
                        value = "${profileData?.lessonsCompleted ?: 0} bài"
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    StatisticRow(
                        icon = Icons.Default.CalendarMonth,
                        label = "Số ngày hoạt động",
                        value = "${profileData?.daysActive ?: 0} ngày"
                    )
                }
            }
                }
            }
        }
    }
}

@Composable
private fun StatisticRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
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