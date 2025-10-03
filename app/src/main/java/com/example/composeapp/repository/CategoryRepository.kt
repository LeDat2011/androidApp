package com.example.composeapp.repository

import com.example.composeapp.models.FlashcardCategory
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class CategoryRepository {
    private val database = FirebaseDatabase.getInstance()
    private val categoriesRef = database.getReference("app_data/categories")
    
    private val _categories = MutableStateFlow<List<CategoryData>>(emptyList())
    val categories: StateFlow<List<CategoryData>> = _categories
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    // Tải danh sách categories từ Firebase
    suspend fun loadCategories() {
        try {
            _isLoading.value = true
            _error.value = null
            
            val snapshot = categoriesRef.get().await()
            val categoriesList = mutableListOf<CategoryData>()
            
            for (categorySnapshot in snapshot.children) {
                val category = parseCategory(categorySnapshot)
                if (category != null) {
                    categoriesList.add(category)
                }
            }
            
            // Sắp xếp categories theo order
            categoriesList.sortBy { it.id }
            _categories.value = categoriesList
        } catch (e: Exception) {
            _error.value = "Không thể tải danh sách categories: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
    
    // Parse CategoryData từ Firebase snapshot
    private fun parseCategory(snapshot: DataSnapshot): CategoryData? {
        return try {
            val id = snapshot.key ?: return null
            val name = snapshot.child("name").getValue(String::class.java) ?: return null
            val description = snapshot.child("description").getValue(String::class.java) ?: ""
            val iconUrl = snapshot.child("iconUrl").getValue(String::class.java) ?: "📚"
            val color = snapshot.child("color").getValue(String::class.java) ?: "#9C27B0"
            val order = snapshot.child("order").getValue(Int::class.java) ?: 0
            val vocabularyCount = snapshot.child("vocabularyCount").getValue(Int::class.java) ?: 0
            val lessonCount = snapshot.child("lessonCount").getValue(Int::class.java) ?: 0
            
            // Tạo levels mặc định cho N5, N4, N3, N2, N1
            val levels = listOf(
                LevelData(id = "N5", name = "N5", description = "Cơ bản", color = "#4CAF50"),
                LevelData(id = "N4", name = "N4", description = "Sơ cấp", color = "#2196F3"),
                LevelData(id = "N3", name = "N3", description = "Trung cấp", color = "#FF9800"),
                LevelData(id = "N2", name = "N2", description = "Trung cao", color = "#F44336"),
                LevelData(id = "N1", name = "N1", description = "Cao cấp", color = "#9C27B0")
            )
            
            CategoryData(
                id = id,
                name = name,
                displayName = name,
                description = description,
                iconUrl = iconUrl,
                color = color,
                levels = levels
            )
        } catch (e: Exception) {
            null
        }
    }
    
    fun resetError() {
        _error.value = null
    }
}

data class CategoryData(
    val id: String,
    val name: String,
    val displayName: String,
    val description: String,
    val iconUrl: String? = null,
    val color: String,
    val levels: List<LevelData> = emptyList()
)

data class LevelData(
    val id: String,
    val name: String,
    val description: String,
    val color: String
)






