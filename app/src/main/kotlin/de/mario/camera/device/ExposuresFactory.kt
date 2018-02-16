package de.mario.camera.device

import android.hardware.camera2.CameraCharacteristics
import android.util.Range


internal class ExposuresFactory(private val managerSupply: CameraManagerSupply) {

    fun exposures(cameraId: String, seqType: Int): Array<Int> {
        val range = range(cameraId)

        when (seqType) {
            1 -> return minToMaxIn2El(range)
            else -> return arrayOf(range.lower, 0, range.upper)
        }
    }

    private fun range(cameraId: String): Range<Int> {
        val chars = managerSupply.cameraCharacteristics(cameraId)
        return chars.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE)
    }

    private fun minToMaxIn2El(range: Range<Int>): Array<Int> {
        val values = mutableListOf<Int>()
        for(i in range.lower .. range.upper step 2) {
            values.add(i)
        }

        return values.toTypedArray()
    }
}