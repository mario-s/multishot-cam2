package de.mario.camera

import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 */
class CameraFragmentTest {

    @Test
    fun testNewInstance() {
        val instance = CameraFragment.newInstance()
        assertNotNull(instance)
    }
}