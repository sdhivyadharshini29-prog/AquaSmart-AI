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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aquasmart.data.model.PaddyVariety
import com.example.aquasmart.data.model.SoilRating
import com.example.aquasmart.ui.AquaSmartViewModel
import com.example.aquasmart.ui.components.SectionHeader
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FertilizerAdvisoryScreen(
    viewModel: AquaSmartViewModel,
    modifier: Modifier = Modifier
) {
    val activeFarm by viewModel.activeFarm.collectAsState()
    val variety by viewModel.fertVariety.collectAsState()
    val nRating by viewModel.fertNRating.collectAsState()
    val pRating by viewModel.fertPRating.collectAsState()
    val kRating by viewModel.fertKRating.collectAsState()
    val plan by viewModel.fertilizerPlan.collectAsState()

    var showVarietyDropdown by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("fertilizer_advisory_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fertilizer_header_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GoldenPaddy.copy(alpha = 0.12f)
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
                            .background(GoldenPaddy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TNAU Fertilizer Advisory",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldenPaddy
                            )
                        )
                        Text(
                            text = "Precision NPK Scheduling & Split Dosing for ${activeFarm?.farmName ?: "Farm"} (${activeFarm?.areaAcres ?: 3.5} Acres)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 2. Variety & Soil Rating Selector Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fertilizer_parameters_card"),
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
                        text = "Cultivar & Soil Baseline",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Variety Dropdown
                    Box {
                        OutlinedTextField(
                            value = "${variety.varietyName} (${variety.durationDays} days)",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Paddy Variety & Duration") },
                            trailingIcon = {
                                IconButton(onClick = { showVarietyDropdown = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dropdown_fert_variety")
                        )
                        DropdownMenu(
                            expanded = showVarietyDropdown,
                            onDismissRequest = { showVarietyDropdown = false }
                        ) {
                            PaddyVariety.values().forEach { v ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(v.varietyName, fontWeight = FontWeight.Bold)
                                            Text("${v.durationDays} days • ${v.season}", style = MaterialTheme.typography.bodySmall)
                                        }
                                    },
                                    onClick = {
                                        viewModel.fertVariety.value = v
                                        viewModel.recalculateFertilizerPlan()
                                        showVarietyDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Soil Test Rating Selectors
                    Text("Soil Nutrient Availability Adjustment", style = MaterialTheme.typography.labelMedium)

                    // N Selector
                    RatingSelectorRow(
                        nutrientName = "Nitrogen (N)",
                        selected = nRating,
                        onSelect = {
                            viewModel.fertNRating.value = it
                            viewModel.recalculateFertilizerPlan()
                        }
                    )

                    // P Selector
                    RatingSelectorRow(
                        nutrientName = "Phosphorus (P)",
                        selected = pRating,
                        onSelect = {
                            viewModel.fertPRating.value = it
                            viewModel.recalculateFertilizerPlan()
                        }
                    )

                    // K Selector
                    RatingSelectorRow(
                        nutrientName = "Potassium (K)",
                        selected = kRating,
                        onSelect = {
                            viewModel.fertKRating.value = it
                            viewModel.recalculateFertilizerPlan()
                        }
                    )
                }
            }
        }

        // 3. Total Commercial Bag Requirement Card
        item {
            plan?.let { p ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fertilizer_total_bags_card"),
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
                            text = "Total Fertilizer Procurement Requirement",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Text(
                            text = "For ${p.farmAreaAcres} acres: ${p.variety.varietyName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            BagCountCard(
                                title = "Urea (46% N)",
                                kg = p.totalUreaKg.toInt(),
                                bags = p.urea50kgBags.format(1),
                                color = Color(0xFF15803D),
                                modifier = Modifier.weight(1f)
                            )
                            BagCountCard(
                                title = "DAP (18:46)",
                                kg = p.totalDapKg.toInt(),
                                bags = p.dap50kgBags.format(1),
                                color = Color(0xFF0369A1),
                                modifier = Modifier.weight(1f)
                            )
                            BagCountCard(
                                title = "MOP (60% K)",
                                kg = p.totalMopKg.toInt(),
                                bags = p.mop50kgBags.format(1),
                                color = Color(0xFFB45309),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Zinc Sulphate Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Science, contentDescription = null, tint = ForestGreenPrimary)
                                Column {
                                    Text("Micronutrient (Zinc Sulphate)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                    Text("Apply ${p.zincSulphateKg.format(1)} kg ZnSO₄ at basal puddling (prevents Khaira disease in rice).", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Split Application Schedule Timeline
        item {
            SectionHeader(
                title = "Stage-wise Split Application Schedule",
                subtitle = "Apply in 4 distinct vegetative & reproductive splits"
            )
        }

        plan?.splitSchedules?.let { splits ->
            items(splits) { split ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
                                text = split.stageTitle,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = split.timingDas,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Quantities
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (split.ureaKg > 0) {
                                DosageBadge("Urea", "${split.ureaKg.toInt()} kg", Color(0xFF15803D))
                            }
                            if (split.dapKg > 0) {
                                DosageBadge("DAP", "${split.dapKg.toInt()} kg", Color(0xFF0369A1))
                            }
                            if (split.mopKg > 0) {
                                DosageBadge("MOP", "${split.mopKg.toInt()} kg", Color(0xFFB45309))
                            }
                        }

                        Text(
                            text = split.micronutrientNote,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RatingSelectorRow(
    nutrientName: String,
    selected: SoilRating,
    onSelect: (SoilRating) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(nutrientName, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            SoilRating.values().forEach { r ->
                FilterChip(
                    selected = (selected == r),
                    onClick = { onSelect(r) },
                    label = { Text(r.name.take(3), fontSize = 11.sp) },
                    modifier = Modifier.testTag("chip_${nutrientName.take(1)}_${r.name}")
                )
            }
        }
    }
}

@Composable
fun BagCountCard(
    title: String,
    kg: Int,
    bags: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = color))
            Spacer(modifier = Modifier.height(4.dp))
            Text("$kg kg", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text("$bags bags", style = MaterialTheme.typography.bodySmall.copy(color = color))
        }
    }
}

@Composable
fun DosageBadge(name: String, amount: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(name, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = color))
            Text(amount, style = MaterialTheme.typography.labelSmall.copy(color = color))
        }
    }
}

private fun Double.format(digits: Int): String = "%.${digits}f".format(this)
