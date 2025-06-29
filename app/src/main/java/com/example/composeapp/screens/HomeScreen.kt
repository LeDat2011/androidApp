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
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
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
import com.example.composeapp.components.FlashcardComponent
import com.example.composeapp.models.*
import com.example.composeapp.viewmodels.UserProfileViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

// Data class for managing HomeScreen state
data class HomeScreenState(
    val userName: String = "",
    val dailyProgress: Float = 0f,
    val flashcards: List<Flashcard> = emptyList(),
    val categories: List<FlashcardCategory> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val selectedCategory: FlashcardCategory? = null,
    val categoryProgress: Map<FlashcardCategory, Float> = emptyMap()
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navigateToCategory: (String) -> Unit,
    navigateToProfile: () -> Unit
) {
    val userProfileViewModel: UserProfileViewModel = viewModel()
    val profileData by userProfileViewModel.profileData.collectAsState()
    
    // Load user profile data
    LaunchedEffect(Unit) {
        userProfileViewModel.loadUserProfile()
    }
    
    // State for the screen
    var state by remember {
        mutableStateOf(
            HomeScreenState(
                userName = profileData?.name ?: "Người dùng",
                dailyProgress = 0.7f,
                flashcards = SampleData.flashcards,
                categories = FlashcardCategory.values().toList(),
                isLoading = false,
                categoryProgress = FlashcardCategory.values().associateWith { 
                    // Random progress for each category between 0.0 and 1.0
                    (0..100).random() / 100f
                }
            )
        )
    }
    
    // Update state when profile data changes
    LaunchedEffect(profileData) {
        state = state.copy(userName = profileData?.name ?: "Người dùng")
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trang chủ") },
                actions = {
                    IconButton(onClick = navigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
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
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            // Daily progress section
            item {
                DailyProgressSection(
                    progress = state.dailyProgress,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Categories section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
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
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    CategoriesWithProgressGrid(
                        categories = state.categories,
                        progressMap = state.categoryProgress,
                        onCategoryClick = { category ->
                            navigateToCategory(category.name)
                        }
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
            .padding(8.dp), // Reduced padding
        shape = RoundedCornerShape(16.dp), // Smaller corner radius
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
                        colors = listOf(
                            Color(0xFFFF5B94),
                            Color(0xFF8441A4)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp) // Smaller corner radius
                )
                .padding(16.dp) // Reduced padding
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = greeting,
                    color = Color.White,
                    fontSize = 20.sp, // Smaller font size
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = userName,
                    color = Color.White,
                    fontSize = 16.sp, // Smaller font size
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun DailyFlashcards(
    flashcards: List<Flashcard>,
    currentIndex: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (flashcards.isEmpty()) return
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Convert Flashcard to FlashcardData
        val flashcardData = FlashcardData(
            word = flashcards[currentIndex].japaneseWord,
            reading = flashcards[currentIndex].reading,
            meaning = flashcards[currentIndex].vietnameseMeaning,
            example = if (flashcards[currentIndex].examples.isNotEmpty()) 
                flashcards[currentIndex].examples[0].japanese else "",
            exampleMeaning = if (flashcards[currentIndex].examples.isNotEmpty()) 
                flashcards[currentIndex].examples[0].vietnamese else ""
        )
        
        FlashcardComponent(
            flashcard = flashcardData,
            onNext = onNext,
            onPrevious = onPrevious,
            canGoNext = currentIndex < flashcards.size - 1,
            canGoPrevious = currentIndex > 0,
            onMarkLearned = { /* Xử lý đánh dấu đã học */ }
        )
    }
}

@Composable
fun CategoriesWithProgressGrid(
    categories: List<FlashcardCategory>,
    progressMap: Map<FlashcardCategory, Float>,
    onCategoryClick: (FlashcardCategory) -> Unit,
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
                progress = progressMap[category] ?: 0f,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@Composable
fun CategoryWithProgressCard(
    category: FlashcardCategory,
    progress: Float,
    onClick: () -> Unit
) {
    // Get a color based on the category name - for visual variety
    val cardColor = when (category) {
        FlashcardCategory.ANIMALS -> Color(0xFF42A5F5)  // Blue
        FlashcardCategory.FOOD -> Color(0xFFFF7043)     // Orange
        FlashcardCategory.TRANSPORTATION -> Color(0xFF66BB6A) // Green
        FlashcardCategory.FAMILY -> Color(0xFFAB47BC)   // Purple
        FlashcardCategory.WEATHER -> Color(0xFF26C6DA)  // Teal
        FlashcardCategory.DAILY_LIFE -> Color(0xFFFFCA28) // Amber
        else -> Color(0xFF78909C)                       // Blue Grey
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = cardColor.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Category emoji or icon at the top
            val emoji = when (category) {
                FlashcardCategory.ANIMALS -> "🐱"
                FlashcardCategory.FOOD -> "🍣"
                FlashcardCategory.TRANSPORTATION -> "🚆"
                FlashcardCategory.WEATHER -> "☀️"
                FlashcardCategory.FAMILY -> "👪"
                FlashcardCategory.COLORS -> "🎨"
                FlashcardCategory.NUMBERS -> "🔢"
                FlashcardCategory.TIME -> "🕒"
                FlashcardCategory.DAILY_LIFE -> "🏠"
                else -> "📚"
            }
            
            Text(
                text = emoji,
                fontSize = 32.sp,
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Progress bar
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(1000),
                    label = "category_progress"
                )
                
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Progress percentage
                Text(
                    text = "${(animatedProgress * 100).toInt()}% hoàn thành",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
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

@Composable
fun DailyProgressSection(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tiến độ hôm nay",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressStatItem(
                    icon = Icons.Default.School,
                    value = "10",
                    label = "Từ mới"
                )
                ProgressStatItem(
                    icon = Icons.Default.CalendarToday,
                    value = "5",
                    label = "Ngày liên tiếp"
                )
                ProgressStatItem(
                    icon = Icons.Default.MenuBook,
                    value = "3",
                    label = "Bài học"
                )
            }
        }
    }
}

@Composable
private fun ProgressStatItem(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}