package de.mario.camera

import android.app.AlertDialog
import android.app.Fragment
import android.content.Intent
import android.content.res.Configuration
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.camera2.*
import android.media.ImageReader
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
import de.mario.camera.glue.CameraControllable
import de.mario.camera.glue.SettingsAccessable
import de.mario.camera.glue.ViewsOrientationListenable
import de.mario.camera.io.ImageSaver
import de.mario.camera.message.MessageHandler
import de.mario.camera.orientation.ViewsOrientationListener
import de.mario.camera.settings.SettingsAccess
import de.mario.camera.settings.SettingsActivity
import de.mario.camera.view.AbstractPaintView
import de.mario.camera.view.AutoFitTextureView
import de.mario.camera.widget.ErrorDialog
import de.mario.camera.widget.Toaster
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit


open class CameraFragment : Fragment(), OnClickListener, CameraControllable, Captureable {

    private val orientations = SurfaceOrientation()
    private val camState = CameraState()

    private val mCameraOpenCloseLock = Semaphore(1)

    private val toaster = Toaster(this)
    private val messageHandler = MessageHandler(this)
    private val cameraHandler = CameraHandler(this)
    private val previewSizeFactory = PreviewSizeFactory(this)
    private val permissionRequester = PermissionRequester(this)
    private val mCaptureCallback = CaptureCallback(camState, this)
    private val mSurfaceTextureListener = TextureViewSurfaceListener(this)

    private lateinit var mTextureView: AutoFitTextureView
    private lateinit var mPreviewRequestBuilder: CaptureRequest.Builder
    private lateinit var mPreviewRequest: CaptureRequest
    private lateinit var settings: SettingsAccessable
    private lateinit var mPreviewSize: Size
    private lateinit var viewsOrientationListener: ViewsOrientationListenable

    private var mCameraId: String? = null
    private var mCameraDevice: CameraDevice? = null
    private var mBackgroundThread: HandlerThread? = null
    private var mBackgroundHandler: Handler? = null
    private var mImageReader: ImageReader? = null
    private var mCaptureSession: CameraCaptureSession? = null

    companion object {

        private val TAG = "CameraFragment"

        private val FRAGMENT_DIALOG = "dialog"

        //the ids of the buttons
        private val BUTTONS = arrayOf(R.id.picture, R.id.settings, R.id.info)

        fun newInstance(): CameraFragment {
            return CameraFragment()
        }
    }

    override fun getMessageHandler(): Handler {
        return messageHandler
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        BUTTONS.forEach { view.findViewById(it).setOnClickListener(this) }
        mTextureView = view.findViewById(R.id.texture) as AutoFitTextureView
    }

    private fun toggleViews(view: View) {
        fun findView(id: Int): AbstractPaintView = view.findViewById(id) as AbstractPaintView

        findView(R.id.grid).enable(settings.isEnabled(R.string.grid))
        findView(R.id.level).enable(settings.isEnabled(R.string.level))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewsOrientationListener = ViewsOrientationListener(activity)
        settings = SettingsAccess(activity)
    }

    private fun toggleOrientationListener(enable: Boolean) {
        if(enable) {
            BUTTONS.forEach {viewsOrientationListener.addView(activity.findViewById(it))}
            viewsOrientationListener.enable()
        } else {
            viewsOrientationListener.disable()
            BUTTONS.forEach {viewsOrientationListener.removeView(activity.findViewById(it))}
        }
    }

    override fun onResume() {
        super.onResume()

        toggleViews(view)
        toggleOrientationListener(true)
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
        toggleOrientationListener(false)
        super.onPause()
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
     * *
     * @param height The height of available size for camera preview
     */
    private fun setUpCameraOutputs(width: Int, height: Int) {
        try {
            mCameraId = cameraHandler.findCameraId()

            mPreviewSize = createPreviewSize(mCameraId!!, Size(width, height))

            // We fit the aspect ratio of TextureView to the size of preview we picked.
            val orientation = resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio(
                        mPreviewSize.width, mPreviewSize.height)
            } else {
                mTextureView.setAspectRatio(
                        mPreviewSize.height, mPreviewSize.width)
            }
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

    private fun createPreviewSize(cameraId: String, origin: Size): Size {
        val characteristics = cameraHandler.getCameraCharacteristics(cameraId)
        setupImageReader(findLargestSize(characteristics))

        return previewSizeFactory.createPreviewSize(characteristics, origin)
    }

    private fun setupImageReader(largest: Size) {
        mImageReader = ImageReader.newInstance(largest.width, largest.height,
                ImageFormat.JPEG, /*maxImages*/2)
        mImageReader?.setOnImageAvailableListener(
                mOnImageAvailableListener, mBackgroundHandler)
    }

    /**
     * Opens the camera specified by [CameraFragment.mCameraId].
     */
    override fun openCamera(width: Int, height: Int) {
        if (permissionRequester.hasPermissions()) {
            setUpCameraOutputs(width, height)
            updateTransform(width, height)
            try {
                if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                    throw IllegalStateException("Time out waiting to lock camera opening.")
                }
                cameraHandler.openCamera(mCameraId!!, mStateCallback, mBackgroundHandler!!)
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
            mCameraDevice?.close()
            mCameraDevice = null
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
        val builder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)!!
        builder.addTarget(surface)
        setAuto(builder)
        return builder
    }

    private fun setAuto(builder: CaptureRequest.Builder) {
        // Auto focus should be continuous for camera preview.
        builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
        // Flash is automatically enabled when necessary.
        builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
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
            mCameraDevice?.createCaptureSession(Arrays.asList(surface, mImageReader?.surface),
                    object : CameraCaptureSession.StateCallback() {

                        override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                            // The camera is already closed
                            if (null == mCameraDevice) {
                                return
                            }

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession
                            try {
                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build()
                                mCaptureSession?.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackgroundHandler)
                            } catch (e: CameraAccessException) {
                                Log.w(TAG, e.message, e)
                            }
                        }

                        override fun onConfigureFailed(
                                 cameraCaptureSession: CameraCaptureSession) {
                            showToast("Failed")
                        }
                    }, null
            )
        } catch (e: CameraAccessException) {
            Log.w(TAG, e.message, e)
        }
    }

    override fun showToast(msg: String) {
        toaster.showToast(msg)
    }

    override fun updateTransform(viewWidth: Int, viewHeight: Int) {
        mTextureView.setTransform(createMatrix(viewWidth, viewHeight))
    }

    private fun createMatrix(viewWidth: Int, viewHeight: Int): Matrix {
        val viewSize = Size(viewWidth, viewHeight)
        return MatrixFactory.create(mPreviewSize, viewSize, displayRotation())
    }

    private fun displayRotation(): Int = activity.windowManager.defaultDisplay.rotation

    /**
     * Initiate a still image capture.
     * Lock the focus as the first step for a still image capture.
     */
    private fun takePicture() {
        try {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START)
            // Tell #mCaptureCallback to wait for the lock.
            camState.currentState = CameraState.STATE_WAITING_LOCK
            mCaptureSession?.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler!!)
        } catch (e: CameraAccessException) {
            Log.w(TAG, e.message, e)
        }
    }

    private fun startSettings() = startActivity(Intent(activity, SettingsActivity::class.java))

    /**
     * This method should be called when
     * we get a response in [.mCaptureCallback] from [.lockFocus].
     */
    override fun prepareCapturing() {
        try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START)
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            camState.currentState = CameraState.STATE_WAITING_PRECAPTURE
            mCaptureSession?.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler)
        } catch (e: CameraAccessException) {
            Log.w(TAG, e.message, e)
        }
    }

      /**
     * Capture a still picture. This method should be called when we get a response in
     * {@link #mCaptureCallback} from both {@link #lockFocus()}.
     */
    override fun capturePicture() {
        try {
            // This is the CaptureRequest.Builder that we use to take a picture.
            val captureBuilder =
                    mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder?.addTarget(mImageReader?.surface)

            // Use the same AE and AF modes as the preview.
            setAuto(captureBuilder!!)

            // Orientation
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, orientations.get(displayRotation()))

            val captureCallback
                    = object : CameraCaptureSession.CaptureCallback() {

                override fun onCaptureCompleted( session: CameraCaptureSession,
                                                 request: CaptureRequest,
                                                 result: TotalCaptureResult) {
                    unlockFocus()
                }
            }

            mCaptureSession?.stopRepeating()
            mCaptureSession!!.capture(captureBuilder.build(), captureCallback, null)
        } catch (e: CameraAccessException) {
            Log.w(TAG, e.message, e)
        }
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private fun unlockFocus() {
        // Reset the auto-focus trigger
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                CameraMetadata.CONTROL_AF_TRIGGER_CANCEL)
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
        mCaptureSession?.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                mBackgroundHandler)
        // After this, the camera will go back to the normal state of preview.
        camState.currentState = CameraState.STATE_PREVIEW
        mCaptureSession?.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                mBackgroundHandler)
    }

    private val mOnImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        mBackgroundHandler?.post(ImageSaver(this, reader.acquireNextImage()))
    }

    /**
     * [CameraDevice.StateCallback] is called when [CameraDevice] changes its state.
     */
    private val mStateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(cameraDevice: CameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release()
            mCameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            releaseCamera(cameraDevice)
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            releaseCamera(cameraDevice)
            activity.finish()
        }

        private fun releaseCamera(cameraDevice: CameraDevice) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            mCameraDevice = null
        }
    }
}
