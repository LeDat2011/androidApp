package com.example.composeapp.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.composeapp.components.FlashcardComponent
import com.example.composeapp.screens.FlashcardData

@Composable
fun FlashcardTab() {
    var currentIndex by remember { mutableStateOf(0) }
    
    // Create sample flashcard data
    val flashcards = remember {
        listOf(
            FlashcardData(
                word = "こんにちは",
                reading = "konnichiwa",
                meaning = "Xin chào",
                example = "こんにちは、元気ですか？",
                exampleMeaning = "Xin chào, bạn khỏe không?"
            ),
            FlashcardData(
                word = "ありがとう",
                reading = "arigatou",
                meaning = "Cảm ơn",
                example = "ありがとうございます。",
                exampleMeaning = "Cảm ơn rất nhiều."
            ),
            FlashcardData(
                word = "さようなら",
                reading = "sayounara",
                meaning = "Tạm biệt",
                example = "さようなら、また会いましょう。",
                exampleMeaning = "Tạm biệt, hẹn gặp lại."
            )
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (flashcards.isNotEmpty()) {
            FlashcardComponent(
                flashcard = flashcards[currentIndex],
                onNext = { 
                    if (currentIndex < flashcards.size - 1) {
                        currentIndex++
                    }
                },
                onPrevious = {
                    if (currentIndex > 0) {
                        currentIndex--
                    }
                },
                canGoNext = currentIndex < flashcards.size - 1,
                canGoPrevious = currentIndex > 0
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "${currentIndex + 1}/${flashcards.size}",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Text(
                text = "Không có flashcard nào",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
} 