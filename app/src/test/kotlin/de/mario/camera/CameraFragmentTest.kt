package de.mario.camera


import android.app.Activity
import android.content.Intent
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.media.MediaActionSound
import android.os.Bundle
import android.os.Message
import android.util.Size
import android.view.Display
import android.view.View
import android.view.WindowManager
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.willReturn
import de.mario.camera.glue.ViewsMediatable
import de.mario.camera.message.BroadcastingReceiverRegister
import de.mario.camera.process.FileNameListCallback
import de.mario.camera.settings.SettingsLauncher
import de.mario.camera.view.AutoFitTextureView
import org.hamcrest.CoreMatchers.notNullValue
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertThat
import org.junit.platform.runner.JUnitPlatform
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import org.mockito.stubbing.Answer
import org.springframework.test.util.ReflectionTestUtils
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

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
            reset(view, activity)
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
            given(instance.context).willReturn(activity)
            given(activity.findViewById<View>(anyInt())).willReturn(view)

            instance.onActivityCreated(null)
        }

        it("onResume should toogle views") {
            val instance = spy(CameraFragment())
            val textureView: AutoFitTextureView = mock()
            val viewsMediator: ViewsMediatable = mock()
            val broadcastingReceiverRegister: BroadcastingReceiverRegister = mock()

            ReflectionTestUtils.setField(instance, "textureView", textureView)
            ReflectionTestUtils.setField(instance, "viewsMediator", viewsMediator)
            ReflectionTestUtils.setField(instance, "broadcastingReceiverRegister", broadcastingReceiverRegister)

            given(instance.activity).willReturn(activity)
            given(instance.view).willReturn(view)

            instance.onResume()

            verify(viewsMediator, atLeastOnce()).onResume()
        }

        it("onDestroy should free resources") {
            val instance = spy(CameraFragment())
            val sound: MediaActionSound = mock()
            val callback: FileNameListCallback = mock()
            ReflectionTestUtils.setField(instance, "sound", sound)
            ReflectionTestUtils.setField(instance, "listCallback", callback)

            instance.onDestroy()
            verify(sound).release()
            verify(callback).stop()
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
            doAnswer(Answer<Unit> {}).`when`(instance).startSettings()

            instance.onClick(view)
            verify(instance).startSettings()
        }

        it("onOpenCamera should open the camera device") {
            val instance = spy(CameraFragment())
            val windowManager: WindowManager = mock()
            val display: Display = mock()
            val cameraOpenCloseLock: Semaphore = mock()
            val permissionRequester: PermissionRequester = mock()
            val textureView: AutoFitTextureView = mock()
            val previewSize: Size = mock()

            given(permissionRequester.hasPermissions()).willReturn(true)
            given(instance.getString(anyInt())).willReturn("foo")
            given(instance.activity).willReturn(activity)
            given(activity.windowManager).willReturn(windowManager)
            given(windowManager.defaultDisplay).willReturn(display)
            given(cameraOpenCloseLock.tryAcquire(CameraFragment.TIMEOUT, TimeUnit.MILLISECONDS)).willReturn(true)

            ReflectionTestUtils.setField(instance, "permissionRequester", permissionRequester)
            ReflectionTestUtils.setField(instance, "textureView", textureView)
            ReflectionTestUtils.setField(instance, "previewSize", previewSize)
            ReflectionTestUtils.setField(instance, "cameraOpenCloseLock", cameraOpenCloseLock)

            instance.openCamera(1920,1080)
            verify(cameraOpenCloseLock).release()
        }

        it("appendSavedFile should append files and trigger callback") {
            val instance = CameraFragment.newInstance()
            @Suppress("UNCHECKED_CAST")
            val observable = ReflectionTestUtils.getField(instance, "fileNames") as ObservableArrayList<String>
            val callback: ObservableList.OnListChangedCallback<ObservableArrayList<String>> = mock()
            observable.addOnListChangedCallback(callback)

            instance.appendSavedFile("foo")
            verify(callback).onItemRangeInserted(observable, 0, 1)
        }
    }
})