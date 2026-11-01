
def baseline_irrigation(soil_moisture):
    """
    Simple baseline for software comparison.
    Uses soil moisture only and ignores rainfall
    and crop growth stage.
    """

    if not 0 <= soil_moisture <= 100:
        raise ValueError("Soil moisture must be between 0 and 100.")

    if soil_moisture < 50:
        return "Irrigation check required"

    return "No immediate irrigation check required"
