package de.mario.camera.device

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import org.hamcrest.CoreMatchers.equalTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertThat
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock

/**
 */
@RunWith(JUnitPlatform::class)
object CameraLookupTest : Spek({
    val ID = "foo"

    describe("the camera handler") {
        var cameraManager: CameraManager? = null

        var classUnderTest: CameraLookup? = null

        beforeEachTest {
            val fragment = mock(Fragment::class.java)
            val activity = mock(Activity::class.java)
            cameraManager = mock(CameraManager::class.java)
            classUnderTest = CameraLookup(fragment)

            given(fragment.activity).willReturn(activity)
            given(activity.getSystemService(Context.CAMERA_SERVICE)).willReturn(cameraManager!!)
        }

        it("should return an id for a camera") {
            val caracteristics = mock(CameraCharacteristics::class.java)
            given(cameraManager!!.cameraIdList).willReturn(arrayOf(ID))
            given(cameraManager!!.getCameraCharacteristics(ID)).willReturn(caracteristics)

            val camId = classUnderTest?.findCameraId()
            assertThat(camId, equalTo(ID))
        }
    }
})
