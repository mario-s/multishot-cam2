package de.mario.camera


import android.app.Activity
import android.view.View
import de.mario.camera.glue.SettingsAccessable
import de.mario.camera.glue.ViewsOrientationListenable
import de.mario.camera.view.AbstractPaintView
import de.mario.camera.view.AutoFitTextureView
import org.hamcrest.CoreMatchers.notNullValue
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertThat
import org.junit.platform.runner.JUnitPlatform
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import org.springframework.test.util.ReflectionTestUtils

/**
 */
@RunWith(JUnitPlatform::class)
object CameraFragmentTest : Spek({

    describe("the camera fragment") {

        val tmp = TemporaryFolder()

        beforeEachTest {
            tmp.create()
        }

        afterEachTest {
            tmp.delete()
        }

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
            verify(view, times(4)).findViewById(anyInt())
        }

        it("onActivityCreated should complete without error") {
            val instance = spy(CameraFragment())
            val activity = mock(Activity::class.java)

            given(instance.activity).willReturn(activity)

            instance.onActivityCreated(null)
        }

        it("onResume should toogle views") {
            val instance = spy(CameraFragment())
            val paintView = mock(AbstractPaintView::class.java)
            val view = mock(View::class.java)
            val textureView = mock(AutoFitTextureView::class.java)
            val settings = mock(SettingsAccessable::class.java)
            val viewsOrientationListener = mock(ViewsOrientationListenable::class.java)
            val activity = mock(Activity::class.java)

            ReflectionTestUtils.setField(instance, "mTextureView", textureView)
            ReflectionTestUtils.setField(instance, "settings", settings)
            ReflectionTestUtils.setField(instance, "viewsOrientationListener", viewsOrientationListener)

            given(instance.activity).willReturn(activity)
            given(instance.view).willReturn(view)
            given(activity.findViewById(anyInt())).willReturn(view)
            given(view.findViewById(anyInt())).willReturn(paintView)

            instance.onResume()

            val order = inOrder(view, settings)
            order.verify(view, atLeastOnce()).findViewById(anyInt())
            order.verify(settings, atLeastOnce()).isEnabled(anyInt())
        }

    }
})