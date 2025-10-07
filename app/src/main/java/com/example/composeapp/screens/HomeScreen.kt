package com.example.composeapp.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import com.example.composeapp.components.CategoryCard
import com.example.composeapp.components.RecommendedFlashcardComponent
import com.example.composeapp.models.*
import com.example.composeapp.repository.CategoryRepository
import com.example.composeapp.repository.CategoryData
import com.example.composeapp.viewmodels.FlashcardRecommendationViewModel
import com.example.composeapp.viewmodels.UserProfileViewModel
import com.example.composeapp.ui.design.spacing
import com.example.composeapp.ui.design.shapes
import com.example.composeapp.ui.design.elevation
import com.example.composeapp.ui.design.components
import com.example.composeapp.ui.design.CategoryColors
import com.example.composeapp.ui.design.AppGradients
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

// Data class for managing HomeScreen state
data class HomeScreenState(
    val userName: String = "",
    val categories: List<CategoryData> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val selectedCategory: CategoryData? = null,
    val categoryProgress: Map<String, Float> = emptyMap()
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navigateToCategory: (String) -> Unit,
    navigateToProfile: () -> Unit,
    navigateToFlashcardLearning: () -> Unit
) {
    val userProfileViewModel: UserProfileViewModel = viewModel()
    val recommendationViewModel: FlashcardRecommendationViewModel = viewModel()
    val categoryRepository = remember { CategoryRepository() }
    
    val profileData by userProfileViewModel.profileData.collectAsState()
    val recommendedFlashcard by recommendationViewModel.currentRecommendedFlashcard.collectAsState()
    val recommendedFlashcards by recommendationViewModel.recommendedFlashcards.collectAsState()
    val isLoading by recommendationViewModel.isLoading.collectAsState()
    val error by recommendationViewModel.error.collectAsState()
    val categoryProgressMap by userProfileViewModel.categoryProgress.collectAsState()
    
    // Category data from Firebase
    val categories by categoryRepository.categories.collectAsState()
    val categoriesLoading by categoryRepository.isLoading.collectAsState()
    val categoriesError by categoryRepository.error.collectAsState()
    
    // Design System
    val spacing = spacing()
    val shapes = shapes()
    val elevation = elevation()
    val components = components()
    
    // Load user profile data and categories
    LaunchedEffect(Unit) {
        userProfileViewModel.loadUserProfile()
        categoryRepository.loadCategories()
    }
    
    // State for the screen
    var state by remember {
        mutableStateOf(
            HomeScreenState(
                userName = profileData?.name ?: "Người dùng",
                categories = emptyList(),
                isLoading = true,
                categoryProgress = emptyMap()
            )
        )
    }
    
    // Update state when profile data changes
    LaunchedEffect(profileData) {
        state = state.copy(userName = profileData?.name ?: "Người dùng")
    }
    
    // Update state when categories data changes
    LaunchedEffect(categories) {
        state = state.copy(
            categories = categories,
            isLoading = categoriesLoading
        )
    }
    
    // Update progress map when category progress data changes
    LaunchedEffect(categoryProgressMap) {
        state = state.copy(categoryProgress = categoryProgressMap)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trang chủ") },
                actions = {
                    IconButton(onClick = navigateToProfile) {
                        Icon(
                            Icons.Default.Person, 
                            contentDescription = "Profile",
                            modifier = Modifier.size(components.iconSize)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Greeting section
            item {
                JapaneseGreetingHeader(
                    userName = state.userName,
                    modifier = Modifier.padding(spacing.md)
                )
            }
            
            // Recommended Flashcard section (thay thế cho Daily progress section)
            item {
                RecommendedFlashcardSection(
                    flashcard = recommendedFlashcard,
                    isLoading = isLoading,
                    onNext = { recommendationViewModel.moveToNextRecommendedFlashcard() },
                    onPrevious = { recommendationViewModel.moveToPreviousRecommendedFlashcard() },
                    onMarkLearned = { recommendationViewModel.markCurrentFlashcardAsLearned() },
                    onMarkDifficult = { recommendationViewModel.markCurrentFlashcardAsDifficult() },
                    onMarkEasy = { recommendationViewModel.markCurrentFlashcardAsEasy() },
                    canGoNext = recommendedFlashcards.indexOf(recommendedFlashcard) < recommendedFlashcards.size - 1,
                    canGoPrevious = recommendedFlashcards.indexOf(recommendedFlashcard) > 0,
                    onSeeAll = navigateToFlashcardLearning,
                    modifier = Modifier.padding(horizontal = spacing.md)
                )
            }

            // Categories section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.md)
                        .animateItemPlacement()
                ) {
                    Text(
                        text = "Chủ đề học tập",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Chọn một chủ đề để tiếp tục học",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(spacing.md))
                    
                    if (state.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        CategoriesWithProgressGrid(
                            categories = state.categories,
                            progressMap = state.categoryProgress,
                            onCategoryClick = { category ->
                                navigateToCategory(category.id)
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Hiển thị lỗi nếu có
    (error ?: categoriesError)?.let { errorMessage ->
        AlertDialog(
            onDismissRequest = { 
                recommendationViewModel.resetError()
                categoryRepository.resetError()
            },
            title = { Text("Lỗi") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { 
                    recommendationViewModel.resetError()
                    categoryRepository.resetError()
                }) {
                    Text("Đóng")
                }
            }
        )
    }
}

@Composable
fun RecommendedFlashcardSection(
    flashcard: Flashcard?,
    isLoading: Boolean,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onMarkLearned: () -> Unit,
    onMarkDifficult: () -> Unit,
    onMarkEasy: () -> Unit,
    canGoNext: Boolean,
    canGoPrevious: Boolean,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header
            Column {
                Text(
                    text = "Thẻ ghi nhớ đề xuất",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Dựa trên thuật toán học tăng cường",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (flashcard != null) {
                RecommendedFlashcardComponent(
                    flashcard = flashcard,
                    onNext = onNext,
                    onPrevious = onPrevious,
                    onMarkLearned = onMarkLearned,
                    onMarkDifficult = onMarkDifficult,
                    onMarkEasy = onMarkEasy,
                    canGoNext = canGoNext,
                    canGoPrevious = canGoPrevious
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Không có thẻ ghi nhớ nào được đề xuất",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun JapaneseGreetingHeader(
    userName: String,
    modifier: Modifier = Modifier
) {
    val spacing = spacing()
    val shapes = shapes()
    
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "おはようございます" // Good morning
            hour < 18 -> "こんにちは" // Good afternoon
            else -> "こんばんは" // Good evening
        }
    }
    
    // Greeting card with beautiful gradient
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing.sm),
        shape = shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = AppGradients.primaryGradient
                    ),
                    shape = shapes.large
                )
                .padding(spacing.md)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = greeting,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = userName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun CategoriesWithProgressGrid(
    categories: List<CategoryData>,
    progressMap: Map<String, Float>,
    onCategoryClick: (CategoryData) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp) // Fixed height for visibility
    ) {
        items(categories) { category ->
            CategoryWithProgressCard(
                category = category,
                progress = progressMap[category.id] ?: 0f,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@Composable
fun CategoryWithProgressCard(
    category: CategoryData,
    progress: Float,
    onClick: () -> Unit
) {
    val spacing = spacing()
    val shapes = shapes()
    val elevation = elevation()
    
    // Get a color from Firebase data or fallback to default
    val cardColor = try {
        Color(android.graphics.Color.parseColor(category.color))
    } catch (e: Exception) {
        CategoryColors.misc
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = cardColor.copy(alpha = 0.8f)
        ),
        shape = shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.medium)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.md)
        ) {
            // Category emoji or icon at the top
            val emoji = when (category.id.lowercase()) {
                "animals" -> "🐱"
                "food" -> "🍣"
                "transportation" -> "🚆"
                "weather" -> "☀️"
                "family" -> "👪"
                "colors" -> "🎨"
                "numbers" -> "🔢"
                "time" -> "🕒"
                "daily_life" -> "🏠"
                else -> "📚"
            }
            
            Text(
                text = emoji,
                fontSize = 40.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
            )
            
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
            ) {
                // Category name
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 20.sp
                )
                
                Spacer(modifier = Modifier.height(spacing.sm))
                
                // Progress bar
                // Removed progress bar and percentage as requested
            }
        }
    }
}

@Composable
fun CategoryProgressSummary(
    category: FlashcardCategory,
    progress: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Category title
            Text(
                text = "Tiến trình học: ${category.displayName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProgressStat(
                    value = (progress * 100).toInt().toString() + "%",
                    label = "Hoàn thành",
                    icon = Icons.Default.CheckCircle
                )
                
                ProgressStat(
                    value = ((Math.random() * 50).toInt() + 10).toString(),
                    label = "Từ vựng",
                    icon = Icons.Default.Book
                )
                
                ProgressStat(
                    value = ((Math.random() * 24).toInt() + 1).toString(),
                    label = "Ngày học",
                    icon = Icons.Default.CalendarToday
                )
            }
        }
    }
}

@Composable
fun ProgressStat(
    value: String,
    label: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}