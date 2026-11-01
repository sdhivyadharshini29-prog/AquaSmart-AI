
def analyze_irrigation(soil_moisture, rainfall, growth_stage):
    """
    Preliminary irrigation screening for paddy.
    This is a prototype rule-based assessment,
    not a validated irrigation prescription.
    """

    if not 0 <= soil_moisture <= 100:
        raise ValueError("Soil moisture must be between 0 and 100.")

    if rainfall < 0:
        raise ValueError("Rainfall cannot be negative.")

    valid_stages = [
        "Seedling",
        "Tillering",
        "Panicle initiation",
        "Flowering",
        "Maturity"
    ]

    if growth_stage not in valid_stages:
        raise ValueError("Please select a valid growth stage.")

    # Preliminary screening rules
    if rainfall >= 20:
        status = "Recent rainfall is significant"
        recommendation = (
            "Check field water depth before adding irrigation. "
            "Avoid unnecessary watering."
        )

    elif soil_moisture < 30:
        status = "Low soil moisture"
        recommendation = (
            "Inspect the field for water stress and check "
            "soil and water availability before irrigation."
        )

    elif soil_moisture < 50:
        status = "Moderate soil moisture"
        recommendation = (
            "Monitor field moisture and crop condition. "
            "Reassess before the next irrigation."
        )

    else:
        status = "Higher soil moisture"
        recommendation = (
            "Check standing water depth and field drainage. "
            "Additional irrigation may not be necessary."
        )

    return {
        "soil_moisture": soil_moisture,
        "rainfall": rainfall,
        "growth_stage": growth_stage,
        "status": status,
        "recommendation": recommendation
    }
