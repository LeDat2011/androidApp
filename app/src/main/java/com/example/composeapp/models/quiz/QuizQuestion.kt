package com.example.composeapp.models.quiz

import com.example.composeapp.models.FlashcardCategory

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: Int,
    val category: FlashcardCategory = FlashcardCategory.MISC
)


