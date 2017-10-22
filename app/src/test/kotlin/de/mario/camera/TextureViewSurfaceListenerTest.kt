package de.mario.camera

import android.graphics.SurfaceTexture
import de.mario.camera.glue.CameraControllable
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(JUnitPlatform::class)
object TextureViewSurfaceListenerTest : Spek({

    describe("the surface listener") {

        val control = mock(CameraControllable::class.java)
        val texture = mock(SurfaceTexture::class.java)
        val classUnderTest = TextureViewSurfaceListener(control)

        it("should open the camera") {
            classUnderTest.onSurfaceTextureAvailable(texture, 0, 0)
            verify(control).openCamera(0, 0)
        }

        it("should update the transform") {
            classUnderTest.onSurfaceTextureSizeChanged(texture, 0, 0)
            verify(control).updateTransform(0, 0)
        }
    }
})