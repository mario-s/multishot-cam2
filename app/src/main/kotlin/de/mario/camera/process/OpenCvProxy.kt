package de.mario.camera.process

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgcodecs.Imgcodecs
import java.io.File


class OpenCvProxy {

    fun mat(): Mat = Mat()

    fun scalar(v0: Double, v1: Double, v2: Double): Scalar = Scalar(v0, v1, v2)

    fun read(path: String ): Mat = Imgcodecs.imread(path)

    fun write(img: Mat, out: File) = Imgcodecs.imwrite(out.path, img)

    fun multiply(src: Mat, scalar: Scalar, dest: Mat): Mat {
        Core.multiply(src, scalar, dest)
        return dest
    }
}