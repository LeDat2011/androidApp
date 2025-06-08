package com.example.composeapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeapp.components.JapaneseAlphabetRow
import com.example.composeapp.components.JapaneseCharacterDetailCard
import com.example.composeapp.models.AlphabetType
import com.example.composeapp.models.JapaneseCharacter
import com.example.composeapp.viewmodels.JapaneseAlphabetViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(modifier: Modifier = Modifier) {
    AlphabetContent()
}

@Composable
private fun AlphabetContent() {
    // ViewModel
    val viewModel: JapaneseAlphabetViewModel = viewModel()
    
    // State
    val alphabetType by viewModel.currentAlphabetType
    val selectedCharacter by viewModel.selectedCharacter
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val characterImageUrl by viewModel.characterImageUrl.collectAsState()
    
    // Rows of characters
    val characterRows = viewModel.getCharacterRows()
    val alphabetName = viewModel.getCurrentAlphabetName()
    
    // Scaffold và UI cơ bản
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with alphabet title
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bảng chữ cái tiếng Nhật",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = alphabetName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        // Alphabet type selection
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Hiragana button
            FilterChip(
                selected = alphabetType == AlphabetType.HIRAGANA,
                onClick = { viewModel.switchAlphabetType(AlphabetType.HIRAGANA) },
                label = { Text("Hiragana") },
                leadingIcon = {
                    if (alphabetType == AlphabetType.HIRAGANA) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                }
            )
            
            // Katakana button
            FilterChip(
                selected = alphabetType == AlphabetType.KATAKANA,
                onClick = { viewModel.switchAlphabetType(AlphabetType.KATAKANA) },
                label = { Text("Katakana") },
                leadingIcon = {
                    if (alphabetType == AlphabetType.KATAKANA) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                }
            )

            // Kanji button
            FilterChip(
                selected = alphabetType == AlphabetType.KANJI,
                onClick = { viewModel.switchAlphabetType(AlphabetType.KANJI) },
                label = { Text("Kanji") },
                leadingIcon = {
                    if (alphabetType == AlphabetType.KANJI) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                }
            )
        }
        
        // Bảng chữ cái
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            // Hiển thị từng hàng chữ cái
            characterRows.forEach { (rowTitle, characters) ->
                item {
                    JapaneseAlphabetRow(
                        title = rowTitle, 
                        characters = characters,
                        onCharacterClick = { character ->
                            viewModel.selectCharacter(character)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // Phần đệm cuối
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    
    // Dialog hiển thị chi tiết ký tự khi được chọn
    selectedCharacter?.let { character ->
        Dialog(onDismissRequest = { viewModel.closeCharacterDetail() }) {
            JapaneseCharacterDetailCard(
                character = character,
                onClose = { viewModel.closeCharacterDetail() }
            )
        }
    }
    
    // Hiển thị loading indicator
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    
    // Hiển thị error message nếu có
    errorMessage?.let { message ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
} 