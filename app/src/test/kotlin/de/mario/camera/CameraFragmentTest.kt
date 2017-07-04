package de.mario.camera


import android.view.View
import org.hamcrest.CoreMatchers.notNullValue
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

import org.junit.Assert.assertThat
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*

/**
 */
@RunWith(JUnitPlatform::class)
object CameraFragmentTest : Spek({

    val ID = "foo"

    describe("the camera fragment") {

        it("should have a factory method to create the fragment") {
            val instance = CameraFragment.newInstance()
            assertThat(instance, notNullValue())
        }

        it("onViewCreated should request texture view") {
            val instance = CameraFragment.newInstance()
            val view = mock(View::class.java)
            val other = mock(View::class.java)
            val textureView = mock(AutoFitTextureView::class.java)
            given(view.findViewById(anyInt())).willReturn(other)
            given(view.findViewById(R.id.texture)).willReturn(textureView)

            instance.onViewCreated(view, null)
            verify(view, times(3)).findViewById(anyInt())
        }

    }
})