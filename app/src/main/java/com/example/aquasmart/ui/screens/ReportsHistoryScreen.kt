package com.example.aquasmart.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aquasmart.data.model.IrrigationRecordEntity
import com.example.aquasmart.ui.AquaSmartViewModel
import com.example.aquasmart.ui.components.StatusBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportsHistoryScreen(
    viewModel: AquaSmartViewModel,
    modifier: Modifier = Modifier
) {
    val irrigationLogs by viewModel.allIrrigationRecords.collectAsState()
    val soilLogs by viewModel.allSoilRecords.collectAsState()
    val observationLogs by viewModel.allObservations.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var selectedMathDetail by remember { mutableStateOf<String?>(null) }

    val totalWaterSavedLitres = remember(irrigationLogs) {
        irrigationLogs.sumOf { it.waterSavedLitres }
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("reports_history_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Water Conservation Metric Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("water_saved_hero_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cumulative Water Conservation",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            tint = ForestGreenPrimary
                        )
                    }

                    Text(
                        text = "${(totalWaterSavedLitres / 1000).toInt()} m³ (${(totalWaterSavedLitres).toInt()} Litres)",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenPrimary
                        )
                    )

                    Text(
                        text = "Water conserved through Alternate Wetting & Drying (AWD) and precision budgeting vs continuous flooding across all recorded cycles.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // 2. Tab Selector
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = (selectedTab == 0),
                    onClick = { selectedTab = 0 },
                    text = { Text("Irrigation Logs (${irrigationLogs.size})") }
                )
                Tab(
                    selected = (selectedTab == 1),
                    onClick = { selectedTab = 1 },
                    text = { Text("Soil Tests (${soilLogs.size})") }
                )
                Tab(
                    selected = (selectedTab == 2),
                    onClick = { selectedTab = 2 },
                    text = { Text("Observations (${observationLogs.size})") }
                )
            }
        }

        // 3. Tab Contents
        when (selectedTab) {
            0 -> {
                if (irrigationLogs.isEmpty()) {
                    item {
                        Text("No irrigation calculations logged yet.", style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    items(irrigationLogs) { log ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${log.areaAcres} Acres • ${log.stage}",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    StatusBadge(
                                        text = "${log.netIrrigationMm.toInt()} mm (${log.waterVolumeM3.toInt()} m³)",
                                        isPositive = log.waterSavedLitres > 0
                                    )
                                }

                                Text(
                                    text = "Date: ${dateFormatter.format(Date(log.timestampMillis))} • Method: ${log.irrigationMethod}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = log.recommendation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (log.waterSavedLitres > 0) {
                                        Text(
                                            text = "Saved ${(log.waterSavedLitres / 1000).toInt()} m³ water",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = StatusOptimal)
                                        )
                                    } else {
                                        Text("Baseline continuous application", style = MaterialTheme.typography.labelSmall)
                                    }

                                    TextButton(onClick = { selectedMathDetail = log.calculationDetails }) {
                                        Text("View Agronomic Math", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                if (soilLogs.isEmpty()) {
                    item {
                        Text("No soil tests logged yet.", style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    items(soilLogs) { soil ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "pH ${soil.ph} • ${soil.soilType}",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(soil.phCategory, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                }
                                Text("Date: ${dateFormatter.format(Date(soil.recordedDateMillis))}", style = MaterialTheme.typography.labelSmall)
                                Text("N: ${soil.nitrogenKgHa.toInt()} kg/ha | P: ${soil.phosphorusKgHa.toInt()} kg/ha | K: ${soil.potassiumKgHa.toInt()} kg/ha | OC: ${soil.organicCarbonPct}%", style = MaterialTheme.typography.bodySmall)
                                Text("Amendment: ${soil.amendmentRequired}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
            2 -> {
                if (observationLogs.isEmpty()) {
                    item {
                        Text("No crop observations logged yet.", style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    items(observationLogs) { obs ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(obs.suspectedCondition, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                    StatusBadge(text = obs.severityLevel)
                                }
                                Text("Date: ${dateFormatter.format(Date(obs.observationDateMillis))}", style = MaterialTheme.typography.labelSmall)
                                Text("Symptoms: ${obs.observedSymptoms}", style = MaterialTheme.typography.bodySmall)
                                Text("Control: ${obs.organicControl}", style = MaterialTheme.typography.bodySmall, color = ForestGreenPrimary)
                            }
                        }
                    }
                }
            }
        }
    }

    // Mathematical Calculation Breakdown Modal
    selectedMathDetail?.let { detail ->
        AlertDialog(
            onDismissRequest = { selectedMathDetail = null },
            title = { Text("Archived Agronomic Calculation") },
            text = {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = detail,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        ),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedMathDetail = null }) {
                    Text("Close")
                }
            }
        )
    }
}
