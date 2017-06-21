package de.mario.camera

/**
 *
 */
class CameraState {

    companion object Available {
        /**
         * Camera state: Showing camera preview.
         */
        val STATE_PREVIEW = 0
        /**
         * Camera state: Waiting for the focus to be locked.
         */
        val STATE_WAITING_LOCK = 1
        /**
         * Camera state: Waiting for the exposure to be precapture state.
         */
        val STATE_WAITING_PRECAPTURE = 2
        /**
         * Camera state: Waiting for the exposure state to be something other than precapture.
         */
        val STATE_WAITING_NON_PRECAPTURE = 3
        /**
         * Camera state: Picture was taken.
         */
        val STATE_PICTURE_TAKEN = 4
    }

    /**
     * The current state of camera state for taking pictures.

     * @see .mCaptureCallback
     */
    var currentState = STATE_PREVIEW

}