package com.example.aquasmart.domain.calculator

import com.example.aquasmart.data.model.SoilRating

data class SoilAnalysisResult(
    val phCategory: String,
    val phColorHex: Long,
    val nitrogenRating: SoilRating,
    val phosphorusRating: SoilRating,
    val potassiumRating: SoilRating,
    val organicCarbonRating: SoilRating,
    val overallHealthScore: Int, // 0 to 100
    val conditionSummary: String,
    val soilAmendmentAdvice: String,
    val organicPractices: String
)

object SoilHealthAnalyzer {

    fun analyzeSoil(
        ph: Double,
        nKgHa: Double,
        pKgHa: Double,
        kKgHa: Double,
        ocPct: Double,
        soilType: String
    ): SoilAnalysisResult {
        // pH Evaluation
        val (phCat, phColor, phAmendment) = when {
            ph < 5.5 -> Triple(
                "Strongly Acidic (pH < 5.5)",
                0xFFDC2626L,
                "Apply Agricultural Lime (CaCO3) @ 2.0 to 2.5 tonnes/ha 3 weeks before transplanting to raise pH and alleviate aluminum/iron toxicity."
            )
            ph < 6.5 -> Triple(
                "Moderately Acidic (pH 5.5 - 6.4)",
                0xFFD97706L,
                "Apply Dolomite or Lime @ 1.0 tonne/ha along with Farmyard Manure (FYM) to balance soil reaction."
            )
            ph <= 7.8 -> Triple(
                "Neutral & Ideal (pH 6.5 - 7.8)",
                0xFF15803DL,
                "Optimal soil reaction. Excellent macronutrient and micronutrient availability. No chemical reclamation needed."
            )
            ph <= 8.5 -> Triple(
                "Moderately Alkaline / Calcareous (pH 7.9 - 8.5)",
                0xFFD97706L,
                "Incorporate green manure (Daincha / Sesbania aculeata) @ 6.25 tonnes/ha. Organic acids released will buffer pH and mobilize phosphorus."
            )
            else -> Triple(
                "Alkali / Sodic Soil (pH > 8.5)",
                0xFFDC2626L,
                "Apply Agricultural Gypsum (CaSO4.2H2O) @ 50% Gypsum Requirement (~2.0 tonnes/ha) followed by impounding water and flushing to leach sodium."
            )
        }

        // Available Nitrogen (kg/ha)
        val nRating = when {
            nKgHa < 280.0 -> SoilRating.LOW
            nKgHa <= 450.0 -> SoilRating.MEDIUM
            else -> SoilRating.HIGH
        }

        // Available Phosphorus P2O5 (kg/ha)
        val pRating = when {
            pKgHa < 11.0 -> SoilRating.LOW
            pKgHa <= 22.0 -> SoilRating.MEDIUM
            else -> SoilRating.HIGH
        }

        // Available Potassium K2O (kg/ha)
        val kRating = when {
            kKgHa < 118.0 -> SoilRating.LOW
            kKgHa <= 280.0 -> SoilRating.MEDIUM
            else -> SoilRating.HIGH
        }

        // Organic Carbon (%)
        val ocRating = when {
            ocPct < 0.50 -> SoilRating.LOW
            ocPct <= 0.75 -> SoilRating.MEDIUM
            else -> SoilRating.HIGH
        }

        // Calculate holistic soil health score out of 100
        var score = 0
        score += when {
            ph in 6.5..7.8 -> 30
            ph in 6.0..8.2 -> 20
            else -> 10
        }
        score += when (nRating) {
            SoilRating.HIGH -> 20
            SoilRating.MEDIUM -> 20
            SoilRating.LOW -> 10
        }
        score += when (pRating) {
            SoilRating.HIGH -> 15
            SoilRating.MEDIUM -> 15
            SoilRating.LOW -> 8
        }
        score += when (kRating) {
            SoilRating.HIGH -> 15
            SoilRating.MEDIUM -> 15
            SoilRating.LOW -> 8
        }
        score += when (ocRating) {
            SoilRating.HIGH -> 20
            SoilRating.MEDIUM -> 15
            SoilRating.LOW -> 8
        }

        val summary = buildString {
            append("Soil health index is $score/100. ")
            if (nRating == SoilRating.LOW) append("Nitrogen is deficient—increase basal/top-dress N by 25%. ")
            if (pRating == SoilRating.LOW) append("Phosphorus is deficient—apply DAP/Single Super Phosphate at basal. ")
            if (kRating == SoilRating.LOW) append("Potassium is low—ensure MOP split at tillering and panicle emergence. ")
            if (nRating == SoilRating.HIGH) append("Nitrogen is elevated—reduce fertilizer N by 25% to prevent lodging and blast disease. ")
            if (nRating == SoilRating.MEDIUM && pRating == SoilRating.MEDIUM && kRating == SoilRating.MEDIUM) {
                append("NPK macronutrients are in optimal agronomic balance. Follow standard TNAU recommended dose. ")
            }
        }

        val organicAdvice = if (ocRating == SoilRating.LOW) {
            "CRITICAL: Soil Organic Carbon (${ocPct}%) is low. In situ green manuring with Daincha (50 kg seeds/ha ploughed in at 45 days) or 12.5 tonnes/ha FYM will rebuild biological fertility and water retention."
        } else {
            "Organic carbon (${ocPct}%) is healthy. Maintain with Azospirillum and Phosphobacteria biofertilizers @ 2 kg/ha mixed with FYM."
        }

        return SoilAnalysisResult(
            phCategory = phCat,
            phColorHex = phColor,
            nitrogenRating = nRating,
            phosphorusRating = pRating,
            potassiumRating = kRating,
            organicCarbonRating = ocRating,
            overallHealthScore = score,
            conditionSummary = summary,
            soilAmendmentAdvice = phAmendment,
            organicPractices = organicAdvice
        )
    }
}
