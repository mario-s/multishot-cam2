package de.mario.camera

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

/**
 */
@RunWith(JUnitPlatform::class)
object CameraHandlerTest : Spek({
    val ID = "foo"

    describe("the camera handler") {
        var cameraManager: CameraManager? = null

        var classUnderTest: CameraHandler? = null

        beforeEachTest {
            val fragment = mock(Fragment::class.java)
            val activity = mock(Activity::class.java)
            cameraManager = mock(CameraManager::class.java)
            classUnderTest = CameraHandler(fragment)

            given(fragment.activity).willReturn(activity)
            given(activity.getSystemService(Context.CAMERA_SERVICE)).willReturn(cameraManager!!)
        }

        it("should return an id for a camera") {
            val camCaracteristics = mock(CameraCharacteristics::class.java)
            given(cameraManager!!.cameraIdList).willReturn(arrayOf(ID))
            given(cameraManager!!.getCameraCharacteristics(ID)).willReturn(camCaracteristics)

            val camId = classUnderTest?.findCameraId()
            assertThat(camId, equalTo(ID))
        }
    }
})
