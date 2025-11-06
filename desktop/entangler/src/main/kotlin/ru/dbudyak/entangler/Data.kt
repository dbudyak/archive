package ru.dbudyak.entangler

import org.apache.commons.math3.linear.RealMatrix

/**
 * Represents quantum data flowing through circuit elements.
 *
 * Kotlin's data class provides automatic getters/setters for Java interop.
 */
data class Data(
    var channel1: RealMatrix? = null,
    var channel2: RealMatrix? = null
) {
    private var channel1Used: Boolean = false
    private var channel2Used: Boolean = false

    // Java compatibility methods (naming matches original Java code)
    fun isEmptyChannel1(): Boolean = channel1 == null
    fun isEmptyChannel2(): Boolean = channel2 == null

    fun isUsedChannel1(): Boolean = channel1Used
    fun isUsedChannel2(): Boolean = channel2Used

    fun setChannel1Used() {
        channel1Used = true
    }

    fun setChannel2isUsed() {
        channel2Used = true
    }

    /**
     * Java-compatible builder pattern
     */
    class DataBuilder {
        private var channel1: RealMatrix? = null
        private var channel2: RealMatrix? = null

        fun channel1(channel1: RealMatrix?): DataBuilder {
            this.channel1 = channel1
            return this
        }

        fun channel2(channel2: RealMatrix?): DataBuilder {
            this.channel2 = channel2
            return this
        }

        fun build(): Data = Data(channel1, channel2)
    }
}
