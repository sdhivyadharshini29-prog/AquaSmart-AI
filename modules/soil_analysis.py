
def analyze_soil(ph, nitrogen, phosphorus, potassium):
    """
    Basic soil data analysis for the AquaSmart AI prototype.
    NPK values are recorded but not classified until
    their units and crop-specific reference ranges are defined.
    """

    # Validate soil pH
    if not 0 <= ph <= 14:
        raise ValueError("Soil pH must be between 0 and 14.")

    # Identify the pH category
    if ph < 5.5:
        ph_status = "Acidic"
    elif ph <= 7.5:
        ph_status = "Near neutral"
    else:
        ph_status = "Alkaline"

    # Validate NPK values
    if min(nitrogen, phosphorus, potassium) < 0:
        raise ValueError("NPK values cannot be negative.")

    return {
        "pH": ph,
        "pH_status": ph_status,
        "Nitrogen": nitrogen,
        "Phosphorus": phosphorus,
        "Potassium": potassium,
        "note": "NPK interpretation requires soil-test units and crop-specific reference ranges."
    }
