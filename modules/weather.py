
def analyze_weather(temperature, humidity, rainfall, wind_speed):
    """
    Basic weather condition analysis.
    Inputs are manually entered demonstration data.
    """

    # Validate inputs
    if not -10 <= temperature <= 60:
        raise ValueError(
            "Temperature must be between -10 and 60 degrees Celsius."
        )

    if not 0 <= humidity <= 100:
        raise ValueError(
            "Humidity must be between 0 and 100 percent."
        )

    if rainfall < 0:
        raise ValueError("Rainfall cannot be negative.")

    if wind_speed < 0:
        raise ValueError("Wind speed cannot be negative.")

    # Basic weather observations
    if rainfall >= 20:
        rainfall_status = "Heavy rainfall recorded"
    elif rainfall > 0:
        rainfall_status = "Rainfall recorded"
    else:
        rainfall_status = "No rainfall recorded"

    if temperature >= 35:
        temperature_status = "High temperature"
    elif temperature <= 20:
        temperature_status = "Low temperature"
    else:
        temperature_status = "Moderate temperature"

    if humidity >= 80:
        humidity_status = "High humidity"
    elif humidity <= 40:
        humidity_status = "Low humidity"
    else:
        humidity_status = "Moderate humidity"

    return {
        "temperature": temperature,
        "humidity": humidity,
        "rainfall": rainfall,
        "wind_speed": wind_speed,
        "temperature_status": temperature_status,
        "humidity_status": humidity_status,
        "rainfall_status": rainfall_status
    }
