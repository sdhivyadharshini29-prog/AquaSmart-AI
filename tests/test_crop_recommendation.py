
import pytest
from modules.crop_recommendation import recommend_crop


def test_paddy_suitable_conditions():
    result = recommend_crop(6.5, True, "Paddy")
    assert result["status"] == "Potential candidate"


def test_paddy_without_water():
    result = recommend_crop(6.5, False, "Paddy")
    assert result["status"] == "Further assessment required"


def test_unsupported_crop():
    result = recommend_crop(6.5, True, "Maize")
    assert result["status"] == "Not yet supported"


def test_invalid_ph():
    with pytest.raises(ValueError):
        recommend_crop(15, True, "Paddy")


def test_invalid_water_input():
    with pytest.raises(ValueError):
        recommend_crop(6.5, "Yes", "Paddy")

def test_minimum_ph():
    result = recommend_crop(0, True, "Paddy")
    assert result["status"] == "Further assessment required"


def test_maximum_ph():
    result = recommend_crop(14, True, "Paddy")
    assert result["status"] == "Further assessment required"


def test_ph_lower_suitable_boundary():
    result = recommend_crop(5.5, True, "Paddy")
    assert result["status"] == "Potential candidate"


def test_ph_upper_suitable_boundary():
    result = recommend_crop(7.5, True, "Paddy")
    assert result["status"] == "Potential candidate"

