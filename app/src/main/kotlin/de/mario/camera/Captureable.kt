package de.mario.camera

interface Captureable {

    /**
     * Capture a still picture.
     */
    fun capturePicture()

    /**
     * Run the prepare sequence for capturing a still image.
     */
    fun prepareCapturing()
}