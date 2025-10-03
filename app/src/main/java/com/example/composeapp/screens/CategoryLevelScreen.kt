package com.example.composeapp.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeapp.viewmodels.QuizViewModel
import com.example.composeapp.repository.CategoryRepository
import com.example.composeapp.repository.LevelData
import kotlinx.coroutines.delay
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryLevelScreen(
    categoryName: String,
    onBackPress: () -> Unit,
    onLevelSelected: (String) -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val categoryRepository = remember { CategoryRepository() }
    val categories by categoryRepository.categories.collectAsState()
    val isLoading by categoryRepository.isLoading.collectAsState()
    val error by categoryRepository.error.collectAsState()

    // Tải categories để lấy danh sách levels cho category hiện tại
    LaunchedEffect(Unit) {
        categoryRepository.loadCategories()
    }
    // Tiến độ theo level cho category hiện tại
    var levelProgress by remember { mutableStateOf<Map<String, Float>>(emptyMap()) }

    // Tải tiến độ học theo level: dựa vào số từ đã học / tổng số từ mỗi level trong category
    LaunchedEffect(categoryName, categories) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val db = FirebaseDatabase.getInstance().reference

        // Khi chưa có user hoặc chưa có danh mục, để trống tiến độ
        if (currentUser == null) {
            levelProgress = emptyMap()
            return@LaunchedEffect
        }

        try {
            val normalizedCategory = categoryName.trim().lowercase()

            // 1) Lấy toàn bộ vocabulary
            val vocabSnap = db.child("app_data").child("vocabulary").get().await()

            // 2) Lấy map từ đã học của user
            val learnedSnap = db.child("app_data")
                .child("users")
                .child(currentUser.uid)
                .child("learning")
                .child("vocabulary")
                .get()
                .await()

            val learnedIds = learnedSnap.children
                .filter { child -> child.getValue(Boolean::class.java) == true }
                .mapNotNull { child -> child.key }
                .toSet()

            // 3) Tính tổng/đã học theo level trong category
            val totalByLevel = mutableMapOf<String, Int>()
            val learnedByLevel = mutableMapOf<String, Int>()

            for (v in vocabSnap.children) {
                val id = v.key ?: continue
                val level = v.child("level").getValue(String::class.java)?.trim().orEmpty()
                val categoriesNode = v.child("categories")
                val categoriesOfWord = categoriesNode.children.mapNotNull { child -> child.getValue(String::class.java) }
                val matchesCategory = categoriesOfWord.any { cat -> cat.trim().lowercase() == normalizedCategory }
                if (!matchesCategory || level.isBlank()) continue

                totalByLevel[level] = (totalByLevel[level] ?: 0) + 1
                if (learnedIds.contains(id)) {
                    learnedByLevel[level] = (learnedByLevel[level] ?: 0) + 1
                }
            }

            // 4) Chuẩn hóa theo các level hiển thị
            val availableLevels = categories
                .firstOrNull { it.id == categoryName || it.name == categoryName || it.displayName == categoryName }
                ?.levels
                ?.map { it.id }
                ?: listOf("N5", "N4", "N3", "N2", "N1")

            levelProgress = availableLevels.associateWith { lv ->
                val total = totalByLevel[lv] ?: 0
                val learned = learnedByLevel[lv] ?: 0
                if (total == 0) 0f else (learned.toFloat() / total.toFloat()).coerceIn(0f, 1f)
            }
        } catch (_: Exception) {
            // Nếu lỗi, để tiến độ rỗng (0f)
            levelProgress = emptyMap()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Chọn trình độ",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val currentCategory = categories.firstOrNull { it.id == categoryName || it.name == categoryName || it.displayName == categoryName }
                val levels = (currentCategory?.levels ?: emptyList()).let { parsedLevels ->
                    if (parsedLevels.isNotEmpty()) parsedLevels else listOf(
                        LevelData(id = "N5", name = "N5", description = "Sơ cấp", color = "#4CAF50"),
                        LevelData(id = "N4", name = "N4", description = "Sơ trung cấp", color = "#8BC34A"),
                        LevelData(id = "N3", name = "N3", description = "Trung cấp", color = "#FFC107"),
                        LevelData(id = "N2", name = "N2", description = "Trung cao cấp", color = "#FF9800"),
                        LevelData(id = "N1", name = "N1", description = "Cao cấp", color = "#F44336")
                    )
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(levels) { level ->
                        LevelCard(
                            level = level,
                            progress = levelProgress[level.id] ?: 0f,
                            onClick = { onLevelSelected(level.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelCard(
    level: LevelData,
    progress: Float,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Card(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .scale(scale),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(listOf(
                        Color(android.graphics.Color.parseColor(level.color)),
                        Color(android.graphics.Color.parseColor(level.color)).copy(alpha = 0.7f)
                    ))
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Icon
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when(level.id) {
                                "N5" -> Icons.Default.Star
                                "N4" -> Icons.Default.AutoStories
                                "N3" -> Icons.Default.School
                                "N2" -> Icons.Default.Psychology
                                "N1" -> Icons.Default.EmojiEvents
                                else -> Icons.Default.Category
                            },
                            contentDescription = level.name,
                            tint = Color.White
                        )
                    }
                }

                // Level name
                Text(
                    text = level.name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                // Description
                Text(
                    text = level.description,
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
} 