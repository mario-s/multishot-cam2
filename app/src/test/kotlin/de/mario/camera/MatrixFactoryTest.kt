package de.mario.camera

import android.util.Size
import org.hamcrest.CoreMatchers.notNullValue
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertThat
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito

/**
 *
 */
@RunWith(JUnitPlatform::class)
object MatrixFactoryTest : Spek({

    val size = Mockito.mock(Size::class.java)

    describe("the matrix factory") {

        beforeEachTest {
            given(size.width).willReturn(100)
            given(size.height).willReturn(100)
        }

        it("should return a matrix for a rotation of 90 degree") {
            val result = MatrixFactory.create(size, size, 90)
            assertThat(result, notNullValue())
        }

        it("should return a matrix for a rotation of 180 degree") {
            val result = MatrixFactory.create(size, size, 180)
            assertThat(result, notNullValue())
        }

        it("should return a matrix for a rotation of 270 degree") {
            val result = MatrixFactory.create(size, size, 270)
            assertThat(result, notNullValue())
        }
    }
})