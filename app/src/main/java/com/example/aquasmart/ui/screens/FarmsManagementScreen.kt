package com.example.aquasmart.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.aquasmart.data.model.DistrictZone
import com.example.aquasmart.data.model.SoilType
import com.example.aquasmart.ui.AquaSmartViewModel
import com.example.aquasmart.ui.components.SectionHeader
import com.example.aquasmart.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmsManagementScreen(
    viewModel: AquaSmartViewModel,
    modifier: Modifier = Modifier
) {
    val farmer by viewModel.farmerProfile.collectAsState()
    val allFarms by viewModel.allFarms.collectAsState()
    val selectedFarmId by viewModel.selectedFarmId.collectAsState()

    var showAddFarmDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    // Add Farm Form State
    var newFarmName by remember { mutableStateOf("") }
    var newFarmArea by remember { mutableStateOf("2.5") }
    var newFarmDistrict by remember { mutableStateOf(DistrictZone.THANJAVUR) }
    var newFarmSoil by remember { mutableStateOf(SoilType.ALLUVIAL) }
    var newFarmWaterSource by remember { mutableStateOf("Canal & Borewell") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("farms_management_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Farmer Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("farmer_profile_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(ForestGreenPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                            }
                            Column {
                                Text(
                                    text = farmer?.name ?: "Karthik Subramanian",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Primary District: ${farmer?.primaryDistrict ?: "Thanjavur"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = { showEditProfileDialog = true },
                            modifier = Modifier.testTag("btn_edit_profile")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                        }
                    }

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Contact Number", style = MaterialTheme.typography.labelSmall)
                            Text(farmer?.phone ?: "+91 94432 18765", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
                        }
                        Column {
                            Text("Email", style = MaterialTheme.typography.labelSmall)
                            Text(farmer?.email ?: "karthik.subramanian@deltafarms.in", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
                        }
                    }
                }
            }
        }

        // 2. Farms Header & Add Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Registered Land Parcels",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${allFarms.size} farms under management",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddFarmDialog = true },
                    modifier = Modifier.testTag("btn_add_farm_dialog"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Farm")
                }
            }
        }

        // 3. Farms List
        items(allFarms) { farm ->
            val isSelected = (farm.id == selectedFarmId)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectFarm(farm.id) }
                    .testTag("farm_card_${farm.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) ForestGreenPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                ),
                border = if (isSelected) BorderStroke(1.5.dp, ForestGreenPrimary) else null,
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
                            text = farm.farmName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        if (isSelected) {
                            StatusBadge(text = "ACTIVE FARM", isPositive = true)
                        } else {
                            Text(
                                text = "Tap to Select",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Area: ${farm.areaAcres} Acres", style = MaterialTheme.typography.bodySmall)
                        Text("District: ${farm.district}", style = MaterialTheme.typography.bodySmall)
                        Text("Soil: ${farm.soilType}", style = MaterialTheme.typography.bodySmall)
                    }

                    Text(
                        text = "Water Source: ${farm.waterSource}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Add New Farm Dialog
    if (showAddFarmDialog) {
        var districtOpen by remember { mutableStateOf(false) }
        var soilOpen by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddFarmDialog = false },
            title = { Text("Register New Farm Parcel") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newFarmName,
                        onValueChange = { newFarmName = it },
                        label = { Text("Farm Parcel Name") },
                        placeholder = { Text("e.g. Vennar Canal Plot") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newFarmArea,
                        onValueChange = { newFarmArea = it },
                        label = { Text("Area (Acres)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // District Selector
                    Box {
                        OutlinedTextField(
                            value = newFarmDistrict.districtName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("District in Tamil Nadu") },
                            trailingIcon = {
                                IconButton(onClick = { districtOpen = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = districtOpen,
                            onDismissRequest = { districtOpen = false }
                        ) {
                            DistrictZone.values().forEach { d ->
                                DropdownMenuItem(
                                    text = { Text(d.districtName) },
                                    onClick = {
                                        newFarmDistrict = d
                                        districtOpen = false
                                    }
                                )
                            }
                        }
                    }

                    // Soil Type Selector
                    Box {
                        OutlinedTextField(
                            value = newFarmSoil.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Soil Type") },
                            trailingIcon = {
                                IconButton(onClick = { soilOpen = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = soilOpen,
                            onDismissRequest = { soilOpen = false }
                        ) {
                            SoilType.values().forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s.displayName) },
                                    onClick = {
                                        newFarmSoil = s
                                        soilOpen = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = newFarmWaterSource,
                        onValueChange = { newFarmWaterSource = it },
                        label = { Text("Water Source") },
                        placeholder = { Text("Canal, Borewell, Tank") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = newFarmName.ifBlank { "Farm Parcel #${allFarms.size + 1}" }
                        val area = newFarmArea.toDoubleOrNull() ?: 2.0
                        viewModel.addNewFarm(
                            name = name,
                            area = area,
                            district = newFarmDistrict.districtName,
                            soilType = newFarmSoil.displayName,
                            waterSource = newFarmWaterSource
                        )
                        showAddFarmDialog = false
                    },
                    modifier = Modifier.testTag("btn_confirm_add_farm")
                ) {
                    Text("Add Farm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFarmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Farmer Profile Dialog
    if (showEditProfileDialog) {
        var pName by remember { mutableStateOf(farmer?.name ?: "") }
        var pPhone by remember { mutableStateOf(farmer?.phone ?: "") }
        var pEmail by remember { mutableStateOf(farmer?.email ?: "") }
        var pDistrict by remember { mutableStateOf(farmer?.primaryDistrict ?: "Thanjavur") }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Update Farmer Profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = pName, onValueChange = { pName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pPhone, onValueChange = { pPhone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pEmail, onValueChange = { pEmail = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pDistrict, onValueChange = { pDistrict = it }, label = { Text("Home District") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveFarmerProfile(pName, pPhone, pEmail, pDistrict)
                        showEditProfileDialog = false
                    },
                    modifier = Modifier.testTag("btn_save_farmer_profile")
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
