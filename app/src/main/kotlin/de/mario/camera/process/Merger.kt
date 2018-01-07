package de.mario.camera.process

import org.opencv.core.Mat


internal interface Merger {
    fun merge(images: List<Mat>): Mat
}