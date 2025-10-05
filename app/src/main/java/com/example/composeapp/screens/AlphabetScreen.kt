package com.example.composeapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeapp.components.JapaneseCharacterCard
import com.example.composeapp.components.JapaneseCharacterDetailCard
import com.example.composeapp.models.AlphabetType
import com.example.composeapp.models.JapaneseAlphabet
import com.example.composeapp.models.JapaneseCharacter
import com.example.composeapp.viewmodels.JapaneseAlphabetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphabetScreen(
    alphabetType: AlphabetType,
    onNavigateBack: () -> Unit,
    viewModel: JapaneseAlphabetViewModel = viewModel()
) {
    // Thiết lập loại bảng chữ cái hiện tại
    LaunchedEffect(key1 = alphabetType) {
        viewModel.switchAlphabetType(alphabetType)
    }
    
    // Theo dõi loại bảng chữ cái hiện tại từ ViewModel
    val currentAlphabetType by viewModel.currentAlphabetType
    
    // State cho ký tự được chọn
    val selectedCharacter by viewModel.selectedCharacter
    
    // Lấy danh sách ký tự theo hàng dựa trên loại bảng chữ cái hiện tại
    val characters by viewModel.characters.collectAsState()
    val characterRows by remember(currentAlphabetType, characters) {
        mutableStateOf(
            when (currentAlphabetType) {
                AlphabetType.HIRAGANA -> JapaneseAlphabet.getRows(AlphabetType.HIRAGANA)
                AlphabetType.KATAKANA -> JapaneseAlphabet.getRows(AlphabetType.KATAKANA)
                AlphabetType.KANJI -> viewModel.getCharacterRows()
            }
        )
    }
    
    // State cho thanh tìm kiếm
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    val searchResults = remember(searchQuery, currentAlphabetType) {
        if (searchQuery.isBlank()) emptyList()
        else viewModel.searchCharacters(searchQuery)
    }
    
    Scaffold(
        containerColor = Color(0xFFFFF8E1), // Màu nền vàng nhạt như trong hình mẫu
        topBar = {
            TopAppBar(
                title = { Text(viewModel.getCurrentAlphabetName()) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại màn hình chọn bảng chữ cái"
                        )
                    }
                },
                actions = {
                    // Nút tìm kiếm
                    IconButton(onClick = { isSearching = !isSearching }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Tìm kiếm"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFF8E1) // Màu nền vàng nhạt cho thanh tiêu đề
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Thanh tìm kiếm
            AnimatedVisibility(visible = isSearching) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    placeholder = { Text("Tìm kiếm theo phiên âm...") },
                    singleLine = true,
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Xóa"
                                )
                            }
                        }
                    }
                )
            }
            
            // Hiển thị kết quả tìm kiếm nếu đang tìm kiếm
            if (isSearching && searchQuery.isNotEmpty()) {
                if (searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Không tìm thấy kết quả")
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(searchResults) { character ->
                            JapaneseCharacterCard(
                                character = character,
                                onClick = { viewModel.selectCharacter(it) }
                            )
                        }
                    }
                }
            } else {
                // Hiển thị bảng chữ cái theo hàng
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    characterRows.forEach { (rowTitle, characters) ->
                        // Tiêu đề hàng
                        Text(
                            text = rowTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                        
                        // Grid hiển thị các ký tự
                        if (characters.isNotEmpty()) {
                            // Tính toán chiều cao dựa trên số lượng ký tự và số cột (5)
                            val rows = (characters.size / 5) + if (characters.size % 5 > 0) 1 else 0
                            val gridHeight = (rows * 80).dp // 64dp cho card + 16dp cho padding
                        
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(5),
                                contentPadding = PaddingValues(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                    .height(gridHeight.coerceAtMost(300.dp)) // Giới hạn chiều cao tối đa
                        ) {
                            items(characters) { character ->
                                JapaneseCharacterCard(
                                    character = character,
                                        onClick = { viewModel.selectCharacter(it) },
                                        modifier = Modifier.padding(4.dp)
                                )
                                }
                            }
                        }
                    }
                    
                    // Thêm khoảng trống ở cuối
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
    
    // Hiển thị chi tiết ký tự khi được chọn
    selectedCharacter?.let { character ->
        Dialog(
            onDismissRequest = { viewModel.closeCharacterDetail() }
        ) {
            JapaneseCharacterDetailCard(
                character = character,
                onClose = { viewModel.closeCharacterDetail() }
            )
        }
    }
} 