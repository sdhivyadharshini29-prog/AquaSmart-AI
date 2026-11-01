
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

## Test Execution Results

- Total automated tests: 35
- Tests passed: 35
- Tests failed: 0
- Pass rate: 100%
- Execution time: 0.08 seconds

All 35 automated software tests passed using pytest.

Note: This pass rate measures software test execution, not
agricultural accuracy or real-world field performance.

## Baseline Comparison

Two automated test cases compare the preliminary AquaSmart AI
irrigation screening with a simple soil-moisture-only baseline.

The comparison demonstrates differences in rule-based outputs.
It does not prove improved irrigation efficiency, water savings,
or crop yield.
