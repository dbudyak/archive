# Kotlin Migration Guide

This document tracks the ongoing migration from Java to Kotlin for the Entangler quantum circuit simulator.

## Branch

The migration is happening on the `kotlin-rewrite` branch. This allows:
- Gradual, incremental conversion
- Master branch remains stable
- Testing at each step
- Safe experimentation

## Current Status

### ✅ Completed (Phase 1: Data Models)

**Files converted to Kotlin:**
- ✅ `Data.kt` - Quantum data channels (52 lines, was 80 in Java, **35% reduction**)
- ✅ `Side.kt` - Element side directions (17 lines, was 30 in Java, **43% reduction**)
- ✅ `BaseElement.kt` - Base class for all elements (29 lines, was 51 in Java, **43% reduction**)

**Overall code reduction: 40%** for converted files

**Build configuration:**
- ✅ Kotlin plugin configured (version 1.9.22)
- ✅ Java 17 / Kotlin JVM target 17
- ✅ Java-Kotlin interop working perfectly
- ✅ All tests passing
- ✅ App runs successfully

### 🔄 Remaining (Phase 2-4)

**Phase 2: Utility Classes**
- ⏳ `Utils.java` → `Utils.kt`
- ⏳ `PropertiesWorker.java` → `PropertiesWorker.kt`
- ⏳ `GraphBuilder.java` → `GraphBuilder.kt`

**Phase 3: Core Logic**
- ⏳ `CircuitWorker.java` → `CircuitWorker.kt` (317 lines)
- ⏳ `QElement.java` → `QElement.kt` (813 lines - the big one!)

**Phase 4: Application Entry**
- ⏳ `Main.java` → `Main.kt`

## Why Kotlin?

### Benefits Achieved

1. **Null Safety** - No more NPE surprises
   ```kotlin
   var channel1: RealMatrix? = null  // Explicit nullability
   ```

2. **Concise Code** - ~40% less boilerplate
   ```kotlin
   // Kotlin (clean)
   data class Data(var channel1: RealMatrix? = null)

   // Java (verbose)
   public class Data {
       private RealMatrix channel1;
       public RealMatrix getChannel1() { return channel1; }
       public void setChannel1(RealMatrix c) { channel1 = c; }
   }
   ```

3. **Smart Casts** - Type-safe when expressions
4. **Extension Functions** - Add methods without inheritance
5. **Data Classes** - Auto equals/hashCode/toString
6. **Properties** - No more get/set boilerplate

### Java Interop

Kotlin is **100% interoperable** with Java:
- Java code can call Kotlin classes seamlessly
- Kotlin `var` properties generate Java getters/setters automatically
- Builder pattern preserved for Java compatibility
- Existing Java code continues to work unchanged

## Migration Strategy

### Hybrid Approach (Current)

1. **Keep both languages** during transition
2. **Convert bottom-up** - data models first, then logic, then UI
3. **Test after each conversion**
4. **Maintain full functionality** at every step

### Per-File Conversion Steps

1. Create Kotlin version in `src/main/kotlin/`
2. Ensure Java compatibility (getters/setters, builders)
3. Update import statements in Java files
4. Delete old Java file
5. Build and test
6. Commit

### Example Conversion

**Before (Java):**
```java
public class Side {
    private boolean isConnected = false;
    private Direction direction = Direction.NONE;

    public boolean isConnected() { return isConnected; }
    public void setConnected(boolean c) { isConnected = c; }
    // ... more boilerplate
}
```

**After (Kotlin):**
```kotlin
class Side {
    var isConnected: Boolean = false
    var direction: Direction = Direction.NONE

    enum class Direction { INPUT, OUTPUT, NONE }
}
```

## Testing Checklist

After converting each file:
- [ ] `./gradlew compileKotlin` - Kotlin compiles
- [ ] `./gradlew compileJava` - Java still compiles
- [ ] `./gradlew build` - Full build succeeds
- [ ] `./gradlew run` - App launches
- [ ] Test basic circuit creation
- [ ] Test computation
- [ ] Verify results match expected values

## Next Steps

**Immediate:**
1. Continue with Phase 2 utility classes
2. Convert `Utils.java` to `Utils.kt`
3. Test thoroughly

**Future:**
1. Consider Kotlin coroutines for `CircuitWorker` async processing
2. Use Kotlin DSL for circuit building
3. Leverage sealed classes for element types
4. Add Kotlin-specific features (lazy, delegates, etc.)

## Resources

- [Kotlin docs](https://kotlinlang.org/docs/home.html)
- [Java-Kotlin interop](https://kotlinlang.org/docs/java-interop.html)
- [Migration guide](https://kotlinlang.org/docs/mixing-java-kotlin-intellij.html)

---

*Migration started: 2025-11-06*
*Current phase: Data Models ✅ Complete*
*Branch: `kotlin-rewrite`*
