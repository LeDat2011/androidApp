package com.example.composeapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeapp.models.*

@Composable
fun WritingSettingsComponent(
    settings: WritingSettings,
    onSettingsChanged: (WritingSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Cài đặt hiển thị",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Chế độ hiển thị nét mẫu
            Text(
                text = "Chế độ hiển thị nét mẫu:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TemplateModeSelector(
                selectedMode = settings.templateDisplayMode,
                onModeSelected = { mode ->
                    onSettingsChanged(settings.copy(templateDisplayMode = mode))
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Cài đặt opacity
            Text(
                text = "Độ mờ nét mẫu: ${(settings.templateOpacity * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Slider(
                value = settings.templateOpacity,
                onValueChange = { opacity ->
                    onSettingsChanged(settings.copy(templateOpacity = opacity))
                },
                valueRange = 0.1f..0.8f,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Các tùy chọn khác
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hiển thị số thứ tự",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(
                        checked = settings.showStrokeNumbers,
                        onCheckedChange = { show ->
                            onSettingsChanged(settings.copy(showStrokeNumbers = show))
                        }
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Animation nét mẫu",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(
                        checked = settings.animateTemplate,
                        onCheckedChange = { animate ->
                            onSettingsChanged(settings.copy(animateTemplate = animate))
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hiển thị mũi tên",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(
                        checked = settings.showDirectionArrows,
                        onCheckedChange = { show ->
                            onSettingsChanged(settings.copy(showDirectionArrows = show))
                        }
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Đường kẻ hướng dẫn",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(
                        checked = settings.showGuideLines,
                        onCheckedChange = { show ->
                            onSettingsChanged(settings.copy(showGuideLines = show))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TemplateModeSelector(
    selectedMode: TemplateDisplayMode,
    onModeSelected: (TemplateDisplayMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        TemplateDisplayMode.values().forEach { mode ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedMode == mode,
                        onClick = { onModeSelected(mode) }
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedMode == mode,
                    onClick = { onModeSelected(mode) }
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column {
                    Text(
                        text = getModeDisplayName(mode),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = getModeDescription(mode),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

private fun getModeDisplayName(mode: TemplateDisplayMode): String {
    return when (mode) {
        TemplateDisplayMode.FADING -> "Nét mờ dần"
        TemplateDisplayMode.OUTLINE -> "Chỉ viền nét"
        TemplateDisplayMode.FILLED -> "Nét đầy"
        TemplateDisplayMode.ANIMATED -> "Nét có animation"
        TemplateDisplayMode.TRACING -> "Nét theo dõi"
        TemplateDisplayMode.BOLD_BLACK -> "Nét đen đậm rõ ràng"
        TemplateDisplayMode.THICK_GUIDE -> "Nét dày hướng dẫn"
    }
}

private fun getModeDescription(mode: TemplateDisplayMode): String {
    return when (mode) {
        TemplateDisplayMode.FADING -> "Nét mẫu hiển thị mờ với hiệu ứng chấm gạch"
        TemplateDisplayMode.OUTLINE -> "Chỉ hiển thị viền của nét mẫu"
        TemplateDisplayMode.FILLED -> "Nét mẫu được tô đầy với màu nhạt"
        TemplateDisplayMode.ANIMATED -> "Nét mẫu có hiệu ứng chạy từ đầu đến cuối"
        TemplateDisplayMode.TRACING -> "Nét mẫu có hiệu ứng theo dõi liên tục"
        TemplateDisplayMode.BOLD_BLACK -> "Nét mẫu đen đậm rõ ràng, dễ nhìn và theo dõi"
        TemplateDisplayMode.THICK_GUIDE -> "Nét mẫu dày với chấm gạch, dễ vẽ theo"
    }
}
