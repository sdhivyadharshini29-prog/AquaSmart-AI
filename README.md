Candidate Introduction
I am Dhivyadharshini S, a B.Sc. (Hons.) Agriculture graduate from Karunya Institute of Technology and Sciences, Coimbatore, with a strong interest in exploring how Artificial Intelligence and emerging technologies can address real-world challenges in agriculture and environmental sustainability.
My academic background has provided me with an understanding of agricultural systems and farming-related challenges. Alongside this, I have developed foundational technical knowledge through an AI Programming course under the TNSDC Vetri Nichayam Scheme, covering Python programming, data handling, machine learning and introductory deep learning.
I have also completed an LLM Post-Training Internship at Ethara AI, gaining exposure to AI training workflows and data annotation.
I am particularly interested in applying AI to improve agricultural decision-making, optimise natural resource utilisation and support farmers in adapting to changing climatic conditions.
Through EduRankAI's Entrepreneurial & Venture Leadership Programme, I aim to combine my agricultural knowledge and developing technical skills to identify meaningful problems, investigate practical solutions, build working prototypes and validate their real-world usefulness.
My long-term ambition is to contribute to technology-driven agricultural ventures that are affordable, accessible, environmentally responsible and beneficial to farming communities.
Primary interest = Irrigation water productivity optimisation
Improving irrigation decisions and reducing avoidable water wastage through accessible, data-driven recommendations.
AI in Agriculture, Climate & Environmental Sciences
Proposed focus: Irrigation Water Management
Proposed venture concept: AquaSmart AI
Target beneficiaries: Smallholder and marginal farmers across India.
Long-term objective: Develop affordable, accessible technology that helps farmers make better-informed irrigation decisions.
Relevance: Irrigation water management is an important agricultural challenge in India due to water scarcity, irregular rainfall, rising cultivation costs and inefficient water use. Smallholder and marginal farmers need affordable, reliable and easy-to-understand irrigation guidance. A combination of voice messages, SMS and human assistance can make such guidance more accessible.
Capability Evidence: I hold a B.Sc. (Hons.) in Agriculture and have completed an AI Programming course under the TNSDC Vetri Nichayam Scheme. I have basic knowledge of Python, data handling, machine learning and data preprocessing, along with internship experience in LLM post-training and AI data annotation at Ethara AI. These experiences provide a foundation to research the problem and develop a simple, affordable AI-assisted prototype with expert guidance.
Concise Venture Brief – AquaSmart AI
Target Users: Smallholder and marginal farmers in India who need affordable and accessible irrigation guidance.
Problem Evidence: Irregular rainfall, water scarcity and rising irrigation costs make irrigation planning difficult. Research on digital agricultural extension in India shows that combining SMS, voice messages and human support can improve farmers' access to agricultural information.
Proposed Solution: AquaSmart AI is a farmer-friendly irrigation decision-support system that uses crop details, weather information, rainfall and available soil data to suggest when and how much to irrigate. It will share guidance through local-language voice messages, SMS and agricultural extension workers. The initial prototype will be developed using Python and a simple dashboard, with expert review of recommendations.
Success Measures:
•	Accuracy of irrigation estimates compared with recognised agricultural methods.
•	Reduction in unnecessary irrigation in pilot testing.
•	Farmer understanding and usefulness of recommendations.
•	Accessibility and successful delivery through voice and SMS.
•	Positive feedback from farmers and agricultural experts.
1. Prior-art matrix
Existing solution	What it does	What I can learn
FAO CROPWAT	Calculates crop-water requirements.	Use established irrigation methods.
IIT Madras NeerPasanam	Provides weather-based irrigation advice.	Similar solutions already exist.
NASA POWER	Provides weather and climate data.	Use weather data in my prototype.
pyfao56 (GitHub)	Open-source Python tool for irrigation calculations.	Reuse existing tools instead of building everything from scratch.
2. Possible gap
Existing solutions already provide irrigation advice. However, I want to investigate whether small and marginal farmers need a more affordable and easier-to-access service through:
•	Local-language voice messages.
•	Simple SMS alerts.
•	Support from agricultural extension workers.
•	Guidance that does not require expensive sensors or smartphones.
This gap needs to be confirmed through farmer interviews.
3. Feasibility assumptions
Requirement	Proposed approach
Data	NASA POWER, FAO and TNAU resources.
Technology	Python, Pandas and Streamlit.
AI/ML	Begin with basic irrigation calculations; introduce ML only if useful.
Compute	A normal laptop is sufficient for the initial prototype.
Cost	Use free and open-source tools wherever possible.
Delivery	Start with a dashboard and sample SMS/voice messages.
Safety	Validate irrigation recommendations with agricultural experts.
4. Risk register
Risk	Solution
Incorrect advice	Expert verification.
Limited internet access	Voice and SMS support.
Incomplete data	Clearly communicate limitations.
Different crop requirements	Start with one crop.
High operating costs	Test low-cost delivery methods.
Existing competitors	Compare features and identify genuine gaps.
5. Go / Revise / Stop Recommendation
GO – with revisions
I recommend proceeding with a small prototype. Similar irrigation technologies already exist, so I will not claim that my idea is completely new.
My focus will be to develop an affordable, farmer-friendly irrigation guidance system using existing scientific methods, weather data and accessible communication channels. I will test its usefulness with farmers and agricultural experts before considering further development.
References:
•	FAO CROPWAT
•	NASA POWER
•	IIT Madras AquaMAP
•	pyfao56 – GitHub



AquaSmart AI
MVP Development, Testing, Validation and Iteration Report
Project: AquaSmart AI – Smart Agriculture Decision Support System
Initial Crop Focus: Paddy (Rice)
Technology: Python, Streamlit
Project Type: Functional Software MVP
Version: 1.0
Repository: https://github.com/sdhivyadharshini29-prog/AquaSmart-AI
Development Branch: aquasmart-streamlit-integration
________________________________________
1. Project Overview
AquaSmart AI is a smart agriculture decision-support prototype designed to help users understand basic soil conditions, irrigation requirements, weather conditions, and preliminary crop suitability.
The initial MVP focuses on paddy cultivation. The long-term objective is to develop a scalable platform supporting multiple agricultural and horticultural crops.
The current application uses Python-based rule-driven logic and a Streamlit interface to demonstrate its core workflows.
2. Project Objectives
The main objectives are:
•	Develop a usable agriculture decision-support prototype.
•	Provide basic soil pH and NPK assessments.
•	Demonstrate preliminary irrigation screening.
•	Analyse manually entered weather conditions.
•	Provide preliminary paddy suitability screening.
•	Compare the irrigation workflow against a simple baseline.
•	Evaluate software functionality using automated tests.
•	Establish a foundation for future multi-crop support.
3. Technology Stack
Technology	Purpose
Python	Application development
Streamlit	Interactive user interface
Pandas	Data handling
NumPy	Numerical operations
Plotly	Data visualisation support
Scikit-learn	Machine-learning library dependency
Pytest	Automated software testing
Git and GitHub	Version control and code hosting
Note: The current MVP uses rule-based decision logic. The presence of Scikit-learn in the dependencies does not mean a trained machine-learning model is currently deployed.
4. Implemented Core Features
4.1 Soil Analysis
The soil analysis module accepts:
•	Soil pH
•	Nitrogen
•	Phosphorus
•	Potassium
It validates the inputs and provides basic soil pH classification and NPK-related assessment.
4.2 Irrigation Assessment
The irrigation module accepts:
•	Soil moisture percentage
•	Recent rainfall
•	Crop growth stage
It provides preliminary irrigation screening using illustrative soil-moisture and rainfall rules.
4.3 Weather Analysis
The weather module accepts:
•	Temperature
•	Humidity
•	Rainfall
•	Wind speed
It classifies manually entered weather conditions.
Live weather API integration is not yet implemented.
4.4 Crop Suitability
The crop recommendation module currently provides preliminary paddy suitability screening based on soil pH and water availability.
Other crops are clearly identified as unsupported by the current recommendation module.
5. MVP Demonstration
The application was successfully launched using Streamlit and tested through its four main workflows.
Feature	Sample inputs	Demonstrated output
Soil Analysis	pH 6.5, N 80, P 40, K 40	Soil assessment
Irrigation	Moisture 25%, rainfall 0 mm, Tillering	Low soil moisture
Weather	32°C, 75% humidity, 10 mm rainfall, 12 km/h wind	Weather classification
Crop Suitability	Paddy, pH 6.5, water available	Potential candidate
The demonstration confirms that the four application workflows execute successfully with the supplied sample inputs.
6. Testing and Evaluation
Automated testing was performed using Pytest.
6.1 Test Results
Test Module	Tests Passed
Soil Analysis	7
Irrigation Assessment	8
Weather Analysis	9
Crop Suitability	9
Baseline Comparison	2
Total	35
Execution result:
•	Total tests: 35
•	Passed: 35
•	Failed: 0
•	Execution time: 0.08 seconds
•	Software test pass rate: 100%
The 100% pass rate represents the execution of the current automated software tests. It does not represent agricultural prediction accuracy.
6.2 Input Validation and Edge Cases
The automated tests include checks for:
•	Soil pH boundary values.
•	Negative nutrient values.
•	Soil moisture boundary values.
•	Zero rainfall.
•	Temperature boundary values.
•	Humidity boundary values.
•	Crop suitability pH boundaries.
•	Baseline comparison scenarios.
These tests help identify invalid inputs and verify expected software behaviour.
7. Baseline Comparison
A simple soil-moisture-only irrigation baseline was implemented for comparison with the AquaSmart irrigation screening module.
Test Scenario	Simple Baseline	AquaSmart AI
Moisture 25%, rainfall 0 mm	Irrigation check required	Low soil moisture
Moisture 60%, rainfall 25 mm	No immediate irrigation check required	Recent rainfall is significant
Evaluation
The comparison demonstrates that adding rainfall as an input can produce a different preliminary assessment from a moisture-only rule.
However, both approaches are illustrative rule-based systems. This comparison does not establish better agricultural performance, reduced water consumption, or increased crop yield.
8. Development and Iteration Notes
The development process involved the following stages:
Iteration 1 – Project Setup
•	Established the Python project structure.
•	Created a virtual environment.
•	Installed the required dependencies.
•	Configured the Streamlit application.
Iteration 2 – Core Workflow Development
•	Implemented soil analysis.
•	Implemented irrigation screening.
•	Implemented weather classification.
•	Implemented preliminary paddy suitability screening.
Iteration 3 – Software Testing
•	Created automated test modules.
•	Added boundary and edge-case checks.
•	Implemented a simple irrigation baseline.
•	Completed the combined test run with 35 passing tests.
Iteration 4 – Documentation and Version Control
•	Created project README documentation.
•	Updated testing metrics.
•	Preserved the existing Android application.
•	Added the Python MVP to a separate GitHub integration branch.
•	Uploaded the application demonstration screenshots.
These iterations document implementation and software verification. They do not represent completed field trials or validated agronomic improvements.
9. Demonstration Evidence
The following screenshots are included in the GitHub repository:
1.	screenshots/soil_analysis.png
2.	screenshots/irrigation_assessment.png
3.	screenshots/weather_analysis.png
4.	screenshots/crop_suitability.png
Repository:
https://github.com/sdhivyadharshini29-prog/AquaSmart-AI/tree/aquasmart-streamlit-integration
Testing documentation:
docs/testing_metrics.md
10. Current Limitations
The current MVP has the following limitations:
•	Irrigation thresholds are illustrative and require expert agronomic validation.
•	Weather information is entered manually.
•	Crop suitability currently supports preliminary paddy screening only.
•	No trained predictive machine-learning model is deployed.
•	No field trials have been conducted.
•	No measured water-saving or yield-improvement results are available.
•	NPK interpretation requires appropriate units and crop-specific reference standards.
11. Future Enhancements
The proposed future development roadmap includes:
1.	Add support for multiple agricultural and horticultural crops.
2.	Develop crop-specific soil and irrigation assessment modules.
3.	Integrate live weather information through an API.
4.	Use reliable agricultural datasets for model development.
5.	Explore machine-learning-based crop and irrigation prediction.
6.	Add multilingual support, including Tamil.
7.	Introduce farmer-friendly recommendations and alerts.
8.	Conduct field testing with agricultural experts and farmers.
9.	Evaluate performance against validated agronomic baselines.
10.	Measure real-world water efficiency and crop outcomes.
12. Conclusion
AquaSmart AI has successfully progressed from project setup to a functional software MVP with four working decision-support workflows.
The application has been demonstrated using sample inputs, and its automated software test suite achieved 35 out of 35 passing tests.
The baseline comparison demonstrates the ability to compare two different rule-based irrigation screening approaches.
The current prototype provides a foundation for future development into a multi-crop agriculture decision-support platform. Further agronomic validation, reliable datasets, field testing, and machine-learning integration are necessary before its recommendations can be used as validated agricultural guidance.
Project Status: Functional rule-based MVP completed; field validation and advanced AI development remain future work.
Website:- https://sdhivyadharshini29-prog-aquasmart-ai-app-aquasmart-strea-5ol05m.streamlit.app/

