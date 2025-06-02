package com.example.composeapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.screens.FlashcardData
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FlashcardViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val vocabularyRef = database.getReference("app_data/vocabulary")
    
    private val _flashcards = MutableStateFlow<List<FlashcardData>>(emptyList())
    val flashcards: StateFlow<List<FlashcardData>> = _flashcards

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadFlashcards(categoryName: String, level: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val flashcardList = suspendCoroutine<List<FlashcardData>> { continuation ->
                    vocabularyRef
                        .child(categoryName)
                        .child(level)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val flashcards = mutableListOf<FlashcardData>()
                                for (vocabSnapshot in snapshot.children) {
                                    val japanese = vocabSnapshot.child("japanese").getValue(String::class.java) ?: ""
                                    val reading = vocabSnapshot.child("reading").getValue(String::class.java) ?: ""
                                    val vietnamese = vocabSnapshot.child("vietnamese").getValue(String::class.java) ?: ""
                                    val example = vocabSnapshot.child("example").getValue(String::class.java) ?: ""

                                    // Tách ví dụ thành tiếng Nhật và tiếng Việt
                                    val parts = example.split(" - ")
                                    val japaneseExample = parts.getOrNull(0) ?: ""
                                    val vietnameseExample = parts.getOrNull(1) ?: ""

                                    flashcards.add(
                                        FlashcardData(
                                            word = japanese,
                                            reading = reading,
                                            meaning = vietnamese,
                                            example = japaneseExample,
                                            exampleMeaning = vietnameseExample
                                        )
                                    )
                                }
                                continuation.resume(flashcards)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                continuation.resumeWithException(error.toException())
                            }
                        })
                }

                _flashcards.value = flashcardList
            } catch (e: Exception) {
                _error.value = "Không thể tải dữ liệu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 