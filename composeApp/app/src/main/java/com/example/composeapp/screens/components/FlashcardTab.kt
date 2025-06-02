package com.example.composeapp.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.composeapp.components.FlashcardItem
import com.example.composeapp.models.SampleData

@Composable
fun FlashcardTab() {
    val flashcards = SampleData.flashcards
    var currentIndex by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (flashcards.isNotEmpty()) {
            FlashcardItem(
                flashcard = flashcards[currentIndex],
                isFlipped = false,
                onFlip = { /* Handle flip */ }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { 
                        if (currentIndex > 0) currentIndex-- 
                    },
                    enabled = currentIndex > 0
                ) {
                    Text("Previous")
                }
                
                Text("${currentIndex + 1}/${flashcards.size}")
                
                Button(
                    onClick = { 
                        if (currentIndex < flashcards.size - 1) currentIndex++ 
                    },
                    enabled = currentIndex < flashcards.size - 1
                ) {
                    Text("Next")
                }
            }
        } else {
            Text("No flashcards available")
        }
    }
} 