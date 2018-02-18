package de.mario.camera.device

import android.hardware.camera2.CameraCharacteristics
import android.util.Range
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
object SequenceFactoryTest : Spek({
    val ID = "foo"

    describe("the create factory") {
        val range: Range<Int> = mock()
        val characteristics: CameraCharacteristics = mock()
        var cameraManager: CameraManagerSupply = mock()
        var classUnderTest = SequenceFactory(cameraManager)


        beforeEachTest {
            given(range.lower).willReturn(-4)
            given(range.upper).willReturn(4)
            given(cameraManager.cameraCharacteristics(ID)).willReturn(characteristics)
            given(characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE)).willReturn(range)
        }

        on("create") {
            it("should return 3 values when seq type is zero") {
                val result = classUnderTest.create(ID, 0)
                result.size shouldBe 3
            }

            it("should return an array with upper and lower range when type is 1") {
                val result = classUnderTest.create(ID, 1)
                result shouldEqual arrayOf(-4, -2, 0, 2, 4)
            }
        }

    }
})