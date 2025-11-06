package ru.dbudyak.entangler

/**
 * Represents one side of a quantum element (left, top, right, bottom).
 * Each side has a direction (INPUT, OUTPUT, or NONE) and connection status.
 */
class Side {
    var isConnected: Boolean = false
    var direction: Direction = Direction.NONE

    enum class Direction {
        INPUT, OUTPUT, NONE
    }
}
