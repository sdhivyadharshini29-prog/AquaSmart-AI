# AquaSmart AI – Smart Agriculture Decision Support System

## Project Overview

AquaSmart AI is an agriculture decision-support prototype designed to help users assess soil conditions, irrigation requirements, weather conditions, and preliminary crop suitability.

The initial MVP focuses on paddy (rice) cultivation, with a long-term architecture goal of supporting agricultural and horticultural crops.

## Core Features

1. **Soil Analysis:** Basic soil pH classification and NPK input assessment.
2. **Irrigation Assessment:** Preliminary irrigation screening using soil moisture and recent rainfall.
3. **Weather Analysis:** Classification of manually entered weather conditions.
4. **Crop Suitability:** Preliminary paddy suitability screening based on soil pH and water availability.

## Technology Stack

- Python
- Streamlit
- Pandas
- NumPy
- Plotly
- Scikit-learn
- Pytest

## Project Structure

```text
AquaSmart-AI/
├── app.py
├── requirements.txt
├── modules/
│   ├── soil_analysis.py
│   ├── irrigation.py
│   ├── irrigation_baseline.py
│   ├── weather.py
│   └── crop_recommendation.py
├── tests/
│   ├── test_soil_analysis.py
│   ├── test_irrigation.py
│   ├── test_weather.py
│   ├── test_crop_recommendation.py
│   └── test_baseline_comparison.py
├── docs/
│   └── testing_metrics.md
└── README.md
```

## Installation and Execution

1. Clone the repository.
2. Open the Python project folder.
3. Create a virtual environment:

```bash
python -m venv venv
```

4. Activate it on Windows CMD:

```cmd
venv\Scripts\activate.bat
```

5. Install dependencies:

```bash
pip install -r requirements.txt
```

6. Run the application:

```bash
python -m streamlit run app.py
```

7. Open `http://localhost:8501` in your browser.

## Testing

Run the automated test suite:

```bash
python -m pytest -v
```

Latest recorded result:

- Total tests: 35
- Passed: 35
- Failed: 0
- Execution time: 0.08 seconds

The pass rate represents software test execution, not agricultural accuracy.

## Current Limitations

- Irrigation thresholds are illustrative and require agronomic validation.
- Weather information is manually entered; live weather integration is not implemented.
- Crop suitability currently supports preliminary paddy screening only.
- No field validation or measured yield and water-saving evaluation has been completed.
- The current MVP uses rule-based logic rather than a trained predictive AI model.

## Future Enhancements

- Support for more agricultural and horticultural crops.
- Live weather API integration.
- Crop-specific soil and irrigation recommendations.
- Validated agricultural datasets and machine-learning models.
- Multilingual farmer-friendly interface.
- Field testing and performance evaluation.

## Project Goal

To develop a scalable, accessible agriculture decision-support platform that can eventually support farmers across multiple crops and growing conditions.