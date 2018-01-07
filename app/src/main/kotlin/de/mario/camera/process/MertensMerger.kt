package de.mario.camera.process

import org.opencv.core.Mat
import org.opencv.photo.Photo


internal class MertensMerger : Merger {

    override fun merge(images: List<Mat>): Mat {
        val fusion = Mat()
        val mergeMertens = Photo.createMergeMertens()
        mergeMertens.process(images, fusion)
        return fusion
    }
}