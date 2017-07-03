package de.mario.camera

import android.app.Fragment
import android.content.pm.PackageManager
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
object RequestPermissionCallbackTest : Spek({
    val ID = "foo"

    describe("the camera handler") {

        val fragment = mock(Fragment::class.java)
        val classUnderTest = RequestPermissionCallback(fragment)

        it("should request camera permissions") {
            classUnderTest.requestCameraPermission()
            verify(fragment, never()).childFragmentManager
        }

        it("forwards permission results to the fragment") {
            val code = 0
            val permissions = arrayOf<String>()
            val results = IntArray(0)
            classUnderTest.onRequestPermissionsResult(code, permissions, results)
            verify(fragment).onRequestPermissionsResult(code, permissions, results)
        }

        it("should show error dialog") {
            val code = classUnderTest.REQUEST_CAMERA_PERMISSION
            val permissions = arrayOf<String>()
            val results = IntArray(1)

            given(fragment.getString(R.string.request_permission)).willReturn(ID)

            classUnderTest.onRequestPermissionsResult(code, permissions, results)
            verify(fragment, never()).onRequestPermissionsResult(code, permissions, results)
        }

        it("should not show the error dialog when permission was granted") {
            val code = classUnderTest.REQUEST_CAMERA_PERMISSION
            val permissions = arrayOf<String>()
            val results = IntArray(PackageManager.PERMISSION_GRANTED)

            given(fragment.getString(R.string.request_permission)).willReturn(ID)

            classUnderTest.onRequestPermissionsResult(code, permissions, results)
            verify(fragment, never()).onRequestPermissionsResult(code, permissions, results)
        }
    }
})
