# HOM (Hong-Ou-Mandel) Interferometer Test Scheme

## Circuit Layout

The HOM interferometer demonstrates quantum interference between two photons at a beam splitter.

### Grid Layout (x, y):

```
(0,1): SOURCE (top path)
(1,1): WAVEGUIDE
(2,1): BS (beam splitter at θ=π/4)
(3,1): WAVEGUIDE
(4,1): DETECTOR (D1)

(0,3): SOURCE (bottom path)
(1,3): WAVEGUIDE
(2,3): [connects to BS at (2,1)]
(3,3): WAVEGUIDE
(4,3): DETECTOR (D2)
```

### Visual Representation:

```
SOURCE → WG → ╲ → WG → D1
              BS
SOURCE → WG → ╱ → WG → D2
```

### Step-by-step construction:

1. **Top path:**
   - Place SOURCE at (0,1)
   - Place WAVEGUIDE at (1,1)
   - Place BS at (2,1)
   - Place WAVEGUIDE at (3,1)
   - Place DETECTOR at (4,1)

2. **Bottom path:**
   - Place SOURCE at (0,3)
   - Place WAVEGUIDE at (1,3)
   - Place WAVEGUIDE at (3,3)
   - Place DETECTOR at (4,3)

3. **Connections:**
   - Top source connects to top waveguide
   - Top waveguide connects to BS top input
   - Bottom source connects to bottom waveguide
   - Bottom waveguide connects to BS left input
   - BS right output connects to top detector path
   - BS bottom output connects to bottom detector path

### Expected Results:

For a 50:50 beam splitter (θ = π/4):
- When two identical photons arrive simultaneously at the BS, quantum interference causes them to bunch together
- Both photons exit through the same output port
- **D1 should show: ~0.5**
- **D2 should show: ~0.5**
- Total probability: 1.0

### Current Implementation Notes:

This is the basic HOM interferometer. In the quantum version with identical photons entering both inputs simultaneously, the photons should exhibit bunching behavior due to quantum interference.

For the current simulator implementation where we're using classical superposition states, we expect to see the photons split between the two detectors according to the beam splitter ratio.
