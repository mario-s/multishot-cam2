package de.mario.camera

import android.app.AlertDialog
import android.app.Fragment
import android.content.Intent
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.camera2.*
import android.media.ImageReader
import android.media.MediaActionSound
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import de.mario.camera.SizeHelper.findLargestSize
import de.mario.camera.device.CameraDeviceProxy
import de.mario.camera.device.CameraLookup
import de.mario.camera.glue.CameraControlable
import de.mario.camera.glue.HdrProcessControlable
import de.mario.camera.glue.SettingsAccessable
import de.mario.camera.glue.ViewsMediatable
import de.mario.camera.io.ImageSaver
import de.mario.camera.orientation.ViewsOrientationListener
import de.mario.camera.process.HdrProcessController
import de.mario.camera.settings.SettingsAccess
import de.mario.camera.settings.SettingsActivity
import de.mario.camera.view.AutoFitTextureView
import de.mario.camera.view.ViewsMediator
import de.mario.camera.widget.ErrorDialog
import de.mario.camera.widget.Toaster
import java.io.File
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit


class CameraFragment : Fragment(), OnClickListener, CameraControlable, Captureable {

    private val sound = MediaActionSound()
    private val orientations = SurfaceOrientation()
    private val camState = CameraState()
    private val cameraDeviceProxy = CameraDeviceProxy(this)

    private val mCameraOpenCloseLock = Semaphore(1)
    private val fileNameStack = LinkedList<String>()

    private val toaster = Toaster(this)
    private val messageHandler = MessageHandler(this)
    private val cameraLookup = CameraLookup(this)
    private val previewSizeFactory = PreviewSizeFactory(this)
    private val permissionRequester = PermissionRequester(this)
    private val captureProgressCallback = CaptureProgressCallback(camState, this)
    private val mSurfaceTextureListener = TextureViewSurfaceListener(this)

    private lateinit var mTextureView: AutoFitTextureView
    private lateinit var mPreviewRequestBuilder: CaptureRequest.Builder
    private lateinit var mPreviewRequest: CaptureRequest
    private lateinit var settings: SettingsAccessable
    private lateinit var viewsMediator: ViewsMediatable
    private lateinit var mPreviewSize: Size
    private lateinit var hdrProcessController: HdrProcessControlable

    private var mBackgroundThread: HandlerThread? = null
    private var mBackgroundHandler: Handler? = null
    private var mImageReader: ImageReader? = null
    private var mCaptureSession: CameraCaptureSession? = null

    companion object {

        const val TAG = "CameraFragment"

        const val FRAGMENT_DIALOG = "dialog"

        private const val MAX_IMG = 3

        fun newInstance(): CameraFragment = CameraFragment()
    }

    override fun getMessageHandler(): Handler = messageHandler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mTextureView = view.findViewById<AutoFitTextureView>(R.id.texture)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sound.load(MediaActionSound.SHUTTER_CLICK)
        settings = SettingsAccess(activity)
        val viewsOrientationListener = ViewsOrientationListener(activity)
        viewsMediator = ViewsMediator(activity, settings, viewsOrientationListener)
        viewsMediator.setOnClickListener(this)
        hdrProcessController = HdrProcessController(activity)
    }

    override fun onResume() {
        super.onResume()

        viewsMediator.onResume()
        startBackgroundThread()

        if (mTextureView.isAvailable) {
            openCamera(mTextureView.width, mTextureView.height)
        } else {
            mTextureView.surfaceTextureListener = mSurfaceTextureListener
        }
    }

    override fun onPause() {
        closeCamera()
        stopBackgroundThread()
        viewsMediator.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        sound.release()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.picture -> takePicture()
            R.id.settings -> startSettings()
            R.id.info -> AlertDialog.Builder(activity!!)
                            .setMessage(R.string.intro_message)
                            .setPositiveButton(android.R.string.ok, null)
                            .show()
        }
    }

    /**
     * Sets up member variables related to camera.

     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    private fun setUpCameraOutputs(width: Int, height: Int) {
        try {
            cameraDeviceProxy.cameraId = cameraLookup.findCameraId()

            mPreviewSize = createPreviewSize(Size(width, height))

            // We fit the aspect ratio of TextureView to the size of preview we picked.
            val orientation = resources.configuration.orientation
            mTextureView.setAspectRatio(mPreviewSize, orientation)
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

    private fun createPreviewSize(origin: Size): Size {
        val characteristics = cameraDeviceProxy.getCameraCharacteristics()
        setupImageReader(findLargestSize(characteristics))

        return previewSizeFactory.createPreviewSize(characteristics, origin)
    }

    private fun setupImageReader(largest: Size) {
        mImageReader = ImageReader.newInstance(largest.width, largest.height,
                ImageFormat.JPEG, MAX_IMG)
        mImageReader?.setOnImageAvailableListener(
                mOnImageAvailableListener, mBackgroundHandler)
    }

    /**
     * Opens the camera specified by [CameraDeviceProxy.cameraId].
     */
    override fun openCamera(width: Int, height: Int) {
        if (permissionRequester.hasPermissions()) {
            setUpCameraOutputs(width, height)
            updateTransform(width, height)
            try {
                if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                    throw IllegalStateException("Time out waiting to lock camera opening.")
                }
                cameraDeviceProxy.openCamera(mStateCallback, mBackgroundHandler!!)
            } catch (e: CameraAccessException) {
                Log.w(TAG, e.message, e)
            } catch (e: InterruptedException) {
                throw IllegalStateException("Interrupted while trying to lock camera opening.", e)
            }
        }
    }

    private fun closeCamera() {
        try {
            mCameraOpenCloseLock.acquire()
            mCaptureSession?.close()
            mCaptureSession = null
            cameraDeviceProxy.close()
            mImageReader?.close()
            mImageReader = null
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
            mCameraOpenCloseLock.release()
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
            val texture = mTextureView.surfaceTexture

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.width, mPreviewSize.height)

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
                            mCaptureSession = cameraCaptureSession
                            try {
                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build()
                                mCaptureSession?.setRepeatingRequest(mPreviewRequest,
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

    override fun updateTransform(viewWidth: Int, viewHeight: Int) = mTextureView.setTransform(createMatrix(viewWidth, viewHeight))

    internal fun appendSavedFile(name: String) {
        fileNameStack.push(name)
        val size = fileNameStack.size
        if(size >= MAX_IMG) {
            process()
            val folder = File(name).parent
            showToast(getString(R.string.photos_saved)?.format(size, folder))
        }
    }

    private fun process() {
        val names = fileNameStack.toTypedArray()
        MediaScannerConnection.scanFile(activity, names, null, null)
        if(settings.isEnabled(R.string.hdr)) {
            hdrProcessController.process(names)
        }
    }

    private fun createMatrix(viewWidth: Int, viewHeight: Int): Matrix {
        val viewSize = Size(viewWidth, viewHeight)
        return MatrixFactory.create(mPreviewSize, viewSize, displayRotation())
    }

    private fun displayRotation(): Int = activity.windowManager.defaultDisplay.rotation

    private fun startSettings() = startActivity(Intent(activity, SettingsActivity::class.java))

    /**
     * Initiate a still image capture.
     * Lock the focus as the first step for a still image capture.
     */
    private fun takePicture() {
        prepareSession({
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START)
            // Tell #captureProgressCallback to wait for the lock.
            camState.currentState = CameraState.STATE_WAITING_LOCK
        })
    }

    /**
     * This method should be called when
     * we get a response in [.captureProgressCallback] from [.lockFocus].
     */
    override fun prepareCapturing() {
        prepareSession({
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START)
            // Tell #captureProgressCallback to wait for the precapture sequence to be set.
            camState.currentState = CameraState.STATE_WAITING_PRECAPTURE
        })
    }

    private fun prepareSession(before: () -> Unit) {
        try {
            before()
            mCaptureSession?.capture(mPreviewRequestBuilder.build(), captureProgressCallback,
                mBackgroundHandler!!)
        } catch (e: CameraAccessException) {
            Log.w(TAG, e.message, e)
        }
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     * {@link #captureProgressCallback} from both {@link #lockFocus()}.
     */
    override fun capturePicture() {
        try {
            fileNameStack.clear()
            val requests = cameraDeviceProxy.createBurstRequests(orientations.get(displayRotation()), mImageReader!!.surface)
            mCaptureSession?.stopRepeating()
            playShutterSound()
            mCaptureSession?.captureBurst(requests, captureImageCallback, null)
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
            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL)
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON)

            // After this, the camera will go back to the normal state of preview.
            camState.currentState = CameraState.STATE_PREVIEW
            mCaptureSession?.setRepeatingRequest(mPreviewRequest, captureProgressCallback,
                    mBackgroundHandler)
        }
    }

    /**
     * [CameraDevice.StateCallback] is called when [CameraDevice] changes its state.
     */
    private val mStateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(cameraDevice: CameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release()
            cameraDeviceProxy.cameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            releaseCamera(cameraDevice)
            activity.finish()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) = releaseCamera(cameraDevice)

        private fun releaseCamera(cameraDevice: CameraDevice) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            cameraDeviceProxy.close()
        }
    }
}
