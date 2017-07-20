package de.mario.camera.orientation


import android.content.Context
import android.view.View
import de.mario.camera.ConfirmationDialog
import de.mario.camera.view.AutoFitTextureView
import org.hamcrest.CoreMatchers.notNullValue
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertThat
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import java.math.BigDecimal
import de.mario.camera.orientation.OrientationNoiseFilter
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.number.IsCloseTo


/**
 */
@RunWith(JUnitPlatform::class)
object AbstractOrientationListenerTest : Spek({
    class TestInstance(context: Context) : AbstractOrientationListener(context) {
        override fun orientationChanged(orientation: Int) {
        }
    }

    val context = mock(Context::class.java)
    val instance = TestInstance(context)

    describe("the orientation listener") {

        it("should return 0 for 315 from orientationInDeg") {
            val result = instance.orientationInDeg(315)
            assertThat(result, equalTo(0))
        }

        it("should return 0 for 44 from orientationInDeg") {
            val result = instance.orientationInDeg(44)
            assertThat(result, equalTo(0))
        }

        it("should return 90 for 45 from orientationInDeg") {
            val result = instance.orientationInDeg(45)
            assertThat(result, equalTo(90))
        }

        it("should return 90 for 134 from orientationInDeg") {
            val result = instance.orientationInDeg(134)
            assertThat(result, equalTo(90))
        }

        it("should return 180 for 135 from orientationInDeg") {
            val result = instance.orientationInDeg(135)
            assertThat(result, equalTo(180))
        }

        it("should return 180 for 224 from orientationInDeg") {
            val result = instance.orientationInDeg(224)
            assertThat(result, equalTo(180))
        }

        it("should return 270 for 225 from orientationInDeg") {
            val result = instance.orientationInDeg(225)
            assertThat(result, equalTo(270))
        }

        it("should return 270 for 314 from orientationInDeg") {
            val result = instance.orientationInDeg(225)
            assertThat(result, equalTo(270))
        }

    }
})