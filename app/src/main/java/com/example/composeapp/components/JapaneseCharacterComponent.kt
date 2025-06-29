package com.example.composeapp.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.composeapp.models.JapaneseCharacter
import com.example.composeapp.models.Example

@Composable
fun JapaneseCharacterCard(
    character: JapaneseCharacter,
    onClick: (JapaneseCharacter) -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation for click
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale_animation"
    )
    
    // Card with character
    Card(
        modifier = modifier
            .size(64.dp)
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                isPressed = true
                onClick(character)
            }
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                kotlinx.coroutines.delay(100)
                isPressed = false
            }
        }
        
        // Content - Japanese character and romanization
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Japanese character
            Text(
                text = character.character,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFFD50000) // Thay đổi từ xanh thành đỏ đậm
            )
            
            // Romanization
            Text(
                text = character.romanization,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun JapaneseCharacterDetailCard(
    character: JapaneseCharacter,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E0) // Màu nền cam nhạt
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ký tự lớn
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE3F2FD)) // Màu nền xanh nhạt
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = character.character,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD50000) // Thay đổi từ xanh thành đỏ đậm
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Romanization
            Text(
                text = character.romanization,
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Kiểm tra xem có phải là chữ kanji không
            val isKanji = character.character.any { it.code >= 0x4E00 && it.code <= 0x9FFF }
            
            if (isKanji) {
                // Hiển thị thông tin kanji
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFECEFF1))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Nghĩa của kanji
                    character.meaning?.let { meaning ->
                        Text(
                            text = "Nghĩa:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4E342E)
                        )
                        Text(
                            text = meaning,
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    // Âm On
                    character.onReading?.let { onReading ->
                        Text(
                            text = "Âm On (音読み):",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4E342E)
                        )
                        Text(
                            text = onReading,
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    // Âm Kun
                    character.kunReading?.let { kunReading ->
                        Text(
                            text = "Âm Kun (訓読み):",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4E342E)
                        )
                        Text(
                            text = kunReading,
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
                
                // Ví dụ sử dụng
                if (character.examples.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ví dụ:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        character.examples.forEach { example ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFECB3)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = example.japanese,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = example.vietnamese,
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Hình ảnh thứ tự nét viết (nếu có)
                if (character.image != null) {
                    Text(
                        text = "Thứ tự nét viết:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100), // Màu cam đậm
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Xác định đường dẫn file hình ảnh
                    val imagePath = character.image.removePrefix("asset:///")
                    
                    // Sử dụng AsyncImage để hiển thị hình ảnh tĩnh
                    AsyncImage(
                        model = "file:///android_asset/$imagePath",
                        contentDescription = "Thứ tự nét viết của ${character.character}",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Chỉ hiển thị nút đóng
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Nút đóng
                OutlinedButton(
                    onClick = onClose,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray
                    ),
                    border = BorderStroke(1.dp, Color.Gray),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("Đóng")
                }
            }
        }
    }
}

@Composable
fun JapaneseAlphabetRow(
    title: String,
    characters: List<JapaneseCharacter>,
    onCharacterClick: (JapaneseCharacter) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Row title
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        // Characters in a row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            characters.forEach { character ->
                JapaneseCharacterCard(
                    character = character,
                    onClick = onCharacterClick
                )
            }
        }
    }
} 