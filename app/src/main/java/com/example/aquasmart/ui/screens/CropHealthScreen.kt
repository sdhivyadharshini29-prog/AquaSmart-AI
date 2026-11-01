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
import com.example.aquasmart.data.model.SeverityLevel
import com.example.aquasmart.ui.AquaSmartViewModel
import com.example.aquasmart.ui.components.SectionHeader
import com.example.aquasmart.ui.components.StatusBadge
import com.example.ui.theme.*

data class KnownPaddyIssue(
    val name: String,
    val part: String,
    val commonSymptoms: String,
    val ipmAction: String,
    val organicRemedy: String
)

val KnownIssuesList = listOf(
    KnownPaddyIssue(
        name = "Rice Blast (Magnaporthe oryzae)",
        part = "Leaf & Neck",
        commonSymptoms = "Spindle-shaped elliptical lesions with gray centers and brown borders.",
        ipmAction = "Avoid high nitrogen top-dressing. Keep 2-5 cm standing water layer to inhibit airborne spores.",
        organicRemedy = "Foliar spray with Pseudomonas fluorescens @ 10 g/litre or 3% Neem Seed Kernel Extract (NSKE)."
    ),
    KnownPaddyIssue(
        name = "Brown Plant Hopper (BPH)",
        part = "Stem Base & Tillers",
        commonSymptoms = "Circular drying patches ('hopper burn'), lodging, brown insects clustered at stem base.",
        ipmAction = "Drain standing water immediately (AWD drying) for 3-4 days to break insect breeding cycle.",
        organicRemedy = "Spray 5% NSKE or Neem oil (3000 ppm) @ 3 ml/litre targeting base of tillers. Erect light traps."
    ),
    KnownPaddyIssue(
        name = "Bacterial Leaf Blight (BLB)",
        part = "Leaf Margins",
        commonSymptoms = "Water-soaked streaks along leaf margins turning wavy yellow and bleach-white.",
        ipmAction = "Hold nitrogen fertilizer. Drain field water to prevent bacterial flow between plots.",
        organicRemedy = "Spray fresh cow dung slurry (20%) extract supernatant or Copper Oxychloride @ 2.5 g/litre."
    ),
    KnownPaddyIssue(
        name = "Yellow Stem Borer",
        part = "Central Shoot & Panicle",
        commonSymptoms = "Dead heart (drying central shoot) during tillering; white ears (empty panicles) during flowering.",
        ipmAction = "Release Trichogramma japonicum egg parasitoids @ 100,000/ha at 30 & 37 DAT.",
        organicRemedy = "Install pheromone traps @ 12 traps/ha with TNAU lures to monitor and trap adult moths."
    ),
    KnownPaddyIssue(
        name = "Sheath Blight",
        part = "Leaf Sheaths & Lower Blades",
        commonSymptoms = "Greenish-grey oval or irregular spots resembling snake-skin patterns on leaf sheath.",
        ipmAction = "Provide wider plant spacing (20x15 cm) to allow aeration and reduce humidity around stems.",
        organicRemedy = "Soil application of Trichoderma viride @ 2.5 kg/ha mixed with 50 kg FYM."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropHealthScreen(
    viewModel: AquaSmartViewModel,
    modifier: Modifier = Modifier
) {
    val activeFarm by viewModel.activeFarm.collectAsState()
    val observations by viewModel.farmObservations.collectAsState()

    var showLogDialog by remember { mutableStateOf(false) }
    var selectedIssue by remember { mutableStateOf(KnownIssuesList.first()) }
    var customSymptoms by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf(SeverityLevel.MODERATE) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("crop_health_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("crop_health_header_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
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
                            .background(Color(0xFFDC2626)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Crop Health & Symptom Observation",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                            )
                        )
                        Text(
                            text = "Observation & IPM Screening for ${activeFarm?.farmName ?: "Farm"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 2. Qualified Disclaimer Notice (Mandatory Agronomic Standard)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("crop_health_disclaimer_card"),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(20.dp))
                    Text(
                        text = "Agronomic Qualification: This module assists in screening observable field symptoms and recommending eco-friendly IPM & water management practices. It does not replace certified laboratory plant pathology analysis.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 3. Log New Observation Button
        item {
            Button(
                onClick = { showLogDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_log_crop_observation"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Field Symptoms & Get Guidance")
            }
        }

        // 4. Logged Observations List
        item {
            SectionHeader(
                title = "Logged Observations",
                subtitle = "Active field records for this parcel"
            )
        }

        if (observations.isEmpty()) {
            item {
                Text(
                    text = "No observations logged for this parcel yet. Healthy field!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(observations) { obs ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                text = obs.suspectedCondition,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            StatusBadge(
                                text = obs.severityLevel,
                                isPositive = obs.severityLevel.contains("Mild"),
                                isWarning = obs.severityLevel.contains("Moderate")
                            )
                        }

                        Text(
                            text = "Part: ${obs.affectedPart} • Symptoms: ${obs.observedSymptoms}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ForestGreenPrimary.copy(alpha = 0.08f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Organic & Cultural Intervention:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ForestGreenPrimary))
                                Text(obs.organicControl, style = MaterialTheme.typography.bodySmall)
                                Text("IPM Directive: ${obs.ipmGuideline}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        // 5. Reference Library Section
        item {
            SectionHeader(
                title = "Paddy Pest & Disease Diagnostic Library",
                subtitle = "Common Tamil Nadu rice cultivation symptoms"
            )
        }

        items(KnownIssuesList) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                    )
                    Text("Symptoms: ${item.commonSymptoms}", style = MaterialTheme.typography.bodySmall)
                    Text("Remedy: ${item.organicRemedy}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    // Log Observation Dialog
    if (showLogDialog) {
        var issueDropdownOpen by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showLogDialog = false },
            title = { Text("Record Field Symptom Observation") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select observed condition or symptoms:", style = MaterialTheme.typography.bodySmall)

                    Box {
                        OutlinedTextField(
                            value = selectedIssue.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Suspected Issue") },
                            trailingIcon = {
                                IconButton(onClick = { issueDropdownOpen = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = issueDropdownOpen,
                            onDismissRequest = { issueDropdownOpen = false }
                        ) {
                            KnownIssuesList.forEach { ki ->
                                DropdownMenuItem(
                                    text = { Text(ki.name) },
                                    onClick = {
                                        selectedIssue = ki
                                        customSymptoms = ki.commonSymptoms
                                        issueDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = customSymptoms.ifEmpty { selectedIssue.commonSymptoms },
                        onValueChange = { customSymptoms = it },
                        label = { Text("Observable Symptoms") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Severity Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Severity:", style = MaterialTheme.typography.bodySmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            SeverityLevel.values().forEach { sl ->
                                FilterChip(
                                    selected = (severity == sl),
                                    onClick = { severity = sl },
                                    label = { Text(sl.name.take(4)) }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.recordCropObservation(
                            symptoms = customSymptoms.ifEmpty { selectedIssue.commonSymptoms },
                            affectedPart = selectedIssue.part,
                            suspectedIssue = selectedIssue.name,
                            severity = severity,
                            ipmAdvice = selectedIssue.ipmAction,
                            organicMeasure = selectedIssue.organicRemedy
                        )
                        showLogDialog = false
                    },
                    modifier = Modifier.testTag("btn_confirm_log_observation")
                ) {
                    Text("Save Observation")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
