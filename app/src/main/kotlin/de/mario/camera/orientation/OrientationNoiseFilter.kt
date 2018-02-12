package de.mario.camera.orientation

/**
 */

import java.lang.Math.*

/**
 * Noise reduction for the values of the device orientation.<br></br>
 * It uses a low pass filter.
 */
class OrientationNoiseFilter {

    private var alpha: Double = 0.0

    private var timestamp: Float = 0.0f

    private val timestampOld: Float

    private var count: Int = 0

    private var values: IntArray = IntArray(0)

    init {
        timestamp = System.nanoTime().toFloat()
        timestampOld = System.nanoTime().toFloat()
    }

    /**
     * Filters noise out of the orientation values.
     * @param input values in degree
     * *
     * @return result in degree.
     */
    fun filter(input: Int): Int {
        //there is no need to filter if we have only one
        if (values.isEmpty()) {
            values = intArrayOf(0, input)
            return input
        }

        //filter based on last element from array and input
        val filtered = filter(values[1], input)
        //new array based on previous result and filter
        values = intArrayOf(values[1], filtered)

        return filtered
    }

    private fun filter(previous: Int, current: Int): Int {
        calculateAlpha()
        //convert to radians
        val radPrev = toRadians(previous.toDouble())
        val radCurrent = toRadians(current.toDouble())
        //filter based on sin & cos
        val sumSin = filter(sin(radPrev), sin(radCurrent))
        val sumCos = filter(cos(radPrev), cos(radCurrent))
        //calculate result angle
        val radRes = atan2(sumSin, sumCos)
        //convert radians to degree, round it and normalize (modulo of 360)
        val round = round(toDegrees(radRes))
        return ((MAX + round) % MAX).toInt()
    }

    private fun calculateAlpha() {
        timestamp = System.nanoTime().toFloat()
        // Find the sample period (between updates).
        // Convert from nanoseconds to seconds
        val diff = timestamp - timestampOld
        val dt = (1 / (count / (diff / FAC))).toDouble()
        count++
        // Calculate alpha
        alpha = dt / (TIME_CONSTANT + dt)
    }

    private fun filter(previous: Double, current: Double): Double {
        return previous + alpha * (current - previous)
    }

    companion object {

        private val TIME_CONSTANT = .297f

        internal val MAX = 360

        private val FAC = 1000000000.0f
    }
}