package de.mario.camera.process

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.File
import android.R.attr.src




class OpenCvProxy {

    fun scalar(v0: Double, v1: Double, v2: Double): Scalar = Scalar(v0, v1, v2)

    fun read(file: File): Mat = read(file.path)

    fun read(path: String): Mat = Imgcodecs.imread(path, Imgcodecs.CV_LOAD_IMAGE_UNCHANGED)

    fun write(img: Mat, out: File) {
        Imgcodecs.imwrite(out.path, img)
    }

    fun multiply(src: Mat, scalar: Scalar): Mat {
        val scalar = Scalar(255.0, 255.0, 255.0)
        val filter = Mat(src.size(), src.type(), scalar)
        val dest = src.mul(filter)
        return dest
    }
}