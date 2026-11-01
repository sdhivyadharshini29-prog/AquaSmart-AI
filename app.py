
import streamlit as st
from modules.soil_analysis import analyze_soil
from modules.irrigation import analyze_irrigation
from modules.weather import analyze_weather
from modules.crop_recommendation import recommend_crop
# Page configuration
st.set_page_config(
    page_title="AquaSmart AI",
    page_icon="🌱",
    layout="wide"
)

st.title("🌱 AquaSmart AI")
st.subheader("Smart Agriculture and Irrigation Management")

st.write(
    "A decision-support prototype for farmers. "
    "We are starting with paddy cultivation."
)

st.divider()

# Farmer information
st.header("👨‍🌾 Farmer and Farm Details")

farmer_name = st.text_input("Farmer Name")
location = st.text_input("Farm Location")

farm_area = st.number_input(
    "Farm Area (acres)",
    min_value=0.0,
    step=0.5
)

st.divider()

# Soil analysis
st.header("🧪 Soil Analysis")

ph = st.number_input(
    "Soil pH",
    min_value=0.0,
    max_value=14.0,
    value=6.5,
    step=0.1
)

nitrogen = st.number_input(
    "Nitrogen (enter soil-test value)",
    min_value=0.0,
    value=0.0
)

phosphorus = st.number_input(
    "Phosphorus (enter soil-test value)",
    min_value=0.0,
    value=0.0
)

potassium = st.number_input(
    "Potassium (enter soil-test value)",
    min_value=0.0,
    value=0.0
)

if st.button("Analyze Soil"):
    try:
        result = analyze_soil(
            ph, nitrogen, phosphorus, potassium
        )

        st.success("Soil data analyzed successfully!")

        st.subheader("Soil Analysis Results")

        st.write("pH:", result["pH"])
        st.write("pH Status:", result["pH_status"])

        st.write("Nitrogen:", result["Nitrogen"])
        st.write("Phosphorus:", result["Phosphorus"])
        st.write("Potassium:", result["Potassium"])

        st.info(result["note"])

    except ValueError as error:
        st.error(str(error))

st.divider()

# Paddy irrigation module
st.header("💧 Paddy Irrigation Assessment")

st.write(
    "Enter your current field conditions "
    "to receive a preliminary irrigation assessment."
)

soil_moisture = st.number_input(
    "Soil Moisture (%)",
    min_value=0.0,
    max_value=100.0,
    value=40.0,
    step=1.0
)

rainfall = st.number_input(
    "Recent Rainfall (mm)",
    min_value=0.0,
    value=0.0,
    step=1.0
)

growth_stage = st.selectbox(
    "Paddy Growth Stage",
    [
        "Seedling",
        "Tillering",
        "Panicle initiation",
        "Flowering",
        "Maturity"
    ]
)

if st.button("Assess Irrigation"):
    try:
        result = analyze_irrigation(
            soil_moisture,
            rainfall,
            growth_stage
        )

        st.success("Irrigation assessment completed!")

        st.subheader("Assessment Results")

        st.write(
            "Soil Moisture:",
            result["soil_moisture"],
            "%"
        )

        st.write(
            "Recent Rainfall:",
            result["rainfall"],
            "mm"
        )

        st.write(
            "Growth Stage:",
            result["growth_stage"]
        )

        st.write(
            "Field Status:",
            result["status"]
        )

        st.info(result["recommendation"])

        st.caption(
            "Prototype assessment only. "
            "Verify field water depth, soil conditions "
            "and local agricultural guidance before irrigation."
        )

    except ValueError as error:
        st.error(str(error))

st.divider()

# Weather analysis module
st.header("🌦️ Weather Information")

st.write(
    "Enter weather observations to analyse "
    "basic environmental conditions."
)

temperature = st.number_input(
    "Temperature (°C)",
    min_value=-10.0,
    max_value=60.0,
    value=30.0
)

humidity = st.number_input(
    "Humidity (%)",
    min_value=0.0,
    max_value=100.0,
    value=60.0
)

weather_rainfall = st.number_input(
    "Rainfall (mm)",
    min_value=0.0,
    value=0.0
)

wind_speed = st.number_input(
    "Wind Speed (km/h)",
    min_value=0.0,
    value=10.0
)

if st.button("Analyze Weather"):
    try:
        result = analyze_weather(
            temperature,
            humidity,
            weather_rainfall,
            wind_speed
        )

        st.success("Weather analysis completed!")

        st.subheader("Weather Analysis Results")

        st.write(
            "Temperature:",
            result["temperature"],
            "°C"
        )

        st.write(
            "Temperature Status:",
            result["temperature_status"]
        )

        st.write(
            "Humidity:",
            result["humidity"],
            "%"
        )

        st.write(
            "Humidity Status:",
            result["humidity_status"]
        )

        st.write(
            "Rainfall:",
            result["rainfall"],
            "mm"
        )

        st.write(
            "Rainfall Status:",
            result["rainfall_status"]
        )

        st.write(
            "Wind Speed:",
            result["wind_speed"],
            "km/h"
        )

        st.caption(
            "Manual weather observations only. "
            "This is not a live weather forecast."
        )

    except ValueError as error:
        st.error(str(error))

st.divider()

# Crop recommendation module
st.header("🌾 Crop Suitability Screening")

st.write(
    "Check whether paddy is a potential candidate "
    "based on basic soil and water information."
)

crop_name = st.selectbox(
    "Select Crop",
    ["Paddy", "Other crop (not yet supported)"]
)

crop_ph = st.number_input(
    "Soil pH for Crop Assessment",
    min_value=0.0,
    max_value=14.0,
    value=6.5,
    step=0.1
)

water_available = st.radio(
    "Is irrigation water available?",
    ["Yes", "No"]
)

if st.button("Check Crop Suitability"):
    try:
        selected_crop = (
            "Paddy" if crop_name == "Paddy"
            else "Other crop"
        )

        result = recommend_crop(
            crop_ph,
            water_available == "Yes",
            selected_crop
        )

        st.subheader("Crop Assessment Result")

        st.write("Selected Crop:", result["crop"])
        st.write("Status:", result["status"])

        st.info(result["recommendation"])

        st.caption(
            "Preliminary prototype screening only. "
            "Not a validated crop recommendation."
        )

    except ValueError as error:
        st.error(str(error))
