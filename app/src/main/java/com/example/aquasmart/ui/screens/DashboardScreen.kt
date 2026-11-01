package com.example.aquasmart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aquasmart.data.model.FarmEntity
import com.example.aquasmart.ui.AquaSmartViewModel
import com.example.aquasmart.ui.components.MetricStatCard
import com.example.aquasmart.ui.components.SectionHeader
import com.example.aquasmart.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: AquaSmartViewModel,
    onNavigateToIrrigation: () -> Unit,
    onNavigateToSoil: () -> Unit,
    onNavigateToFertilizer: () -> Unit,
    onNavigateToPaddy: () -> Unit,
    onNavigateToWeather: () -> Unit,
    onNavigateToCropHealth: () -> Unit,
    onNavigateToFarms: () -> Unit,
    onNavigateToReports: () -> Unit,
    modifier: Modifier = Modifier
) {
    val farmer by viewModel.farmerProfile.collectAsState()
    val allFarms by viewModel.allFarms.collectAsState()
    val activeFarm by viewModel.activeFarm.collectAsState()
    val activeCrop by viewModel.activeCropCycle.collectAsState()
    val latestSoil by viewModel.latestSoilRecord.collectAsState()
    val latestIrrigation by viewModel.latestIrrigationRecord.collectAsState()
    val weather by viewModel.currentWeather.collectAsState()
    val irrigationResult by viewModel.irrigationResult.collectAsState()

    var showFarmMenu by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Farmer Welcome & Active Farm Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("farmer_welcome_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Vanakkam, ${farmer?.name ?: "Farmer"}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                            Text(
                                text = "AquaSmart AI • Tamil Nadu Agriculture",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            )
                        }
                        IconButton(
                            onClick = onNavigateToFarms,
                            modifier = Modifier
                                .testTag("manage_farms_icon_button")
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Agriculture,
                                contentDescription = "Manage Farms",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Farm Selector Pill
                    Box {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { showFarmMenu = true }
                                .testTag("active_farm_dropdown_trigger"),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column {
                                        Text(
                                            text = activeFarm?.farmName ?: "Select Farm",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                        Text(
                                            text = "${activeFarm?.areaAcres ?: 0.0} Acres • ${activeFarm?.district ?: ""} • ${activeFarm?.soilType ?: ""}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Switch Farm"
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showFarmMenu,
                            onDismissRequest = { showFarmMenu = false }
                        ) {
                            allFarms.forEach { farm ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(farm.farmName, fontWeight = FontWeight.Bold)
                                            Text(
                                                "${farm.areaAcres} Acres, ${farm.district}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.selectFarm(farm.id)
                                        showFarmMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Terrain,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Weather Alert Banner
        item {
            val alert = weather.irrigationAlert
            if (!alert.isNullOrBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToWeather() }
                        .testTag("weather_alert_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (weather.isAlertUrgent) Color(0xFFFEF3C7) else Color(0xFFE0F2FE)
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(
                                if (weather.isAlertUrgent) Color(0xFFF59E0B) else Color(0xFF0284C7),
                                if (weather.isAlertUrgent) Color(0xFFD97706) else Color(0xFF0369A1)
                            )
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (weather.isAlertUrgent) Color(0xFFF59E0B) else Color(0xFF0284C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (weather.isAlertUrgent) Icons.Default.Warning else Icons.Default.CloudQueue,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Agro-Meteorological Advisory • ${weather.districtName}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (weather.isAlertUrgent) Color(0xFF92400E) else Color(0xFF075985)
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = alert,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (weather.isAlertUrgent) Color(0xFF78350F) else Color(0xFF0C4A6E)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = if (weather.isAlertUrgent) Color(0xFF92400E) else Color(0xFF075985)
                        )
                    }
                }
            }
        }

        // 3. Quick Agronomic Stats Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricStatCard(
                    title = "Active Crop",
                    value = activeCrop?.varietyCode ?: "ADT 43",
                    subtitle = activeCrop?.stage ?: "Tillering Stage",
                    icon = Icons.Default.Grass,
                    accentColor = ForestGreenPrimary,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToPaddy() }
                        .testTag("metric_active_crop")
                )
                MetricStatCard(
                    title = "Soil Health",
                    value = latestSoil?.let { "pH ${it.ph}" } ?: "pH 7.2",
                    subtitle = latestSoil?.phCategory?.take(18) ?: "Optimal",
                    icon = Icons.Default.Science,
                    accentColor = EarthBrown,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToSoil() }
                        .testTag("metric_soil_health")
                )
            }
        }

        // 4. Smart Irrigation Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_irrigation_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AquaBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = AquaBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Smart Irrigation Status",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "Alternate Wetting & Drying (AWD)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        val action = irrigationResult?.recommendationAction ?: "AWD DRYING"
                        val isIrrigNeeded = irrigationResult?.isIrrigationNeededToday ?: false
                        StatusBadge(
                            text = if (isIrrigNeeded) "IRRIGATE" else "DRYING OK",
                            isPositive = !isIrrigNeeded,
                            isWarning = isIrrigNeeded
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = irrigationResult?.recommendationSummary
                            ?: "Field water tube is at safe drying level. Maintain aeration.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Irrigation Metrics Bar
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Water Depth", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    text = "${irrigationResult?.netIrrigationDepthMm?.toInt() ?: 35} mm",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AquaBlue
                                    )
                                )
                            }
                            Divider(
                                modifier = Modifier
                                    .height(32.dp)
                                    .width(1.dp)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Estimated Volume", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    text = "${((irrigationResult?.waterVolumeLitres ?: 495740.0) / 1000).toInt()} m³",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                            Divider(
                                modifier = Modifier
                                    .height(32.dp)
                                    .width(1.dp)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Water Saved", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    text = "${((irrigationResult?.waterSavedLitres ?: 192800.0) / 1000).toInt()} m³",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = StatusOptimal
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onNavigateToIrrigation,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_open_irrigation_calculator"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AquaBlue
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Calculate & Adjust Irrigation", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // 5. Quick Modules Grid
        item {
            SectionHeader(
                title = "Agricultural Modules",
                subtitle = "Precision decision support for rice cultivation"
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleCard(
                        title = "Soil Health",
                        description = "NPK & pH Analysis",
                        icon = Icons.Default.Science,
                        color = Color(0xFF15803D),
                        onClick = onNavigateToSoil,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("module_soil_health")
                    )
                    ModuleCard(
                        title = "Fertilizer Advisory",
                        description = "TNAU Split Dosing",
                        icon = Icons.Default.Eco,
                        color = Color(0xFFD97706),
                        onClick = onNavigateToFertilizer,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("module_fertilizer")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleCard(
                        title = "Paddy Cycle",
                        description = "Growth Stage Pipeline",
                        icon = Icons.Default.Spa,
                        color = Color(0xFF16A34A),
                        onClick = onNavigateToPaddy,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("module_paddy")
                    )
                    ModuleCard(
                        title = "Weather Intelligence",
                        description = "Delta Zones & Rain Alert",
                        icon = Icons.Default.Cloud,
                        color = Color(0xFF0284C7),
                        onClick = onNavigateToWeather,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("module_weather")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleCard(
                        title = "Crop Observation",
                        description = "Pest & Blast Screening",
                        icon = Icons.Default.HealthAndSafety,
                        color = Color(0xFFDC2626),
                        onClick = onNavigateToCropHealth,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("module_crop_health")
                    )
                    ModuleCard(
                        title = "Audit Reports",
                        description = "Irrigation & Soil Logs",
                        icon = Icons.Default.Assessment,
                        color = Color(0xFF4B5563),
                        onClick = onNavigateToReports,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("module_reports")
                    )
                }
            }
        }

        // 6. Agronomic Validation & Transparency Notice
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("agronomic_validation_card"),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = ForestGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Validated Agronomic Standard",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Calibrated using FAO-56 crop coefficient equations, USDA effective rainfall, and Tamil Nadu Agricultural University (TNAU) AWD guidelines. Calculations distinguish sensor telemetry from farmer inputs.",
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
fun ModuleCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
