package ru.dbudyak.entangler

import org.apache.commons.math3.linear.RealMatrix

/**
 * Represents quantum data flowing through circuit elements.
 *
 * @property channel1 First quantum channel (nullable)
 * @property channel2 Second quantum channel (nullable)
 */
data class Data(
    var channel1: RealMatrix? = null,
    var channel2: RealMatrix? = null
) {
    var channel1Used: Boolean = false
        private set

    var channel2Used: Boolean = false
        private set

    val isEmptyChannel1: Boolean
        get() = channel1 == null

    val isEmptyChannel2: Boolean
        get() = channel2 == null

    fun setChannel1Used() {
        channel1Used = true
    }

    fun setChannel2Used() {
        channel2Used = true
    }
}
