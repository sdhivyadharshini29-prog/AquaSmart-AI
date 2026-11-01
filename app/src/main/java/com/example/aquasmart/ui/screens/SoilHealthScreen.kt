package com.example.aquasmart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aquasmart.data.model.SoilRating
import com.example.aquasmart.data.model.SoilType
import com.example.aquasmart.ui.AquaSmartViewModel
import com.example.aquasmart.ui.components.SectionHeader
import com.example.aquasmart.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoilHealthScreen(
    viewModel: AquaSmartViewModel,
    modifier: Modifier = Modifier
) {
    val activeFarm by viewModel.activeFarm.collectAsState()
    val phInput by viewModel.soilPhInput.collectAsState()
    val nInput by viewModel.soilNInput.collectAsState()
    val pInput by viewModel.soilPInput.collectAsState()
    val kInput by viewModel.soilKInput.collectAsState()
    val ocInput by viewModel.soilOcInput.collectAsState()
    val soilTypeInput by viewModel.soilTypeInput.collectAsState()
    val analysis by viewModel.soilAnalysisResult.collectAsState()
    val allSoilRecords by viewModel.allSoilRecords.collectAsState()

    var showSoilTypeDropdown by remember { mutableStateOf(false) }
    var saveSuccessMessage by remember { mutableStateOf<String?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("soil_health_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("soil_header_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ForestGreenPrimary.copy(alpha = 0.1f)
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
                            .background(ForestGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Soil Health & Nutrient Analysis",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenPrimary
                            )
                        )
                        Text(
                            text = "TNAU Soil Fertility Classification for ${activeFarm?.farmName ?: "Active Farm"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 2. Input Form Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("soil_input_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Enter Soil Test Card Values",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Soil pH
                    OutlinedTextField(
                        value = phInput,
                        onValueChange = {
                            viewModel.soilPhInput.value = it
                            validationError = null
                            viewModel.recalculateSoilAnalysis()
                        },
                        label = { Text("Soil pH (Acidity / Alkalinity)") },
                        supportingText = { Text("Normal: 6.5 - 7.8 | Lime needed if <6.5 | Gypsum if >8.5") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        leadingIcon = { Icon(Icons.Default.WaterDrop, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_soil_ph")
                    )

                    // NPK Row 1: Nitrogen and Phosphorus
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = nInput,
                            onValueChange = {
                                viewModel.soilNInput.value = it
                                viewModel.recalculateSoilAnalysis()
                            },
                            label = { Text("Available N (kg/ha)") },
                            supportingText = { Text("<280 Low | >450 High") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_soil_n")
                        )
                        OutlinedTextField(
                            value = pInput,
                            onValueChange = {
                                viewModel.soilPInput.value = it
                                viewModel.recalculateSoilAnalysis()
                            },
                            label = { Text("Available P₂O₅ (kg/ha)") },
                            supportingText = { Text("<11 Low | >22 High") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_soil_p")
                        )
                    }

                    // NPK Row 2: Potassium and Organic Carbon
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = kInput,
                            onValueChange = {
                                viewModel.soilKInput.value = it
                                viewModel.recalculateSoilAnalysis()
                            },
                            label = { Text("Available K₂O (kg/ha)") },
                            supportingText = { Text("<118 Low | >280 High") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_soil_k")
                        )
                        OutlinedTextField(
                            value = ocInput,
                            onValueChange = {
                                viewModel.soilOcInput.value = it
                                viewModel.recalculateSoilAnalysis()
                            },
                            label = { Text("Organic Carbon (%)") },
                            supportingText = { Text("<0.5% Low | >0.75% High") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_soil_oc")
                        )
                    }

                    // Soil Type Dropdown
                    Box {
                        OutlinedTextField(
                            value = soilTypeInput.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Soil Texture & Type") },
                            trailingIcon = {
                                IconButton(onClick = { showSoilTypeDropdown = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dropdown_soil_health_type")
                        )
                        DropdownMenu(
                            expanded = showSoilTypeDropdown,
                            onDismissRequest = { showSoilTypeDropdown = false }
                        ) {
                            SoilType.values().forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(st.displayName) },
                                    onClick = {
                                        viewModel.soilTypeInput.value = st
                                        viewModel.recalculateSoilAnalysis()
                                        showSoilTypeDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    validationError?.let { err ->
                        Text(err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    // Save Button
                    Button(
                        onClick = {
                            val ph = phInput.toDoubleOrNull()
                            if (ph == null || ph !in 3.0..11.0) {
                                validationError = "Please enter a valid soil pH between 3.0 and 11.0."
                                return@Button
                            }
                            viewModel.saveCurrentSoilAnalysis()
                            saveSuccessMessage = "Soil test report saved successfully to farm records!"
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_save_soil_report"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Soil Test Record")
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

        // 3. Soil Analysis Results Card
        item {
            analysis?.let { res ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("soil_results_card"),
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
                            Text(
                                text = "Soil Fertility Condition",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            StatusBadge(
                                text = "Score ${res.overallHealthScore}/100",
                                isPositive = res.overallHealthScore >= 70,
                                isWarning = res.overallHealthScore in 50..69
                            )
                        }

                        // Rating Badges Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NutrientRatingChip(name = "Nitrogen", rating = res.nitrogenRating, modifier = Modifier.weight(1f))
                            NutrientRatingChip(name = "Phosphorus", rating = res.phosphorusRating, modifier = Modifier.weight(1f))
                            NutrientRatingChip(name = "Potassium", rating = res.potassiumRating, modifier = Modifier.weight(1f))
                        }

                        // pH Reaction & Condition Summary
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = res.phCategory,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(res.phColorHex)
                                    )
                                )
                                Text(
                                    text = res.conditionSummary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Soil Amendment & Biological Advice
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFF92400E), modifier = Modifier.size(20.dp))
                                Column {
                                    Text("Soil Reclamation & Amendment Advisory", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF92400E)))
                                    Text(res.soilAmendmentAdvice, style = MaterialTheme.typography.bodySmall, color = Color(0xFF78350F))
                                }
                            }
                        }

                        // Organic Carbon Advice
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(Icons.Default.Yard, contentDescription = null, tint = Color(0xFF166534), modifier = Modifier.size(20.dp))
                                Column {
                                    Text("Organic Carbon & Biofertilizer Guidance", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF166534)))
                                    Text(res.organicPractices, style = MaterialTheme.typography.bodySmall, color = Color(0xFF14532D))
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Historical Soil Tests
        item {
            SectionHeader(
                title = "Soil Test Records History",
                subtitle = "Logged laboratory & card tests"
            )
        }

        if (allSoilRecords.isEmpty()) {
            item {
                Text(
                    text = "No prior soil tests logged yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(allSoilRecords) { record ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "pH ${record.ph} • ${record.soilType}",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = record.phCategory,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "N: ${record.nitrogenKgHa.toInt()} kg/ha (${record.nitrogenRating}) | P: ${record.phosphorusKgHa.toInt()} kg/ha | K: ${record.potassiumKgHa.toInt()} kg/ha | OC: ${record.organicCarbonPct}%",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = record.summaryRecommendation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NutrientRatingChip(
    name: String,
    rating: SoilRating,
    modifier: Modifier = Modifier
) {
    val (bg, text) = when (rating) {
        SoilRating.LOW -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
        SoilRating.MEDIUM -> Color(0xFFDCFCE7) to Color(0xFF166534)
        SoilRating.HIGH -> Color(0xFFFEF3C7) to Color(0xFF92400E)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bg
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(name, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
            Text(rating.name, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = text))
        }
    }
}
