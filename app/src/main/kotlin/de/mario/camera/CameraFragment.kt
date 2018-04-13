package de.mario.camera

import android.app.Fragment
import android.databinding.ObservableArrayList
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.camera2.*
import android.media.ImageReader
import android.media.MediaActionSound
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import de.mario.camera.device.CameraDeviceProxy
import de.mario.camera.device.CameraLookup
import de.mario.camera.device.PackageLookup
import de.mario.camera.glue.*
import de.mario.camera.io.ImageSaver
import de.mario.camera.message.BroadcastingReceiverRegister
import de.mario.camera.message.MessageHandler
import de.mario.camera.orientation.DeviceOrientationListener
import de.mario.camera.orientation.ViewsOrientationListener
import de.mario.camera.opencv.FileNameListCallback
import de.mario.camera.opencv.OpenCvAlert
import de.mario.camera.settings.SettingsAccess
import de.mario.camera.settings.SettingsLauncher
import de.mario.camera.view.AutoFitTextureView
import de.mario.camera.view.ViewsMediator
import de.mario.camera.widget.ErrorDialog
import de.mario.camera.widget.Toaster
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit


class CameraFragment : Fragment(), OnClickListener, CameraControlable, Captureable {

    private val sound = MediaActionSound()
    private val camState = CameraState()

    private val cameraOpenCloseLock = Semaphore(1)
    private val fileNames = ObservableArrayList<String>()

    private val toaster = Toaster(this)
    private val messageHandler = MessageHandler(this)
    private val cameraLookup = CameraLookup(this)
    private val cameraDeviceProxy = CameraDeviceProxy(this)
    private val mSurfaceTextureListener = TextureViewSurfaceListener(this)
    private val broadcastingReceiverRegister = BroadcastingReceiverRegister(this)

    private val settingsLauncher = SettingsLauncher(this, cameraDeviceProxy)
    private val previewSizeFactory = PreviewSizeFactory(this, cameraDeviceProxy)
    private val permissionRequester = PermissionRequester(this)
    private val captureProgressCallback = CaptureProgressCallback(camState, this)

    private lateinit var textureView: AutoFitTextureView
    private lateinit var mPreviewRequestBuilder: CaptureRequest.Builder
    private lateinit var mPreviewRequest: CaptureRequest
    private lateinit var settings: SettingsAccessable
    private lateinit var viewsMediator: ViewsMediatable
    private lateinit var previewSize: Size
    private lateinit var listCallback: FileNameListCallback
    private lateinit var deviceOrientationListener: DeviceOrientationListener

    private var mBackgroundThread: HandlerThread? = null
    private var mBackgroundHandler: Handler? = null
    private var mImageReader: ImageReader? = null
    private var captureSession: CameraCaptureSession? = null

    companion object {

        const val TAG = "CameraFragment"

        const val FRAGMENT_DIALOG = "dialog"

        fun newInstance(): CameraFragment = CameraFragment()

        internal const val TIMEOUT = 2500L
    }

    override fun getMessageHandler(): Handler = messageHandler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        textureView = view.findViewById<AutoFitTextureView>(R.id.texture)
    }

    private fun hasOpenCv() = PackageLookup(this).exists(PackageLookup.OPENCV)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sound.load(MediaActionSound.SHUTTER_CLICK)
        settings = SettingsAccess(activity)
        deviceOrientationListener = DeviceOrientationListener(activity)
        val viewsOrientationListener = ViewsOrientationListener(activity)
        viewsMediator = ViewsMediator(activity, settings, viewsOrientationListener)
        viewsMediator.setOnClickListener(this)
        listCallback = FileNameListCallback(this)
        fileNames.addOnListChangedCallback(listCallback)
    }

    override fun onResume() {
        super.onResume()

        deviceOrientationListener.enable()
        viewsMediator.onResume()
        broadcastingReceiverRegister.registerBroadcastReceiver(activity)
        startBackgroundThread()

        if (textureView.isAvailable) {
            openCamera(textureView.width, textureView.height)
        } else {
            textureView.surfaceTextureListener = mSurfaceTextureListener
        }

        if (hasOpenCv()) {
            view.findViewById<View>(R.id.info).visibility = View.GONE
        }
    }

    override fun onPause() {
        closeCamera()
        stopBackgroundThread()
        broadcastingReceiverRegister.unregisterBroadcastReceiver(activity)
        deviceOrientationListener.disable()
        viewsMediator.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        sound.release()
        listCallback.stop()
        fileNames.removeOnListChangedCallback(listCallback)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.picture -> takePicture()
            R.id.settings -> startSettings()
            R.id.info -> OpenCvAlert.show(activity)
        }
    }

    /**
     * Sets up member variables related to camera.

     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    private fun initCameraOutput(width: Int, height: Int) {
        try {
            cameraDeviceProxy.cameraId = cameraLookup.findCameraId()

            previewSize = createPreviewSize(Size(width, height))
            initImageReader()

            // We fit the aspect ratio of TextureView to the size of preview we picked.
            val orientation = resources.configuration.orientation
            textureView.setAspectRatio(previewSize, orientation)
        } catch (e: CameraAccessException) {
            Log.w(TAG, e.message, e)
        } catch (e: NullPointerException) {
            Log.w(TAG, e.message, e)
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            ErrorDialog.newInstance(getString(R.string.camera_error))
                    .show(childFragmentManager, FRAGMENT_DIALOG)
        }
    }

    private fun createPreviewSize(origin: Size): Size = previewSizeFactory.createPreviewSize(origin)

    private fun sizeForImageReader(): Size {
        val sizePrefs = settings.getString(getString(R.string.pictureSize))
        val size = SizeFilter.parse(sizePrefs)
        if(size != null) {
            return size
        }
        val resolutions = cameraDeviceProxy.imageSizes()
        if (!resolutions.isEmpty()){
            val index: Int = resolutions.size / 2
            return resolutions.get(index)
        }
        //if everything fails return the preview size
        return previewSize
    }

    private fun initImageReader() {
        val size = sizeForImageReader()
        mImageReader = ImageReader.newInstance(size.width, size.height,
                ImageFormat.JPEG, 2)
        mImageReader?.setOnImageAvailableListener(
                mOnImageAvailableListener, mBackgroundHandler)
    }

    /**
     * Opens the camera specified by [CameraDeviceProxy.cameraId].
     */
    override fun openCamera(width: Int, height: Int) {
        if (permissionRequester.hasPermissions()) {
            initCameraOutput(width, height)
            updateTransform(width, height)
            try {
                if (!cameraOpenCloseLock.tryAcquire(TIMEOUT, TimeUnit.MILLISECONDS)) {
                    throw IllegalStateException("Time out waiting to lock camera opening.")
                }
                if(mBackgroundHandler != null){
                    cameraDeviceProxy.openCamera(mStateCallback, mBackgroundHandler!!)
                }
            } catch (e: CameraAccessException) {
                Log.w(TAG, e.message, e)
            } catch (e: InterruptedException) {
                throw IllegalStateException("Interrupted while trying to lock camera opening.", e)
            } finally {
                cameraOpenCloseLock.release()
            }
        }
    }

    private fun closeCamera() {
        try {
            cameraOpenCloseLock.acquire()
            captureSession?.close()
            captureSession = null
            cameraDeviceProxy.close()
            mImageReader?.close()
            mImageReader = null
        } catch (e: InterruptedException) {
            Log.w(TAG, e.message, e)
        } finally {
            cameraOpenCloseLock.release()
        }
    }

    /**
     * Starts a background thread and its [Handler].
     */
    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread?.start()
        mBackgroundHandler = Handler(mBackgroundThread?.looper)
    }

    /**
     * Stops the background thread and its [Handler].
     */
    private fun stopBackgroundThread() {
        mBackgroundThread?.quitSafely()
        try {
            mBackgroundThread?.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            Log.w(TAG, e.message, e)
        }
    }

    //set up a CaptureRequest.Builder with the output Surface.
    private fun createPreviewRequestBuilder(surface: Surface): CaptureRequest.Builder {
        return cameraDeviceProxy.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW, surface)!!
    }

    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    private fun createCameraPreviewSession() {
        try {
            val texture = textureView.surfaceTexture

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(previewSize.width, previewSize.height)

            // This is the output Surface we need to start preview.
            val surface = Surface(texture)

            mPreviewRequestBuilder = createPreviewRequestBuilder(surface)

            // Here, we create a CameraCaptureSession for camera preview.
            val outputs = listOf(surface, mImageReader!!.surface)
            cameraDeviceProxy.createCaptureSession(outputs,
                    object : CameraCaptureSession.StateCallback() {

                        override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                            // The camera is already closed
                            if (cameraDeviceProxy.isClosed()) {
                                return
                            }

                            // When the session is ready, we start displaying the preview.
                            captureSession = cameraCaptureSession
                            try {
                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build()
                                captureSession?.setRepeatingRequest(mPreviewRequest,
                                        captureProgressCallback, mBackgroundHandler)
                            } catch (e: CameraAccessException) {
                                Log.w(TAG, e.message, e)
                            }
                        }

                        override fun onConfigureFailed(
                                 cameraCaptureSession: CameraCaptureSession) {
                            showToast("Failed")
                        }
                    }
            )
        } catch (e: CameraAccessException) {
            Log.w(TAG, e.message, e)
        }
    }

    override fun showToast(msg: String?) = toaster.showToast(msg)

    override fun updateTransform(viewWidth: Int, viewHeight: Int) = textureView.setTransform(createMatrix(viewWidth, viewHeight))

    override fun appendSavedFile(name: String) {
        fileNames.add(name)
    }

    private fun createMatrix(viewWidth: Int, viewHeight: Int): Matrix {
        val viewSize = Size(viewWidth, viewHeight)
        return MatrixFactory.create(previewSize, viewSize, deviceOrientationListener.displayRotation())
    }


    /**
     * Initiate a still image capture.
     * Lock the focus as the first step for a still image capture.
     */
    private fun takePicture() {
        prepareSession {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START)
            // Tell #captureProgressCallback to wait for the lock.
            camState.currentState = CameraState.STATE_WAITING_LOCK
        }
    }

    /**
     * This method should be called when
     * we get a response in [.captureProgressCallback] from [.lockFocus].
     */
    override fun prepareCapturing() {
        prepareSession {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START)
            // Tell #captureProgressCallback to wait for the precapture sequence to be set.
            camState.currentState = CameraState.STATE_WAITING_PRECAPTURE
        }
    }

    private fun prepareSession(before: () -> Unit) {
        try {
            before()
            captureSession?.capture(mPreviewRequestBuilder.build(), captureProgressCallback,
                mBackgroundHandler!!)
        } catch (e: CameraAccessException) {
            Log.w(TAG, e.message, e)
        }
    }

    override fun capturePicture() {
        try {
            fileNames.clear()
            val orientation = deviceOrientationListener.getOrientation()
            val requests = cameraDeviceProxy.createBurstRequests(orientation, mImageReader!!.surface)
            listCallback.requiredImages = requests.size
            captureSession?.stopRepeating()
            captureSession?.captureBurst(requests, captureImageCallback, null)
        } catch (e: CameraAccessException) {
            Log.w(TAG, e.message, e)
        }
    }

    private fun playShutterSound() {
        if(settings.isEnabled(R.string.shutter_sound)) {
            sound.play(MediaActionSound.SHUTTER_CLICK)
        }
    }

    private val mOnImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        mBackgroundHandler?.post(ImageSaver(this, reader))
    }

    private val captureImageCallback
            = object : CameraCaptureSession.CaptureCallback() {

        override fun onCaptureSequenceCompleted(session: CameraCaptureSession?, sequenceId: Int, frameNumber: Long) {
            playShutterSound()
            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL)
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON)

            // After this, the camera will go back to the normal state of preview.
            camState.currentState = CameraState.STATE_PREVIEW
            captureSession?.setRepeatingRequest(mPreviewRequest, captureProgressCallback,
                    mBackgroundHandler)
        }
    }

    internal fun startSettings() {
        mBackgroundHandler?.post({
            val msg = Message.obtain()
            msg.data.putString(getString(R.string.pictureSize), sizeForImageReader().toString())
            settingsLauncher.sendMessage(msg)
        })
    }

    /**
     * [CameraDevice.StateCallback] is called when [CameraDevice] changes its state.
     */
    private val mStateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(cameraDevice: CameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            cameraOpenCloseLock.release()
            cameraDeviceProxy.cameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            releaseCamera(cameraDevice)
            activity.finish()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) = releaseCamera(cameraDevice)

        private fun releaseCamera(cameraDevice: CameraDevice) {
            cameraOpenCloseLock.release()
            cameraDevice.close()
            cameraDeviceProxy.close()
        }
    }
}
