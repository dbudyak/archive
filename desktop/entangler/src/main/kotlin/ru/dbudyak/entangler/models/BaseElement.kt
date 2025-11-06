package ru.dbudyak.entangler.models

import ru.dbudyak.entangler.Data

/**
 * Base class for all quantum circuit elements.
 *
 * @property elementType Type of the element (SOURCE, DETECTOR, BS, etc.)
 */
class BaseElement(var elementType: ElementType) {
    var input: Data? = null
    var output: Data? = null

    enum class ElementType {
        MIRROR, BS, SOURCE, DETECTOR, PHASE_SHIFTER, WAVEGUIDE
    }

    companion object {
        const val OUT = 1
        const val IN = 0
        const val NONE = -1
    }
}
