package com.example.aquasmart.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aquasmart.data.model.CropGrowthStage
import com.example.aquasmart.data.model.IrrigationMethod
import com.example.aquasmart.data.model.SoilType
import com.example.aquasmart.ui.AquaSmartViewModel
import com.example.aquasmart.ui.components.SectionHeader
import com.example.aquasmart.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IrrigationCalculatorScreen(
    viewModel: AquaSmartViewModel,
    modifier: Modifier = Modifier
) {
    val activeFarm by viewModel.activeFarm.collectAsState()
    val area by viewModel.calcAreaAcres.collectAsState()
    val stage by viewModel.calcStage.collectAsState()
    val soilType by viewModel.calcSoilType.collectAsState()
    val moisture by viewModel.calcMoisturePct.collectAsState()
    val rainfall by viewModel.calcRecentRainfallMm.collectAsState()
    val temperature by viewModel.calcTemperatureC.collectAsState()
    val method by viewModel.calcIrrigationMethod.collectAsState()
    val isSensorMode by viewModel.calcIsSensorMeasured.collectAsState()
    val result by viewModel.irrigationResult.collectAsState()

    var showStageDropdown by remember { mutableStateOf(false) }
    var showSoilDropdown by remember { mutableStateOf(false) }
    var showMethodDropdown by remember { mutableStateOf(false) }
    var showExplanationDialog by remember { mutableStateOf(false) }
    var saveSuccessMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("irrigation_calculator_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Screen Title & Farm Indicator
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("calculator_header_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AquaBlue.copy(alpha = 0.1f)
                )
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
                            .background(AquaBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Smart Irrigation Calculator",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AquaBlue
                            )
                        )
                        Text(
                            text = "Target: ${activeFarm?.farmName ?: "Default Farm"} (${activeFarm?.areaAcres ?: 3.5} Acres in ${activeFarm?.district ?: "Thanjavur"})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 2. Data Mode Switcher (Sensor vs Farmer Estimation)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("data_mode_switch_card"),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isSensorMode) "IoT Telemetry Sensor Mode" else "Farmer-Entered Estimate Mode",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (isSensorMode) "Live soil moisture & weather probe feeds" else "User entered field observation values",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isSensorMode,
                        onCheckedChange = {
                            viewModel.calcIsSensorMeasured.value = it
                            viewModel.recalculateIrrigation()
                        },
                        modifier = Modifier.testTag("sensor_mode_toggle")
                    )
                }
            }
        }

        // 3. Calculator Input Parameters Form
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("irrigation_inputs_form_card"),
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
                    Text(
                        text = "Cultivation & Environmental Inputs",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Crop Stage Selector
                    Box {
                        OutlinedTextField(
                            value = "${stage.stageName} (Kc: ${stage.kc})",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Paddy Growth Stage") },
                            trailingIcon = {
                                IconButton(onClick = { showStageDropdown = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Stage")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dropdown_crop_stage")
                        )
                        DropdownMenu(
                            expanded = showStageDropdown,
                            onDismissRequest = { showStageDropdown = false }
                        ) {
                            CropGrowthStage.values().forEach { st ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(st.stageName, fontWeight = FontWeight.Bold)
                                            Text("Kc: ${st.kc} • ${st.typicalDasStart}-${st.typicalDasEnd} DAS", style = MaterialTheme.typography.bodySmall)
                                        }
                                    },
                                    onClick = {
                                        viewModel.calcStage.value = st
                                        viewModel.recalculateIrrigation()
                                        showStageDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Soil Type Selector
                    Box {
                        OutlinedTextField(
                            value = "${soilType.displayName} (Percolation: ${soilType.percolationMmDay} mm/d)",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Soil Type & Infiltration Rate") },
                            trailingIcon = {
                                IconButton(onClick = { showSoilDropdown = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Soil")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dropdown_soil_type")
                        )
                        DropdownMenu(
                            expanded = showSoilDropdown,
                            onDismissRequest = { showSoilDropdown = false }
                        ) {
                            SoilType.values().forEach { st ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(st.displayName, fontWeight = FontWeight.Bold)
                                            Text(st.description, style = MaterialTheme.typography.bodySmall)
                                        }
                                    },
                                    onClick = {
                                        viewModel.calcSoilType.value = st
                                        viewModel.recalculateIrrigation()
                                        showSoilDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Irrigation Method Selector
                    Box {
                        OutlinedTextField(
                            value = "${method.methodName} (-${method.waterSavingPct}% water)",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Irrigation Application Method") },
                            trailingIcon = {
                                IconButton(onClick = { showMethodDropdown = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Method")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dropdown_irrigation_method")
                        )
                        DropdownMenu(
                            expanded = showMethodDropdown,
                            onDismissRequest = { showMethodDropdown = false }
                        ) {
                            IrrigationMethod.values().forEach { m ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(m.methodName, fontWeight = FontWeight.Bold)
                                            Text(m.summary, style = MaterialTheme.typography.bodySmall)
                                        }
                                    },
                                    onClick = {
                                        viewModel.calcIrrigationMethod.value = m
                                        viewModel.recalculateIrrigation()
                                        showMethodDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Farm Area Input
                    OutlinedTextField(
                        value = area.toString(),
                        onValueChange = {
                            val v = it.toDoubleOrNull() ?: area
                            viewModel.calcAreaAcres.value = v
                            viewModel.recalculateIrrigation()
                        },
                        label = { Text("Cultivated Area (Acres)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        leadingIcon = { Icon(Icons.Default.SquareFoot, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_calc_area")
                    )

                    // Soil Moisture Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Current Soil Moisture",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = "${moisture.toInt()}%",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (moisture < 40.0) StatusWarning else AquaBlue
                                )
                            )
                        }
                        Slider(
                            value = moisture.toFloat(),
                            onValueChange = {
                                viewModel.calcMoisturePct.value = it.toDouble()
                                viewModel.recalculateIrrigation()
                            },
                            valueRange = 10f..100f,
                            modifier = Modifier.testTag("slider_soil_moisture")
                        )
                    }

                    // Recent Rainfall & Temperature
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = rainfall.toString(),
                            onValueChange = {
                                val v = it.toDoubleOrNull() ?: 0.0
                                viewModel.calcRecentRainfallMm.value = v
                                viewModel.recalculateIrrigation()
                            },
                            label = { Text("Rainfall (mm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_calc_rainfall")
                        )
                        OutlinedTextField(
                            value = temperature.toString(),
                            onValueChange = {
                                val v = it.toDoubleOrNull() ?: 32.0
                                viewModel.calcTemperatureC.value = v
                                viewModel.recalculateIrrigation()
                            },
                            label = { Text("Temp (°C)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_calc_temp")
                        )
                    }
                }
            }
        }

        // 4. Results & Recommendation Card
        item {
            result?.let { res ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("irrigation_result_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(ForestGreenPrimary.copy(alpha = 0.5f), AquaBlue.copy(alpha = 0.5f))
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Irrigation Decision",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            StatusBadge(
                                text = res.recommendationAction,
                                isPositive = !res.isIrrigationNeededToday,
                                isWarning = res.isIrrigationNeededToday
                            )
                        }

                        Text(
                            text = res.recommendationSummary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // AWD Field Water Tube (Pani Pipe) visual advisory
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AquaBlue.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, AquaBlue.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.VerticalAlignBottom, contentDescription = null, tint = AquaBlue)
                                Column {
                                    Text("AWD Pani Pipe (Field Water Tube) Status", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                    Text(res.awdPaniPipeStatus, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }

                        // Quantitative Outputs Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Net Depth", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        "${res.netIrrigationDepthMm.format(1)} mm",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = AquaBlue
                                        )
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Volume (m³)", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        "${res.waterVolumeCubicMeters.toInt()} m³",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = ForestGreenPrimary
                                        )
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Volume (Litres)", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        "${(res.waterVolumeLitres / 1000).toInt()}k L",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        if (res.waterSavedLitres > 0) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = StatusOptimal.copy(alpha = 0.12f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Savings, contentDescription = null, tint = StatusOptimal)
                                    Text(
                                        text = "Water Conservation: ${(res.waterSavedLitres / 1000).toInt()} m³ (${(res.waterSavedLitres).toInt()} Litres) saved compared to traditional continuous flooding!",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = StatusOptimal
                                        )
                                    )
                                }
                            }
                        }

                        // Explanation & Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showExplanationDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_show_math_explanation")
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Calculation Math")
                            }

                            Button(
                                onClick = {
                                    viewModel.saveCurrentIrrigationCalculation()
                                    saveSuccessMessage = "Calculation saved to farm audit history."
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_save_irrigation_log")
                            ) {
                                Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Record")
                            }
                        }

                        saveSuccessMessage?.let { msg ->
                            Text(
                                text = "✓ $msg",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = StatusOptimal,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // Step-by-step Agronomic Math Explanation Dialog
    if (showExplanationDialog && result != null) {
        AlertDialog(
            onDismissRequest = { showExplanationDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Calculate, contentDescription = null, tint = AquaBlue)
                    Text("Agronomic Calculation Details", style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = result!!.stepByStepExplanation,
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
                TextButton(
                    onClick = { showExplanationDialog = false },
                    modifier = Modifier.testTag("btn_close_explanation")
                ) {
                    Text("Close")
                }
            }
        )
    }
}

private fun Double.format(digits: Int): String = "%.${digits}f".format(this)
