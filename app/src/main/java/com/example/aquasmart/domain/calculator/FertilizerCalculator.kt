package com.example.aquasmart.domain.calculator

import com.example.aquasmart.data.model.PaddyVariety
import com.example.aquasmart.data.model.SoilRating
import kotlin.math.ceil

data class FertilizerPlan(
    val farmAreaAcres: Double,
    val variety: PaddyVariety,
    val adjustedN_KgTotal: Double,
    val adjustedP_KgTotal: Double,
    val adjustedK_KgTotal: Double,
    val totalUreaKg: Double,
    val totalDapKg: Double,
    val totalMopKg: Double,
    val urea50kgBags: Double,
    val dap50kgBags: Double,
    val mop50kgBags: Double,
    val zincSulphateKg: Double,
    val splitSchedules: List<FertilizerSplitStage>,
    val agAdvice: String
)

data class FertilizerSplitStage(
    val stageTitle: String,
    val timingDas: String,
    val ureaKg: Double,
    val dapKg: Double,
    val mopKg: Double,
    val micronutrientNote: String
)

object FertilizerCalculator {

    fun calculatePlan(
        areaAcres: Double,
        variety: PaddyVariety,
        nRating: SoilRating,
        pRating: SoilRating,
        kRating: SoilRating
    ): FertilizerPlan {
        val safeArea = if (areaAcres <= 0) 1.0 else areaAcres
        val haFactor = safeArea / 2.471 // 1 ha = 2.471 acres

        // 1. TNAU Recommended Dose of Fertilizer (RDF) in kg/ha
        val (baseN_Ha, baseP_Ha, baseK_Ha) = when {
            variety.durationDays <= 115 -> Triple(120.0, 40.0, 40.0) // Short duration
            variety.durationDays <= 140 -> Triple(150.0, 50.0, 50.0) // Medium duration
            else -> Triple(150.0, 50.0, 50.0) // Long duration
        }

        // 2. Soil Fertility Multipliers
        val nMultiplier = when (nRating) {
            SoilRating.LOW -> 1.25
            SoilRating.MEDIUM -> 1.00
            SoilRating.HIGH -> 0.75
        }
        val pMultiplier = when (pRating) {
            SoilRating.LOW -> 1.25
            SoilRating.MEDIUM -> 1.00
            SoilRating.HIGH -> 0.75
        }
        val kMultiplier = when (kRating) {
            SoilRating.LOW -> 1.25
            SoilRating.MEDIUM -> 1.00
            SoilRating.HIGH -> 0.75
        }

        val targetN_Ha = baseN_Ha * nMultiplier
        val targetP_Ha = baseP_Ha * pMultiplier
        val targetK_Ha = baseK_Ha * kMultiplier

        // Scaled to farm area
        val totalN_Kg = targetN_Ha * haFactor
        val totalP_Kg = targetP_Ha * haFactor
        val totalK_Kg = targetK_Ha * haFactor

        // 3. Commercial Fertilizer Calculation
        // DAP supplies 46% P2O5 and 18% N
        val dapNeededKg = totalP_Kg / 0.46
        val nitrogenFromDap = dapNeededKg * 0.18

        // Remaining N supplied by Urea (46% N)
        val remainingN_Kg = (totalN_Kg - nitrogenFromDap).coerceAtLeast(0.0)
        val ureaNeededKg = remainingN_Kg / 0.46

        // MOP supplies 60% K2O
        val mopNeededKg = totalK_Kg / 0.60

        // Zinc Sulphate (25 kg/ha basal)
        val zincKg = 25.0 * haFactor

        // 4. Split Applications:
        // Basal: All DAP (100% P), 25% of Urea, 25% of MOP + Zinc Sulphate
        val basalUrea = ureaNeededKg * 0.25
        val basalDap = dapNeededKg
        val basalMop = mopNeededKg * 0.25

        // Tillering (20-25 DAT): 25% Urea, 25% MOP
        val tilleringUrea = ureaNeededKg * 0.25
        val tilleringMop = mopNeededKg * 0.25

        // Panicle Initiation (40-45 DAT): 30% Urea, 30% MOP
        val panicleUrea = ureaNeededKg * 0.30
        val panicleMop = mopNeededKg * 0.30

        // Heading / Flowering (60-65 DAT): 20% Urea, 20% MOP
        val headingUrea = ureaNeededKg * 0.20
        val headingMop = mopNeededKg * 0.20

        val splits = listOf(
            FertilizerSplitStage(
                stageTitle = "Basal Application",
                timingDas = "At last puddling before transplanting",
                ureaKg = basalUrea,
                dapKg = basalDap,
                mopKg = basalMop,
                micronutrientNote = "Apply ${zincKg.format(1)} kg Zinc Sulphate (do not mix Zn directly with DAP in same bucket)."
            ),
            FertilizerSplitStage(
                stageTitle = "Active Tillering",
                timingDas = "20 - 25 Days After Transplanting",
                ureaKg = tilleringUrea,
                dapKg = 0.0,
                mopKg = tilleringMop,
                micronutrientNote = "Broadcast in thin standing water (2-3 cm). Avoid draining field for 48 hours."
            ),
            FertilizerSplitStage(
                stageTitle = "Panicle Initiation",
                timingDas = "40 - 45 Days After Transplanting",
                ureaKg = panicleUrea,
                dapKg = 0.0,
                mopKg = panicleMop,
                micronutrientNote = "Critical top-dress for grain count and panicle length. Ensure field is adequately moist."
            ),
            FertilizerSplitStage(
                stageTitle = "Heading / Flowering",
                timingDas = "60 - 65 Days After Transplanting",
                ureaKg = headingUrea,
                dapKg = 0.0,
                mopKg = headingMop,
                micronutrientNote = "Apply remaining nitrogen and potassium to prevent premature flag-leaf senescence."
            )
        )

        val advice = "Fertilizer advisory tailored to ${variety.varietyName} on $safeArea acres. " +
                "Soil N: ${nRating.name}, P: ${pRating.name}, K: ${kRating.name}. " +
                "Total commercial bags needed: ${(ureaNeededKg / 50.0).format(1)} bags Urea, " +
                "${(dapNeededKg / 50.0).format(1)} bags DAP, ${(mopNeededKg / 50.0).format(1)} bags MOP."

        return FertilizerPlan(
            farmAreaAcres = safeArea,
            variety = variety,
            adjustedN_KgTotal = totalN_Kg,
            adjustedP_KgTotal = totalP_Kg,
            adjustedK_KgTotal = totalK_Kg,
            totalUreaKg = ureaNeededKg,
            totalDapKg = dapNeededKg,
            totalMopKg = mopNeededKg,
            urea50kgBags = ureaNeededKg / 50.0,
            dap50kgBags = dapNeededKg / 50.0,
            mop50kgBags = mopNeededKg / 50.0,
            zincSulphateKg = zincKg,
            splitSchedules = splits,
            agAdvice = advice
        )
    }

    private fun Double.format(digits: Int): String = "%.${digits}f".format(this)
}
