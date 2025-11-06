package ru.dbudyak.entangler.models

import ru.dbudyak.entangler.Data

/**
 * Base class for all quantum circuit elements.
 *
 * Kotlin's `var` properties automatically generate Java get/set methods.
 */
class BaseElement(var elementType: ElementType) {
    // Note: `in` is a Kotlin keyword, so we use backticks
    // Java will access this as getIn()/setIn()
    var `in`: Data? = null
    var out: Data? = null

    enum class ElementType {
        MIRROR, BS, SOURCE, DETECTOR, PHASE_SHIFTER, WAVEGUIDE
    }

    companion object {
        @JvmField
        val OUT = 1
        @JvmField
        val IN = 0
        @JvmField
        val NONE = -1
    }
}
