package com.example.composeapp.repository

import com.example.composeapp.models.JapaneseCharacter
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class JapaneseAlphabetRepository {
    private val database = FirebaseDatabase.getInstance()
    private val hiraganaRef = database.getReference("app_data/japanese_alphabet/hiragana")
    private val katakanaRef = database.getReference("app_data/japanese_alphabet/katakana")
    private val kanjiRef = database.getReference("app_data/japanese_alphabet/kanji")
    
    private val _hiragana = MutableStateFlow<List<JapaneseCharacter>>(emptyList())
    val hiragana: StateFlow<List<JapaneseCharacter>> = _hiragana
    
    private val _katakana = MutableStateFlow<List<JapaneseCharacter>>(emptyList())
    val katakana: StateFlow<List<JapaneseCharacter>> = _katakana
    
    private val _kanji = MutableStateFlow<List<JapaneseCharacter>>(emptyList())
    val kanji: StateFlow<List<JapaneseCharacter>> = _kanji
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    // Tải dữ liệu Hiragana từ Firebase
    suspend fun loadHiragana() {
        try {
            _isLoading.value = true
            _error.value = null
            
            val snapshot = hiraganaRef.get().await()
            val characters = mutableListOf<JapaneseCharacter>()
            
            for (charSnapshot in snapshot.children) {
                val character = parseJapaneseCharacter(charSnapshot)
                if (character != null) {
                    characters.add(character)
                }
            }
            
            _hiragana.value = characters
        } catch (e: Exception) {
            _error.value = "Không thể tải dữ liệu Hiragana: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
    
    // Tải dữ liệu Katakana từ Firebase
    suspend fun loadKatakana() {
        try {
            _isLoading.value = true
            _error.value = null
            
            val snapshot = katakanaRef.get().await()
            val characters = mutableListOf<JapaneseCharacter>()
            
            for (charSnapshot in snapshot.children) {
                val character = parseJapaneseCharacter(charSnapshot)
                if (character != null) {
                    characters.add(character)
                }
            }
            
            _katakana.value = characters
        } catch (e: Exception) {
            _error.value = "Không thể tải dữ liệu Katakana: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
    
    // Tải dữ liệu Kanji từ Firebase
    suspend fun loadKanji() {
        try {
            _isLoading.value = true
            _error.value = null
            
            val snapshot = kanjiRef.get().await()
            val characters = mutableListOf<JapaneseCharacter>()
            
            for (charSnapshot in snapshot.children) {
                val character = parseJapaneseCharacter(charSnapshot)
                if (character != null) {
                    characters.add(character)
                }
            }
            
            _kanji.value = characters
        } catch (e: Exception) {
            _error.value = "Không thể tải dữ liệu Kanji: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
    
    // Parse JapaneseCharacter từ Firebase snapshot
    private fun parseJapaneseCharacter(snapshot: DataSnapshot): JapaneseCharacter? {
        return try {
            val character = snapshot.child("character").getValue(String::class.java) ?: return null
            val romanization = snapshot.child("romanization").getValue(String::class.java) ?: return null
            val image = snapshot.child("image").getValue(String::class.java)
            val imageUrl = snapshot.child("imageUrl").getValue(String::class.java)
            val audioUrl = snapshot.child("audioUrl").getValue(String::class.java)
            val meaning = snapshot.child("meaning").getValue(String::class.java)
            val onReading = snapshot.child("onReading").getValue(String::class.java)
            val kunReading = snapshot.child("kunReading").getValue(String::class.java)
            
            // Parse examples
            val examples = mutableListOf<com.example.composeapp.models.Example>()
            val examplesNode = snapshot.child("examples")
            if (examplesNode.exists()) {
                for (exampleSnapshot in examplesNode.children) {
                    val japanese = exampleSnapshot.child("japanese").getValue(String::class.java) ?: ""
                    val vietnamese = exampleSnapshot.child("vietnamese").getValue(String::class.java) ?: ""
                    if (japanese.isNotEmpty() && vietnamese.isNotEmpty()) {
                        examples.add(com.example.composeapp.models.Example(japanese, vietnamese))
                    }
                }
            }
            
            JapaneseCharacter(
                character = character,
                romanization = romanization,
                image = image,
                imageUrl = imageUrl,
                audioUrl = audioUrl,
                examples = examples,
                meaning = meaning,
                onReading = onReading,
                kunReading = kunReading
            )
        } catch (e: Exception) {
            null
        }
    }
    
    // Tải tất cả dữ liệu
    suspend fun loadAllData() {
        loadHiragana()
        loadKatakana()
        loadKanji()
    }
    
    fun resetError() {
        _error.value = null
    }
}
