package com.example.aquasmart.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aquasmart.ui.screens.*
import com.example.ui.theme.*

enum class AquaNavDestination(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    IRRIGATION("Irrigation", Icons.Default.WaterDrop),
    SOIL("Soil Health", Icons.Default.Science),
    PADDY("Paddy Crop", Icons.Default.Spa),
    FERTILIZER("Fertilizer", Icons.Default.Eco),
    WEATHER("Weather", Icons.Default.Cloud),
    CROP_HEALTH("Crop Health", Icons.Default.HealthAndSafety),
    FARMS("Farms", Icons.Default.Agriculture),
    REPORTS("Reports", Icons.Default.Assessment)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AquaSmartApp(
    viewModel: AquaSmartViewModel,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(AquaNavDestination.DASHBOARD) }
    var showMoreMenuSheet by remember { mutableStateOf(false) }

    val activeFarm by viewModel.activeFarm.collectAsState()

    // BackHandler: If on a sub-screen, pressing Back returns to Dashboard
    BackHandler(enabled = currentDestination != AquaNavDestination.DASHBOARD) {
        currentDestination = AquaNavDestination.DASHBOARD
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ForestGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "AquaSmart AI",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Smarter Water • Healthier Crops",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { currentDestination = AquaNavDestination.FARMS }
                            .testTag("topbar_farm_chip"),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(14.dp))
                            Text(
                                text = activeFarm?.district ?: "Thanjavur",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                val primaryNavItems = listOf(
                    AquaNavDestination.DASHBOARD,
                    AquaNavDestination.IRRIGATION,
                    AquaNavDestination.SOIL,
                    AquaNavDestination.PADDY
                )

                primaryNavItems.forEach { dest ->
                    NavigationBarItem(
                        selected = (currentDestination == dest),
                        onClick = { currentDestination = dest },
                        icon = {
                            Icon(imageVector = dest.icon, contentDescription = dest.label)
                        },
                        label = { Text(dest.label, fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_${dest.name.lowercase()}")
                    )
                }

                // More Menu Button
                val isMoreActive = currentDestination in listOf(
                    AquaNavDestination.FERTILIZER,
                    AquaNavDestination.WEATHER,
                    AquaNavDestination.CROP_HEALTH,
                    AquaNavDestination.FARMS,
                    AquaNavDestination.REPORTS
                )

                NavigationBarItem(
                    selected = isMoreActive,
                    onClick = { showMoreMenuSheet = true },
                    icon = {
                        Icon(imageVector = Icons.Default.Apps, contentDescription = "More Modules")
                    },
                    label = { Text("Modules", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_more_modules")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                AquaNavDestination.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToIrrigation = { currentDestination = AquaNavDestination.IRRIGATION },
                    onNavigateToSoil = { currentDestination = AquaNavDestination.SOIL },
                    onNavigateToFertilizer = { currentDestination = AquaNavDestination.FERTILIZER },
                    onNavigateToPaddy = { currentDestination = AquaNavDestination.PADDY },
                    onNavigateToWeather = { currentDestination = AquaNavDestination.WEATHER },
                    onNavigateToCropHealth = { currentDestination = AquaNavDestination.CROP_HEALTH },
                    onNavigateToFarms = { currentDestination = AquaNavDestination.FARMS },
                    onNavigateToReports = { currentDestination = AquaNavDestination.REPORTS }
                )
                AquaNavDestination.IRRIGATION -> IrrigationCalculatorScreen(viewModel = viewModel)
                AquaNavDestination.SOIL -> SoilHealthScreen(viewModel = viewModel)
                AquaNavDestination.PADDY -> PaddyManagementScreen(viewModel = viewModel)
                AquaNavDestination.FERTILIZER -> FertilizerAdvisoryScreen(viewModel = viewModel)
                AquaNavDestination.WEATHER -> WeatherScreen(viewModel = viewModel)
                AquaNavDestination.CROP_HEALTH -> CropHealthScreen(viewModel = viewModel)
                AquaNavDestination.FARMS -> FarmsManagementScreen(viewModel = viewModel)
                AquaNavDestination.REPORTS -> ReportsHistoryScreen(viewModel = viewModel)
            }
        }
    }

    // Modal Bottom Sheet for Secondary Modules
    if (showMoreMenuSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMoreMenuSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Agricultural Modules & Services",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Divider()

                val secondaryModules = listOf(
                    AquaNavDestination.FERTILIZER to "TNAU NPK split scheduling & bag calculator",
                    AquaNavDestination.WEATHER to "Agro-meteorological forecast & rain alerts",
                    AquaNavDestination.CROP_HEALTH to "Symptom observation, blast & pest IPM screening",
                    AquaNavDestination.FARMS to "Manage land parcels & farmer profile",
                    AquaNavDestination.REPORTS to "Audit logs & cumulative water saved tracking"
                )

                secondaryModules.forEach { (dest, desc) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                currentDestination = dest
                                showMoreMenuSheet = false
                            }
                            .testTag("menu_item_${dest.name.lowercase()}"),
                        color = if (currentDestination == dest) ForestGreenPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ForestGreenPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(dest.icon, contentDescription = null, tint = ForestGreenPrimary)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(dest.label, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}
