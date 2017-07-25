package de.mario.camera


import android.app.Activity
import android.view.View
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
import java.io.File

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

        it("onActivityCreated should add view to orientation listener") {
            val file = tmp.newFile("test")
            val instance = spy(CameraFragment())
            val activity = mock(Activity::class.java)
            val view = mock(View::class.java)

            given(instance.activity).willReturn(activity)
            given(activity.findViewById(anyInt())).willReturn(view)
            given(activity.getExternalFilesDir(null)).willReturn(file)

            instance.onActivityCreated(null)

            verify(activity, times(3)).findViewById(anyInt())
        }

    }
})