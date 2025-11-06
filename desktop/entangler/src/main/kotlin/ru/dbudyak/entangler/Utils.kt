package ru.dbudyak.entangler

import javafx.scene.image.Image

/**
 * Utility functions for the quantum circuit simulator.
 */
object Utils {
    /**
     * Load an image resource by element ID.
     * @param id Element ID (e.g., "elBS", "wGuide")
     * @return Image resource or null if not found
     */
    fun getImageByElId(id: String): Image? {
        val resourceName = when {
            id.startsWith("el") -> id.substring(2).lowercase() + ".png"
            id.startsWith("w") -> id.substring(1).lowercase() + ".png"
            else -> return null
        }

        return javaClass.classLoader.getResourceAsStream(resourceName)?.let { Image(it) }
    }

    /**
     * Print a single object to console.
     */
    @JvmStatic
    fun print(s: Any?) {
        println(s)
    }

    /**
     * Print two objects to console with separator.
     */
    @JvmStatic
    fun print(s: Any?, t: Any?) {
        println("$s : $t")
    }

    /**
     * Print a 2D array of doubles in matrix format.
     */
    @JvmStatic
    fun printData(data: Array<DoubleArray>) {
        data.forEach { row ->
            row.forEach { value ->
                print("$value ")
            }
            println()
        }
    }
}
