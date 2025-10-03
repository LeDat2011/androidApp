package com.example.composeapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeapp.models.*
import com.example.composeapp.viewmodels.LevelSystemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelProgressionScreen(
    viewModel: LevelSystemViewModel = viewModel()
) {
    val userLevelInfo by viewModel.userLevelInfo.collectAsState()
    val currentLessons by viewModel.currentLessons.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "🎯 Tiến độ học tập",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Thông tin level hiện tại
                CurrentLevelCard(userLevelInfo = userLevelInfo)
            }
            
            item {
                // Danh sách các level
                LevelListCard(
                    userLevelInfo = userLevelInfo,
                    onLevelClick = { level, category ->
                        // TODO: Navigate to level content
                    }
                )
            }
            
            item {
                // Danh sách lessons hiện tại
                CurrentLessonsCard(
                    lessons = currentLessons,
                    onLessonClick = { lesson ->
                        // TODO: Navigate to lesson
                    }
                )
            }
            
            item {
                // Achievements
                AchievementsCard(achievements = achievements)
            }
        }
    }
}

@Composable
private fun CurrentLevelCard(userLevelInfo: UserLevelInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Level hiện tại",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = userLevelInfo.currentLevel.displayName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                // Level badge
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = userLevelInfo.currentLevel.displayName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "XP",
                    value = userLevelInfo.totalExperience.toString(),
                    icon = "⭐"
                )
                StatItem(
                    label = "Chuỗi",
                    value = "${userLevelInfo.studyStreak} ngày",
                    icon = "🔥"
                )
                StatItem(
                    label = "Thời gian",
                    value = "${userLevelInfo.totalStudyTime / 60000} phút",
                    icon = "⏱️"
                )
            }
        }
    }
}

@Composable
private fun LevelListCard(
    userLevelInfo: UserLevelInfo,
    onLevelClick: (JLPTLevel, LearningCategory) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📚 Các Level",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(JLPTLevel.values()) { level ->
                    LevelCard(
                        level = level,
                        isUnlocked = userLevelInfo.isLevelUnlocked(level),
                        isCurrentLevel = level == userLevelInfo.currentLevel,
                        onClick = {
                            if (userLevelInfo.isLevelUnlocked(level)) {
                                onLevelClick(level, LearningCategory.VOCABULARY)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelCard(
    level: JLPTLevel,
    isUnlocked: Boolean,
    isCurrentLevel: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isCurrentLevel -> MaterialTheme.colorScheme.primary
        isUnlocked -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val textColor = when {
        isCurrentLevel -> MaterialTheme.colorScheme.onPrimary
        isUnlocked -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier
            .width(120.dp)
            .height(80.dp),
        enabled = isUnlocked
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isUnlocked) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Text(
                text = level.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            
            if (isCurrentLevel) {
                Text(
                    text = "Hiện tại",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor
                )
            }
        }
    }
}

@Composable
private fun CurrentLessonsCard(
    lessons: List<Lesson>,
    onLessonClick: (Lesson) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📖 Bài học hiện tại",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (lessons.isEmpty()) {
                Text(
                    text = "Không có bài học nào",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                lessons.take(5).forEach { lesson ->
                    LessonItem(
                        lesson = lesson,
                        onClick = { onLessonClick(lesson) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun LessonItem(
    lesson: Lesson,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (lesson.isCompleted) 
                Color(0xFF4CAF50).copy(alpha = 0.1f)
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = lesson.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${lesson.estimatedTime} phút",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                if (lesson.isCompleted) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementsCard(achievements: List<LevelAchievement>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🏆 Thành tích",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val unlockedAchievements = achievements.filter { it.isUnlocked }
            val lockedAchievements = achievements.filter { !it.isUnlocked }
            
            if (unlockedAchievements.isNotEmpty()) {
                Text(
                    text = "Đã đạt được (${unlockedAchievements.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(unlockedAchievements) { achievement ->
                        AchievementBadge(achievement = achievement, isUnlocked = true)
                    }
                }
            }
            
            if (lockedAchievements.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Chưa đạt được (${lockedAchievements.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(lockedAchievements.take(3)) { achievement ->
                        AchievementBadge(achievement = achievement, isUnlocked = false)
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementBadge(
    achievement: LevelAchievement,
    isUnlocked: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) 
                Color(0xFFFFD700).copy(alpha = 0.2f)
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.size(80.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = achievement.icon,
                fontSize = 24.sp
            )
            
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = if (isUnlocked) 
                    MaterialTheme.colorScheme.onSurface 
                else 
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 20.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}
