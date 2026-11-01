package com.example.aquasmart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aquasmart.data.model.CropGrowthStage
import com.example.aquasmart.data.model.PaddyVariety
import com.example.aquasmart.ui.AquaSmartViewModel
import com.example.aquasmart.ui.components.SectionHeader
import com.example.aquasmart.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaddyManagementScreen(
    viewModel: AquaSmartViewModel,
    modifier: Modifier = Modifier
) {
    val activeFarm by viewModel.activeFarm.collectAsState()
    val activeCrop by viewModel.activeCropCycle.collectAsState()

    var showNewCropDialog by remember { mutableStateOf(false) }
    var selectedVariety by remember { mutableStateOf(PaddyVariety.ADT_43) }
    var selectedStage by remember { mutableStateOf(CropGrowthStage.VEGETATIVE_TILLERING) }
    var varietyDropdownOpen by remember { mutableStateOf(false) }
    var stageDropdownOpen by remember { mutableStateOf(false) }

    val daysAfterSowing = remember(activeCrop) {
        activeCrop?.let {
            val diffMillis = System.currentTimeMillis() - it.sowingDateMillis
            (diffMillis / (24L * 3600L * 1000L)).toInt().coerceAtLeast(1)
        } ?: 30
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("paddy_management_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("paddy_header_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = LeafGreen.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(LeafGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Paddy Cultivation Lifecycle",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenPrimary
                            )
                        )
                        Text(
                            text = "Tamil Nadu Rice Management Protocol • ${activeFarm?.farmName ?: "Active Farm"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 2. Active Cycle Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_crop_cycle_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = activeCrop?.varietyName ?: "ADT 43 (Kuruvai Special)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Paddy (Oryza sativa) • Active Cycle",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        StatusBadge(text = "$daysAfterSowing Days After Sowing (DAS)", isPositive = true)
                    }

                    // Linear Stage Progress Indicator
                    val currentStageEnum = CropGrowthStage.values().find {
                        it.stageName.equals(activeCrop?.stage, ignoreCase = true)
                    } ?: CropGrowthStage.VEGETATIVE_TILLERING

                    val progressFraction = (currentStageEnum.ordinal + 1) / 6f

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Current Phase: ${currentStageEnum.stageName}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            Text("${(progressFraction * 100).toInt()}% Season Elapsed", style = MaterialTheme.typography.labelSmall)
                        }
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = ForestGreenPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                    // Agronomic Stage Water Directive
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = AquaBlue.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, AquaBlue.copy(alpha = 0.25f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Water, contentDescription = null, tint = AquaBlue, modifier = Modifier.size(18.dp))
                                Text("Water Depth Directive for Current Phase", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = AquaBlue))
                            }
                            Text(
                                text = currentStageEnum.agronomicAdvice,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Water Depth Target: ${currentStageEnum.minWaterCm} - ${currentStageEnum.maxWaterCm} cm standing water.",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, color = ForestGreenPrimary)
                            )
                        }
                    }

                    Button(
                        onClick = { showNewCropDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_register_new_crop"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Register / Switch Crop Cycle")
                    }
                }
            }
        }

        // 3. Stage-by-Stage Phenology Pipeline
        item {
            SectionHeader(
                title = "Rice Phenological Growth Stages",
                subtitle = "TNAU critical water & management guidelines"
            )
        }

        items(CropGrowthStage.values().toList()) { st ->
            val isActiveStage = (st == CropGrowthStage.values().find { it.stageName.equals(activeCrop?.stage, ignoreCase = true) })

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isActiveStage) ForestGreenPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = if (isActiveStage) BorderStroke(1.5.dp, ForestGreenPrimary) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isActiveStage) ForestGreenPrimary else Color.Gray.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${st.ordinal + 1}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isActiveStage) Color.White else Color.Black
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = st.stageName,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            if (st.criticalWaterStage) {
                                StatusBadge(text = "CRITICAL WATER", isPositive = false)
                            }
                        }

                        Text(
                            text = "Timing: ${st.typicalDasStart} - ${st.typicalDasEnd} DAS • Crop Kc: ${st.kc}",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                        )

                        Text(
                            text = st.agronomicAdvice,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    // New Crop Cycle Registration Dialog
    if (showNewCropDialog) {
        AlertDialog(
            onDismissRequest = { showNewCropDialog = false },
            title = { Text("Register Paddy Cultivation Cycle") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select variety and current field stage for ${activeFarm?.farmName ?: "farm"}:", style = MaterialTheme.typography.bodySmall)

                    // Variety selector
                    Box {
                        OutlinedTextField(
                            value = selectedVariety.varietyName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Rice Variety") },
                            trailingIcon = {
                                IconButton(onClick = { varietyDropdownOpen = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = varietyDropdownOpen,
                            onDismissRequest = { varietyDropdownOpen = false }
                        ) {
                            PaddyVariety.values().forEach { v ->
                                DropdownMenuItem(
                                    text = { Text("${v.varietyName} (${v.durationDays}d)") },
                                    onClick = {
                                        selectedVariety = v
                                        varietyDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }

                    // Stage selector
                    Box {
                        OutlinedTextField(
                            value = selectedStage.stageName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Current Growth Phase") },
                            trailingIcon = {
                                IconButton(onClick = { stageDropdownOpen = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = stageDropdownOpen,
                            onDismissRequest = { stageDropdownOpen = false }
                        ) {
                            CropGrowthStage.values().forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(st.stageName) },
                                    onClick = {
                                        selectedStage = st
                                        stageDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val estimatedSowing = System.currentTimeMillis() - (selectedStage.typicalDasStart * 24L * 3600L * 1000L)
                        viewModel.registerOrUpdateCropCycle(
                            variety = selectedVariety,
                            sowingDateMillis = estimatedSowing,
                            stage = selectedStage,
                            targetYield = selectedVariety.yieldPotentialTonsHa
                        )
                        viewModel.calcStage.value = selectedStage
                        viewModel.fertVariety.value = selectedVariety
                        viewModel.recalculateIrrigation()
                        viewModel.recalculateFertilizerPlan()
                        showNewCropDialog = false
                    },
                    modifier = Modifier.testTag("btn_confirm_crop_cycle")
                ) {
                    Text("Save & Activate Cycle")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewCropDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
