# Known Issues - Entangler Quantum Circuit Simulator

## Current Status (2025-11-06)

The simulator has been modernized and many critical bugs have been fixed, but some issues remain with the beam splitter and circuit graph construction.

## Fixed Issues ✅

1. **Build System Modernized**
   - Updated Gradle 4.8.1 → 8.10
   - Added JavaFX 21 support
   - Fixed all dependencies for modern Java

2. **UI Translation**
   - All Russian text translated to English
   - Fully internationalized interface

3. **Element Initialization**
   - Elements properly added to graph when dropped
   - Side directions (INPUT/OUTPUT) correctly initialized
   - Beam splitter θ default value set to π/4

4. **Detector Calculation**
   - Detectors now calculate probabilities as |amplitude|²
   - Shows actual numerical values instead of just "Complete"

5. **Detector Channel Assignment**
   - Fixed bug where second detector received wrong channel (was channel1, now channel2)

## Remaining Issues ❌

### 1. ~~Beam Splitter Normalization~~ ✅ FIXED (Commit 70cec52)

**Problem:** Detector shows probability > 1 (e.g., 1.9140)

**FIXED:** Added 1/√2 normalization factor. Detectors now show 0.0-1.0 ✓

**Current Behavior:**
```
Input:  |in1|² = 1.0, |in2|² = 1.0  (Total: 2.0)
Output: |out1|² = 1.913, |out2|² = 0.087  (Total: 2.0)
```

**Analysis:**
- Total probability IS conserved (2.0 → 2.0) ✓
- But individual output norms are not 1.0 ❌
- Current implementation: `out1[i] = cos(θ)*in1[i] + sin(θ)*in2[i]`

**Root Cause:**
The simulator is mixing two different quantum optics formalisms:
- **Sources** generate qubit states: `|ψ⟩ = cos(θ)|0⟩ + sin(θ)|1⟩` with `|ψ|² = 1`
- **Beam splitter** should transform mode operators, not individual qubit amplitudes

**Possible Solutions:**
1. Normalize each output: `out1 = (cos(θ)*in1 + sin(θ)*in2) / sqrt(2)`
2. Change detector to measure mode intensity, not state norm
3. Restructure to use proper Fock states or coherent states

**Location:** `QElement.java:461-505` (BS case in compute())

---

### 1b. ~~Properties Panel Accumulation~~ ✅ FIXED (Commit dbe6465)

**Problem:** Clicking different elements showed accumulated properties

**FIXED:** Added clearProperties() to clear UI before updating ✓

---

### 2. Graph Construction for Beam Splitter Outputs

**Problem:** Both BS outputs connect to the same detector

**Current Behavior:**
```
BS : 2 2
BS:2 2 -> WAVEGUIDE:3 2   <- Both outputs go here!
sources iterated
BS : 2 2
BS:2 2 -> WAVEGUIDE:3 2   <- Same waveguide again
sources iterated
DETECTOR : checking data for WAVEGUIDE -> DETECTOR : 4 2
DETECTOR : checking data for WAVEGUIDE -> DETECTOR : 4 2  <- Duplicate!
```

**Analysis:**
- BS has two output channels (channel1 and channel2) ✓
- BS should create edges to TWO different elements ❌
- Currently only ONE edge is created (to the RIGHT side)
- The BOTTOM output is never connected to its detector

**Root Cause:**
The `setIO()` method in `QElement.java:251-328` creates graph edges based on side connections, but it doesn't handle the special case of beam splitters having two SEPARATE outputs that need to go to DIFFERENT destinations.

Current logic:
```java
if (getElementRight() != null && getElementRight().getBase() != null) {
    if (getSideRight().isConnected()) {
        GraphBuilder.getInstance().addEdge(QElement.this, getElementRight());
    }
}
```

This creates at most ONE edge per side (right, bottom, etc.), but BS needs:
- One edge for channel1 output (e.g., RIGHT)
- One edge for channel2 output (e.g., BOTTOM)

**Required Fix:**
Need to extend the graph model to support:
- Multi-edges OR
- Edge metadata indicating which channel (channel1 vs channel2) OR
- Separate "output ports" concept in the graph

**Location:** `QElement.java:251-328` (setIO() method)

### 3. Circuit Traversal for Multiple BS Outputs

**Problem:** Simulation processes BS twice but only reaches one detector

**Current Behavior:**
The BS is added to `newStartedSources` (probably once), but when traversed, it creates edges to only one waveguide, so only one detector receives data.

**Analysis:**
The traversal in `CircuitWorker.java:172-246` needs to:
1. Recognize when an element has multiple output channels
2. Process each output channel separately
3. Route each channel to the correct connected element

**Current Logic:**
```java
for (QElement sources : startedSources) {
    for (QElement el : vertexes) {
        if (graph.containsEdge(current, el)) {
            // Process edge
        }
    }
}
```

This finds all edges from current element, but doesn't track which channel each edge corresponds to.

**Required Fix:**
- Add channel metadata to edges
- When processing BS in traversal, create separate paths for channel1 and channel2
- Ensure each channel routes to correct downstream element

**Location:** `CircuitWorker.java:172-246` (run() method)

## Testing Notes

**Test Circuit:** Hong-Ou-Mandel (HOM) Interferometer
```
Source1 → Waveguide → BS (input1)
Source2 → Waveguide → BS (input2)
BS (output1) → Waveguide → Detector1
BS (output2) → Waveguide → Detector2
```

**Expected Results:**
- Each detector shows probability between 0.0 and 1.0
- Sum of detector probabilities = 1.0
- Quantum interference visible in probability distribution

**Actual Results:**
- Detector1 shows 1.9140 (probability > 1)
- Detector2 not reached (shows nothing or old value)
- Only one detector receives data

## Recommendations for Future Work

### Short Term (Required for basic functionality):
1. **Fix normalization:** Decide on convention and normalize outputs appropriately
2. **Fix graph construction:** Add channel tracking to edges or implement multi-edges
3. **Fix traversal:** Route each BS output channel to correct detector

### Medium Term (Improvements):
1. Add graph visualization showing actual edges created
2. Add debug mode showing which channels are active
3. Validate circuit topology before simulation
4. Show warnings for disconnected elements

### Long Term (Enhancements):
1. Implement proper Fock state formalism for multi-photon states
2. Add phase shifters with proper phase gate operation
3. Implement quantum state tomography
4. Add measurement statistics (multiple runs)

## References

- Original implementation: 2014 Master's thesis project
- Quantum optics formalism: Beam splitter acts on mode operators
- Graph theory: Need hypergraph or labeled edges for multi-output devices

## Contact

For questions or to continue fixing these issues, refer to the commit history and this document.

Last updated: 2025-11-06
