
## 4. Updated Test Results

| Module | Tests Passed | Total Tests |
|---|---:|---:|
| Soil Analysis | 7 | 7 |
| Irrigation | 8 | 8 |
| Weather | 9 | 9 |
| Crop Screening | 9 | 9 |
| Total | 33 | 33 |

Overall software test pass rate: 100%

Testing command:

python -m pytest -v

All 33 automated tests passed, including
functional and boundary-value tests.

These results demonstrate software behaviour
for the tested inputs only. They do not establish
agricultural accuracy or field performance.


## 8. Baseline Comparison

### Objective
Compare the simple irrigation baseline with the
AquaSmart AI irrigation assessment module.

### Methods

Baseline:
- Uses soil moisture only.
- Produces two basic outcomes.

AquaSmart AI:
- Uses soil moisture.
- Considers recent rainfall.
- Accepts crop growth stage.
- Produces more detailed assessment outcomes.

### Test Case Results

| Test Case | Baseline Output | AquaSmart AI Output |
|---|---|---|
| 25% moisture, 0 mm rainfall, Tillering | Irrigation check required | Low soil moisture |
| 60% moisture, 25 mm rainfall, Flowering | No immediate irrigation check required | Recent rainfall is significant |

### Observation

AquaSmart AI considers rainfall in addition to
soil moisture, allowing it to produce a more
context-aware assessment than the simple baseline.

### Limitation

Both methods use illustrative rules.
No actual irrigation requirement, water savings,
or crop yield improvement has been measured.
Field validation is still required.

