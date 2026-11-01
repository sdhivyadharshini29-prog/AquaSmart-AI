
import pytest
from modules.irrigation import analyze_irrigation


def test_low_soil_moisture():
    result = analyze_irrigation(25, 0, "Tillering")
    assert result["status"] == "Low soil moisture"


def test_significant_rainfall():
    result = analyze_irrigation(60, 25, "Flowering")
    assert result["status"] == "Recent rainfall is significant"


def test_moderate_soil_moisture():
    result = analyze_irrigation(40, 0, "Tillering")
    assert result["status"] == "Moderate soil moisture"


def test_invalid_moisture():
    with pytest.raises(ValueError):
        analyze_irrigation(110, 0, "Tillering")


def test_invalid_growth_stage():
    with pytest.raises(ValueError):
        analyze_irrigation(50, 0, "Unknown")

def test_zero_soil_moisture():
    result = analyze_irrigation(0, 0, "Tillering")
    assert result["status"] == "Low soil moisture"


def test_maximum_soil_moisture():
    result = analyze_irrigation(100, 0, "Tillering")
    assert result["status"] == "Higher soil moisture"


def test_zero_rainfall():
    result = analyze_irrigation(40, 0, "Flowering")
    assert result["status"] == "Moderate soil moisture"
