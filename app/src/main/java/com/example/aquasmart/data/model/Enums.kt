package com.example.aquasmart.data.model

enum class CropGrowthStage(
    val stageName: String,
    val kc: Double,
    val minWaterCm: Double,
    val maxWaterCm: Double,
    val typicalDasStart: Int,
    val typicalDasEnd: Int,
    val criticalWaterStage: Boolean,
    val agronomicAdvice: String
) {
    NURSERY(
        stageName = "Nursery & Land Prep",
        kc = 1.10,
        minWaterCm = 2.0,
        maxWaterCm = 3.0,
        typicalDasStart = 0,
        typicalDasEnd = 20,
        criticalWaterStage = false,
        agronomicAdvice = "Maintain shallow 2-3 cm water layer to protect delicate seedlings and prevent weed emergence."
    ),
    VEGETATIVE_TILLERING(
        stageName = "Active Tillering",
        kc = 1.15,
        minWaterCm = 2.0,
        maxWaterCm = 5.0,
        typicalDasStart = 21,
        typicalDasEnd = 45,
        criticalWaterStage = false,
        agronomicAdvice = "Adopt Alternate Wetting & Drying (AWD). Allow water to disappear 1-2 days before re-irrigating to stimulate deep root anchorage."
    ),
    PANICLE_INITIATION(
        stageName = "Panicle Initiation",
        kc = 1.25,
        minWaterCm = 5.0,
        maxWaterCm = 5.0,
        typicalDasStart = 46,
        typicalDasEnd = 70,
        criticalWaterStage = true,
        agronomicAdvice = "CRITICAL STAGE: Do not allow moisture stress. Maintain continuous 5 cm water depth to ensure maximum spikelet formation."
    ),
    FLOWERING_HEADING(
        stageName = "Flowering & Heading",
        kc = 1.30,
        minWaterCm = 5.0,
        maxWaterCm = 5.0,
        typicalDasStart = 71,
        typicalDasEnd = 90,
        criticalWaterStage = true,
        agronomicAdvice = "PEAK WATER DEMAND: Maintain 5 cm standing water. Drought stress at flowering leads to high spikelet sterility (chaffy grains)."
    ),
    GRAIN_FILLING(
        stageName = "Grain Filling (Milk & Dough)",
        kc = 1.15,
        minWaterCm = 2.0,
        maxWaterCm = 3.0,
        typicalDasStart = 91,
        typicalDasEnd = 110,
        criticalWaterStage = false,
        agronomicAdvice = "Maintain intermittent shallow irrigation (2-3 cm) until dough stage. Prevent complete dry cracking."
    ),
    RIPENING_MATURITY(
        stageName = "Ripening & Maturity",
        kc = 0.95,
        minWaterCm = 0.0,
        maxWaterCm = 0.0,
        typicalDasStart = 111,
        typicalDasEnd = 125,
        criticalWaterStage = false,
        agronomicAdvice = "Drain water completely 10-14 days prior to harvest to promote uniform ripening and facilitate mechanical harvesting."
    )
}

enum class SoilType(
    val displayName: String,
    val percolationMmDay: Double,
    val fieldCapacityPct: Double,
    val wiltingPointPct: Double,
    val description: String
) {
    CLAYEY(
        displayName = "Clayey / Heavy Black",
        percolationMmDay = 2.5,
        fieldCapacityPct = 38.0,
        wiltingPointPct = 20.0,
        description = "High water retention after puddling; low seepage losses."
    ),
    CLAY_LOAM(
        displayName = "Clay Loam",
        percolationMmDay = 3.5,
        fieldCapacityPct = 32.0,
        wiltingPointPct = 16.0,
        description = "Ideal fertile paddy soil with moderate percolation."
    ),
    ALLUVIAL(
        displayName = "Delta Alluvial (Cauvery)",
        percolationMmDay = 4.0,
        fieldCapacityPct = 30.0,
        wiltingPointPct = 14.0,
        description = "Deep fertile silt deposits in river basin delta belts."
    ),
    RED_LOAM(
        displayName = "Red Loam",
        percolationMmDay = 5.0,
        fieldCapacityPct = 25.0,
        wiltingPointPct = 11.0,
        description = "Moderate water holding capacity; requires periodic wetting."
    ),
    SANDY_LOAM(
        displayName = "Sandy Loam",
        percolationMmDay = 7.0,
        fieldCapacityPct = 22.0,
        wiltingPointPct = 9.0,
        description = "High percolation; best suited for AWD or aerobic drip rice."
    )
}

enum class IrrigationMethod(
    val methodName: String,
    val waterSavingPct: Int,
    val efficiencyPct: Int,
    val summary: String
) {
    AWD(
        methodName = "Alternate Wetting & Drying (AWD)",
        waterSavingPct = 28,
        efficiencyPct = 85,
        summary = "Field water tube (Pani Pipe) guided. Irrigate only when water table drops 15 cm below soil surface. Saves 25-30% water."
    ),
    CONTINUOUS_FLOODING(
        methodName = "Continuous Flooding (Traditional)",
        waterSavingPct = 0,
        efficiencyPct = 60,
        summary = "Continuous 5 cm standing water layer throughout vegetative to flowering stages."
    ),
    DRIP_SYSTEM(
        methodName = "Precision Aerobic Drip",
        waterSavingPct = 42,
        efficiencyPct = 92,
        summary = "Daily fertigation and micro-emitter delivery direct to root zone without continuous standing puddle."
    ),
    FURROW_BORDER(
        methodName = "Border Strip & Furrow",
        waterSavingPct = 12,
        efficiencyPct = 70,
        summary = "Controlled channel flow across bounded parcels."
    )
}

enum class PaddyVariety(
    val code: String,
    val varietyName: String,
    val durationDays: Int,
    val yieldPotentialTonsHa: Double,
    val season: String,
    val keyFeatures: String
) {
    ADT_43(
        code = "ADT 43",
        varietyName = "ADT 43 (Kuruvai Special)",
        durationDays = 110,
        yieldPotentialTonsHa = 6.0,
        season = "Kuruvai / Kar (Jun - Sep)",
        keyFeatures = "Short duration, white medium slender grain, high tillering, resistant to blast."
    ),
    CO_51(
        code = "CO 51",
        varietyName = "TNAU Rice CO 51",
        durationDays = 110,
        yieldPotentialTonsHa = 6.5,
        season = "Kuruvai / Late Thaladi / Navarai",
        keyFeatures = "Excellent drought tolerance, non-lodging, high milling turnout (68%)."
    ),
    BPT_5204(
        code = "BPT 5204",
        varietyName = "BPT 5204 (Samba Mahsuri)",
        durationDays = 135,
        yieldPotentialTonsHa = 6.2,
        season = "Samba / Thaladi (Aug - Jan)",
        keyFeatures = "Premium superfine cooking quality, premium market price, highly responsive to NPK."
    ),
    CR_1009(
        code = "CR 1009 Sub 1",
        varietyName = "CR 1009 Sub 1 (Savithri)",
        durationDays = 155,
        yieldPotentialTonsHa = 6.8,
        season = "Thaladi / Late Samba (Sep - Feb)",
        keyFeatures = "Submergence-tolerant (survives 14 days under delta flood water), bold white grain."
    ),
    PONNI(
        code = "White Ponni",
        varietyName = "Improved White Ponni",
        durationDays = 140,
        yieldPotentialTonsHa = 5.5,
        season = "Samba Season",
        keyFeatures = "Traditional Tamil Nadu table rice, slender grains, fine aroma, susceptible to blast."
    ),
    ASD_16(
        code = "ASD 16",
        varietyName = "ASD 16 (Ambasamudram)",
        durationDays = 115,
        yieldPotentialTonsHa = 6.0,
        season = "Kar / Pishanam",
        keyFeatures = "Short bold grain, suited for southern districts (Tirunelveli, Tenkasi)."
    )
}

enum class DistrictZone(
    val districtName: String,
    val agroZone: String,
    val defaultTempC: Double,
    val defaultHumidityPct: Int,
    val defaultEToMmDay: Double
) {
    THANJAVUR("Thanjavur", "Cauvery Delta Zone", 32.5, 72, 5.4),
    TIRUVARUR("Tiruvarur", "Cauvery Delta Zone", 31.8, 78, 5.1),
    NAGAPATTINAM("Nagapattinam", "Coastal Delta Zone", 31.2, 82, 4.9),
    TIRUCHI("Tiruchirappalli", "Central Zone", 34.0, 65, 5.8),
    MADURAI("Madurai", "Southern Zone", 33.5, 64, 5.6),
    COIMBATORE("Coimbatore", "Western Zone", 29.5, 68, 4.8),
    ERODE("Erode", "Western Zone", 32.0, 66, 5.3),
    TIRUNELVELI("Tirunelveli", "Tamirabarani Basin", 33.0, 70, 5.5)
}

enum class SoilRating(val label: String) {
    LOW("Low - Deficient"),
    MEDIUM("Medium - Optimal"),
    HIGH("High - Abundant")
}

enum class SeverityLevel(val label: String) {
    MILD("Mild / Early Signs"),
    MODERATE("Moderate / Action Required"),
    SEVERE("Severe / Critical Intervention")
}
