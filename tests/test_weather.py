
import pytest
from modules.weather import analyze_weather


def test_high_temperature():
    result = analyze_weather(36, 85, 25, 12)
    assert result["temperature_status"] == "High temperature"


def test_high_humidity():
    result = analyze_weather(30, 90, 5, 10)
    assert result["humidity_status"] == "High humidity"


def test_heavy_rainfall():
    result = analyze_weather(30, 60, 25, 10)
    assert result["rainfall_status"] == "Heavy rainfall recorded"


def test_invalid_temperature():
    with pytest.raises(ValueError):
        analyze_weather(70, 60, 5, 10)


def test_invalid_humidity():
    with pytest.raises(ValueError):
        analyze_weather(30, 120, 5, 10)

def test_minimum_temperature():
    result = analyze_weather(-10, 50, 0, 0)
    assert result["temperature_status"] == "Low temperature"


def test_maximum_temperature():
    result = analyze_weather(60, 50, 0, 0)
    assert result["temperature_status"] == "High temperature"


def test_zero_humidity():
    result = analyze_weather(30, 0, 0, 0)
    assert result["humidity_status"] == "Low humidity"


def test_maximum_humidity():
    result = analyze_weather(30, 100, 0, 0)
    assert result["humidity_status"] == "High humidity"
