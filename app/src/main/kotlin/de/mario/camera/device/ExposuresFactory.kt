package de.mario.camera.device

import android.app.Fragment
import android.hardware.camera2.CameraCharacteristics
import de.mario.camera.glue.SettingsAccessable
import de.mario.camera.settings.SettingsAccess

internal class ExposuresFactory(private val fragment: Fragment) {

    private val managerSupply = CameraManagerSupply(fragment)

    private fun settings(): SettingsAccessable = SettingsAccess(fragment.context)

    fun exposureCompensations(cameraId: String): Array<Int> {
        val characteristics = managerSupply.cameraCharacteristics(cameraId)
        val range = characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE)

        return arrayOf(0, range.lower, range.upper)
    }
}