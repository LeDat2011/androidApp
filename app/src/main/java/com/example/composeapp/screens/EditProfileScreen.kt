package com.example.composeapp.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeapp.models.JapaneseLevel
import com.example.composeapp.models.StudyTimeOptions
import com.example.composeapp.models.UserProfileData
import com.example.composeapp.viewmodels.UserProfileViewModel
import com.example.composeapp.viewmodels.SaveProfileState
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // ViewModel
    val viewModel: UserProfileViewModel = viewModel()
    val profileData by viewModel.profileData.collectAsState()
    val saveState by viewModel.saveProfileState.collectAsState()
    
    // UI States
    var name by remember { mutableStateOf(profileData?.name ?: "") }
    var age by remember { mutableStateOf(profileData?.age?.toString() ?: "") }
    var currentLevelIndex by remember { mutableStateOf(JapaneseLevel.values().indexOf(profileData?.currentLevel ?: JapaneseLevel.N5)) }
    var targetLevelIndex by remember { mutableStateOf(JapaneseLevel.values().indexOf(profileData?.targetLevel ?: JapaneseLevel.N4)) }
    var studyTimeIndex by remember { mutableStateOf(StudyTimeOptions.options.indexOfFirst { it.first == profileData?.studyTimeMinutes }.coerceAtLeast(0)) }
    var isLoading by remember { mutableStateOf(false) }
    
    // Dropdown states
    var isCurrentLevelExpanded by remember { mutableStateOf(false) }
    var isTargetLevelExpanded by remember { mutableStateOf(false) }
    var isStudyTimeExpanded by remember { mutableStateOf(false) }
    
    // Load profile data when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }
    
    // Update local state when profile data changes
    LaunchedEffect(profileData) {
        profileData?.let { data ->
            name = data.name
            age = data.age.toString()
            currentLevelIndex = JapaneseLevel.values().indexOf(data.currentLevel).coerceAtLeast(0)
            targetLevelIndex = JapaneseLevel.values().indexOf(data.targetLevel).coerceAtLeast(0)
            studyTimeIndex = StudyTimeOptions.options.indexOfFirst { it.first == data.studyTimeMinutes }
                .coerceAtLeast(0)
        }
    }
    
    // Handle save state changes
    LaunchedEffect(saveState) {
        when (saveState) {
            is SaveProfileState.Success -> {
                isLoading = false
                snackbarHostState.showSnackbar("Cập nhật thông tin thành công")
                viewModel.resetState()
                onNavigateBack()
            }
            is SaveProfileState.Loading -> {
                isLoading = true
            }
            is SaveProfileState.Error -> {
                isLoading = false
                snackbarHostState.showSnackbar(
                    message = (saveState as SaveProfileState.Error).message,
                    withDismissAction = true
                )
                viewModel.resetState()
            }
            else -> {
                isLoading = false
            }
        }
    }
    
    // Validation
    fun validateForm(): Boolean {
        if (name.isBlank()) {
            scope.launch {
                snackbarHostState.showSnackbar("Vui lòng nhập họ tên")
            }
            return false
        }
        
        if (age.isBlank() || age.toIntOrNull() == null || age.toInt() <= 0) {
            scope.launch {
                snackbarHostState.showSnackbar("Vui lòng nhập tuổi hợp lệ")
            }
            return false
        }
        
        return true
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa hồ sơ") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            keyboardController?.hide()
                            if (validateForm()) {
                                val profile = UserProfileData(
                                    name = name,
                                    age = age.toInt(),
                                    currentLevel = JapaneseLevel.values()[currentLevelIndex],
                                    targetLevel = JapaneseLevel.values()[targetLevelIndex],
                                    studyTimeMinutes = StudyTimeOptions.options[studyTimeIndex].first,
                                    streak = profileData?.streak ?: 0,
                                    wordsLearned = profileData?.wordsLearned ?: 0,
                                    lessonsCompleted = profileData?.lessonsCompleted ?: 0,
                                    daysActive = profileData?.daysActive ?: 0,
                                    registrationDate = profileData?.registrationDate ?: System.currentTimeMillis(),
                                    lastActiveDate = System.currentTimeMillis(),
                                    avatarUrl = profileData?.avatarUrl,
                                    userId = profileData?.userId ?: ""
                                )
                                viewModel.saveUserProfile(profile)
                            }
                        },
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Save, "Lưu")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Profile Icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Name field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Họ tên") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Age field
            OutlinedTextField(
                value = age,
                onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) age = it },
                label = { Text("Tuổi") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Cake, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Japanese Level Selection
            Text(
                text = "Trình độ tiếng Nhật",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Current Level
            ExposedDropdownMenuBox(
                expanded = isCurrentLevelExpanded,
                onExpandedChange = { isCurrentLevelExpanded = it }
            ) {
                OutlinedTextField(
                    value = JapaneseLevel.values()[currentLevelIndex].displayName,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Trình độ hiện tại") },
                    leadingIcon = {
                        Icon(Icons.Default.School, contentDescription = null)
                    },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                DropdownMenu(
                    expanded = isCurrentLevelExpanded,
                    onDismissRequest = { isCurrentLevelExpanded = false }
                ) {
                    JapaneseLevel.values().reversed().forEachIndexed { index, level ->
                        DropdownMenuItem(
                            text = { Text(level.displayName) },
                            onClick = { 
                                currentLevelIndex = JapaneseLevel.values().size - 1 - index
                                isCurrentLevelExpanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Target Level
            ExposedDropdownMenuBox(
                expanded = isTargetLevelExpanded,
                onExpandedChange = { isTargetLevelExpanded = it }
            ) {
                OutlinedTextField(
                    value = JapaneseLevel.values()[targetLevelIndex].displayName,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Mục tiêu") },
                    leadingIcon = {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null)
                    },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                DropdownMenu(
                    expanded = isTargetLevelExpanded,
                    onDismissRequest = { isTargetLevelExpanded = false }
                ) {
                    JapaneseLevel.values().reversed().forEachIndexed { index, level ->
                            DropdownMenuItem(
                                text = { Text(level.displayName) },
                                onClick = { 
                                targetLevelIndex = JapaneseLevel.values().size - 1 - index
                                    isTargetLevelExpanded = false
                                }
                            )
                        }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Study Time Selection
            Text(
                text = "Thời gian học mỗi ngày",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ExposedDropdownMenuBox(
                expanded = isStudyTimeExpanded,
                onExpandedChange = { isStudyTimeExpanded = it }
            ) {
                OutlinedTextField(
                    value = StudyTimeOptions.options[studyTimeIndex].second,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Thời gian học") },
                    leadingIcon = {
                        Icon(Icons.Default.Timer, contentDescription = null)
                    },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                DropdownMenu(
                    expanded = isStudyTimeExpanded,
                    onDismissRequest = { isStudyTimeExpanded = false }
                ) {
                    StudyTimeOptions.options.forEachIndexed { index, option ->
                        DropdownMenuItem(
                            text = { Text(option.second) },
                            onClick = { 
                                studyTimeIndex = index
                                isStudyTimeExpanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isEnabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            enabled = isEnabled,
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
            )
        )
        
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
} 