package de.mario.camera.orientation


import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertThat
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.math.BigDecimal
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.number.IsCloseTo


/**
 */
@RunWith(JUnitPlatform::class)
object OrientationNoiseFilterTest : Spek({

    describe("the noise filter") {

        it("should filter horizontal") {
            val instance = OrientationNoiseFilter()
            instance.filter(270)
            val result = instance.filter(275)
            assertThat(result, equalTo(0))
        }

        it("should filter vertical to left") {
            val instance = OrientationNoiseFilter()
            instance.filter(4)
            instance.filter(2)
            val result = instance.filter(359)
            assertThat(result.toDouble(), IsCloseTo(0.0, 1.0))
        }


        it("should filter vertical to right") {
            val instance = OrientationNoiseFilter()
            instance.filter(358)
            val result = instance.filter(6)
            val expected = BigDecimal(result % OrientationNoiseFilter.MAX)
            assertThat(result.toDouble(), IsCloseTo(expected.toDouble(), 1.0))
        }

    }
})