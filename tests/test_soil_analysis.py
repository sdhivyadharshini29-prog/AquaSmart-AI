
import pytest
from modules.soil_analysis import analyze_soil


def test_near_neutral_soil():
    result = analyze_soil(6.5, 40, 20, 30)
    assert result["pH_status"] == "Near neutral"


def test_acidic_soil():
    result = analyze_soil(5.0, 40, 20, 30)
    assert result["pH_status"] == "Acidic"


def test_alkaline_soil():
    result = analyze_soil(8.0, 40, 20, 30)
    assert result["pH_status"] == "Alkaline"


def test_invalid_ph():
    with pytest.raises(ValueError):
        analyze_soil(15, 40, 20, 30)


def test_negative_nitrogen():
    with pytest.raises(ValueError):
        analyze_soil(6.5, -10, 20, 30)

def test_minimum_ph():
    result = analyze_soil(0, 10, 10, 10)
    assert result["pH_status"] == "Acidic"


def test_maximum_ph():
    result = analyze_soil(14, 10, 10, 10)
    assert result["pH_status"] == "Alkaline"
