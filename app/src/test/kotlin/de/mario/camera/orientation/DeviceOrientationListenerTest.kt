package de.mario.camera.orientation

import android.app.Activity
import com.nhaarman.mockito_kotlin.mock
import org.hamcrest.CoreMatchers.equalTo
import org.jetbrains.spek.api.Spek
import org.junit.Assert.assertThat
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 */
@RunWith(JUnitPlatform::class)
object DeviceOrientationListenerTest : Spek({

    describe("the device orientation listener") {

        val activity: Activity = mock()
        val instance = DeviceOrientationListener(activity)

        it("should return 0 for 90") {
            instance.onOrientationChanged(90)
            assertThat(instance.getOrientation(), equalTo(0))
        }

    }
})