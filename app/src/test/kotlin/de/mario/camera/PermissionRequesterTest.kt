package de.mario.camera

import android.app.Activity
import android.app.Fragment
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
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
object PermissionRequesterTest : Spek({
    val ID = "foo"

    describe("the camera handler") {

        val fragment = mock(Fragment::class.java)
        val activity = mock(Activity::class.java)
        val classUnderTest = PermissionRequester(fragment)

        beforeEachTest {
            given(fragment.activity).willReturn(activity)
        }

        it("should request needed permissions") {
            val result = classUnderTest.hasPermissions()
            assertThat(result, `is`(true))
            verify(fragment, never()).childFragmentManager
        }

        it("forwards permission results to the fragment") {
            val code = 0
            val permissions = arrayOf<String>()
            val results = IntArray(0)
            classUnderTest.onRequestPermissionsResult(code, permissions, results)
            verify(fragment).onRequestPermissionsResult(code, permissions, results)
        }

    }
})
