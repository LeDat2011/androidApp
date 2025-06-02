package com.example.composeapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeapp.models.JapaneseLevel
import com.example.composeapp.models.StudyTimeOptions
import com.example.composeapp.models.UserProfileData
import com.example.composeapp.viewmodels.UserProfileViewModel
import com.example.composeapp.viewmodels.SaveProfileState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun UserProfileSetupScreen(
    onSetupComplete: () -> Unit = {}
) {
    val profileViewModel: UserProfileViewModel = viewModel()
    
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var currentLevel by remember { mutableStateOf(JapaneseLevel.BEGINNER) }
    var targetLevel by remember { mutableStateOf(JapaneseLevel.N5) }
    var studyTimeIndex by remember { mutableStateOf(1) } // Mặc định 30 phút
    
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()
    
    // Danh sách các level có thể chọn làm mục tiêu dựa trên level hiện tại
    val availableTargetLevels = remember(currentLevel) {
        JapaneseLevel.values().filter { it.ordinal <= currentLevel.ordinal }
    }
    
    // Tự động cập nhật targetLevel khi currentLevel thay đổi
    LaunchedEffect(currentLevel) {
        if (targetLevel.ordinal > currentLevel.ordinal) {
            targetLevel = currentLevel
        }
    }
    
    // Observe save state
    val saveState by profileViewModel.saveProfileState.collectAsState()
    
    // Process save state
    LaunchedEffect(saveState) {
        when (saveState) {
            is SaveProfileState.Success -> {
                isLoading = false
                onSetupComplete()
            }
            is SaveProfileState.Loading -> {
                isLoading = true
                errorMessage = null
            }
            is SaveProfileState.Error -> {
                isLoading = false
                errorMessage = (saveState as SaveProfileState.Error).message
            }
            else -> {
                isLoading = false
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thiết lập hồ sơ") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Heading
                Text(
                    text = "Cung cấp thông tin cá nhân",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "Thông tin của bạn sẽ giúp chúng tôi cá nhân hóa trải nghiệm học tiếng Nhật",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )
                
                // Error message
                AnimatedVisibility(
                    visible = errorMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    errorMessage?.let {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
                
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Họ tên") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Name Icon",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Age
                OutlinedTextField(
                    value = age,
                    onValueChange = { 
                        // Chỉ cho phép nhập số
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            age = it
                        }
                    },
                    label = { Text("Tuổi") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Age Icon",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.clearFocus()
                        }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Current Level
                Text(
                    text = "Trình độ tiếng Nhật hiện tại của bạn",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        JapaneseLevel.values().forEach { level ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (level == currentLevel),
                                        onClick = { currentLevel = level }
                                    )
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (level == currentLevel),
                                    onClick = { currentLevel = level }
                                )
                                Text(
                                    text = level.displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                            if (level != JapaneseLevel.BEGINNER) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Target Level
                Text(
                    text = "Trình độ tiếng Nhật mong muốn đạt được",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        availableTargetLevels.forEach { level ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (level == targetLevel),
                                        onClick = { targetLevel = level }
                                    )
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (level == targetLevel),
                                    onClick = { targetLevel = level }
                                )
                                Text(
                                    text = level.displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                            if (level != availableTargetLevels.last()) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Study Time
                Text(
                    text = "Thời gian học mỗi ngày",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        StudyTimeOptions.options.forEachIndexed { index, option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (index == studyTimeIndex),
                                        onClick = { studyTimeIndex = index }
                                    )
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (index == studyTimeIndex),
                                    onClick = { studyTimeIndex = index }
                                )
                                Text(
                                    text = option.second,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                            if (index < StudyTimeOptions.options.lastIndex) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Submit Button
                Button(
                    onClick = {
                        keyboardController?.hide()
                        validateAndRegister(
                            name = name,
                            age = age.toIntOrNull() ?: 0,
                            currentLevel = currentLevel,
                            targetLevel = targetLevel,
                            studyTimeMinutes = StudyTimeOptions.options[studyTimeIndex].first,
                            onError = { errorMessage = it },
                            onSuccess = { userProfileData ->
                                profileViewModel.saveUserProfile(userProfileData)
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Hoàn tất thiết lập",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

private fun validateAndRegister(
    name: String,
    age: Int,
    currentLevel: JapaneseLevel,
    targetLevel: JapaneseLevel,
    studyTimeMinutes: Int,
    onError: (String) -> Unit,
    onSuccess: (UserProfileData) -> Unit
) {
    when {
        name.isBlank() -> {
            onError("Vui lòng nhập họ tên của bạn")
        }
        age <= 0 -> {
            onError("Vui lòng nhập tuổi hợp lệ")
        }
        targetLevel.ordinal > currentLevel.ordinal -> {
            onError("Trình độ mong muốn phải cao hơn hoặc bằng trình độ hiện tại")
        }
        else -> {
            // Tạo đối tượng UserProfileData
            val userProfile = UserProfileData(
                name = name,
                age = age,
                currentLevel = currentLevel,
                targetLevel = targetLevel,
                studyTimeMinutes = studyTimeMinutes
            )
            
            onSuccess(userProfile)
        }
    }
} 