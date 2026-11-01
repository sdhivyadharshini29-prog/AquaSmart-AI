
from modules.irrigation_baseline import baseline_irrigation
from modules.irrigation import analyze_irrigation


def test_low_moisture_comparison():
    moisture = 25
    rainfall = 0
    stage = "Tillering"

    baseline_result = baseline_irrigation(moisture)

    aquasmart_result = analyze_irrigation(
        moisture, rainfall, stage
    )

    assert baseline_result == "Irrigation check required"
    assert aquasmart_result["status"] == "Low soil moisture"


def test_rainfall_comparison():
    moisture = 60
    rainfall = 25
    stage = "Flowering"

    baseline_result = baseline_irrigation(moisture)

    aquasmart_result = analyze_irrigation(
        moisture, rainfall, stage
    )

    assert baseline_result == "No immediate irrigation check required"
    assert aquasmart_result["status"] == "Recent rainfall is significant"
