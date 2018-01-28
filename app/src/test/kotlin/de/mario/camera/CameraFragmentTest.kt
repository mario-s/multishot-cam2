package de.mario.camera


import android.app.Activity
import android.content.Intent
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.media.MediaActionSound
import android.view.View
import com.nhaarman.mockito_kotlin.mock
import de.mario.camera.glue.HdrProcessControlable
import de.mario.camera.glue.SettingsAccessable
import de.mario.camera.glue.ViewsMediatable
import de.mario.camera.message.BroadcastingReceiverRegister
import de.mario.camera.view.AutoFitTextureView
import de.mario.camera.widget.Toaster
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

        val view = mock(View::class.java)
        val activity = mock(Activity::class.java)
        val tmp = TemporaryFolder()

        beforeEachTest {
            tmp.create()
        }

        afterEachTest {
            tmp.delete()
            reset(view)
        }

        it("should have a factory method to create the fragment") {
            val instance = CameraFragment.newInstance()
            assertThat(instance, notNullValue())
        }

        it("onViewCreated should request texture view") {
            val instance = CameraFragment.newInstance()
            val other: View = mock()
            val textureView: AutoFitTextureView = mock()
            given(view.findViewById<View>(anyInt())).willReturn(other)
            given(view.findViewById<View>(R.id.texture)).willReturn(textureView)

            instance.onViewCreated(view, null)
            verify(view).findViewById<View>(anyInt())
        }

        it("onActivityCreated should complete without error") {
            val instance = spy(CameraFragment())

            given(instance.activity).willReturn(activity)
            given(activity.findViewById<View>(anyInt())).willReturn(view)

            instance.onActivityCreated(null)
        }

        it("onResume should toogle views") {
            val instance = spy(CameraFragment())
            val textureView: AutoFitTextureView = mock()
            val viewsMediator: ViewsMediatable = mock()
            val broadcastingReceiverRegister: BroadcastingReceiverRegister = mock()

            ReflectionTestUtils.setField(instance, "mTextureView", textureView)
            ReflectionTestUtils.setField(instance, "viewsMediator", viewsMediator)
            ReflectionTestUtils.setField(instance, "broadcastingReceiverRegister", broadcastingReceiverRegister)

            given(instance.activity).willReturn(activity)
            given(instance.view).willReturn(view)

            instance.onResume()

            verify(viewsMediator, atLeastOnce()).onResume()
        }

        it("onDestroy should release sound") {
            val instance = spy(CameraFragment())
            val sound: MediaActionSound = mock()
            ReflectionTestUtils.setField(instance, "sound", sound)

            instance.onDestroy()
            verify(sound).release()
        }

        it("prepareCapturing should trigger the camera") {
            val instance = spy(CameraFragment())
            given(view.id).willReturn(R.id.picture)
            var builder: CaptureRequest.Builder = mock()
            ReflectionTestUtils.setField(instance, "mPreviewRequestBuilder", builder)

            instance.prepareCapturing()
            verify(builder).set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START)
        }

        it("onClick should take picture") {
            val instance = spy(CameraFragment())
            given(view.id).willReturn(R.id.picture)
            var builder: CaptureRequest.Builder = mock()
            ReflectionTestUtils.setField(instance, "mPreviewRequestBuilder", builder)

            instance.onClick(view)
            verify(builder).set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START)
        }

        it("onClick should start settings") {
            val instance = spy(CameraFragment())
            given(view.id).willReturn(R.id.settings)

            instance.onClick(view)
            verify(instance).startActivity(any(Intent::class.java))
        }

        it("appendSavedFile should append files and show message") {
            val instance = spy(CameraFragment())
            val settings: SettingsAccessable = mock()
            val hdrProcessController: HdrProcessControlable = mock()
            val toaster: Toaster = mock()
            ReflectionTestUtils.setField(instance, "settings", settings)
            ReflectionTestUtils.setField(instance, "hdrProcessController", hdrProcessController)
            ReflectionTestUtils.setField(instance, "toaster", toaster)
            given(settings.isEnabled(R.string.hdr)).willReturn(true)
            given(instance.getString(anyInt())).willReturn("%s %s")

            val names = arrayOf("foo", "bar", "baz")
            names.forEach {instance.appendSavedFile(it)}
            verify(instance).showToast(anyString())
        }
    }
})