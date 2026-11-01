
def recommend_crop(soil_ph, water_available, crop_name):
    """
    Preliminary crop suitability screening.
    This is an illustrative prototype, not a validated
    agricultural crop recommendation model.
    """

    if not 0 <= soil_ph <= 14:
        raise ValueError("Soil pH must be between 0 and 14.")

    if not isinstance(water_available, bool):
        raise ValueError("Water availability must be True or False.")

    supported_crops = ["Paddy"]

    if crop_name not in supported_crops:
        return {
            "crop": crop_name,
            "status": "Not yet supported",
            "recommendation": (
                "This crop has not yet been configured. "
                "Crop-specific soil and climate information "
                "must be added before screening."
            )
        }

    # Illustrative pH screening band for the prototype
    if 5.5 <= soil_ph <= 7.5 and water_available:
        status = "Potential candidate"
        recommendation = (
            "Paddy can be considered for further assessment. "
            "Check soil type, local season, field conditions, "
            "water supply and agricultural guidance."
        )

    else:
        status = "Further assessment required"
        recommendation = (
            "The entered conditions need further evaluation. "
            "Check soil properties, water availability and "
            "local agricultural recommendations before selecting a crop."
        )

    return {
        "crop": crop_name,
        "soil_ph": soil_ph,
        "water_available": water_available,
        "status": status,
        "recommendation": recommendation
    }
