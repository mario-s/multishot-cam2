package de.mario.camera.process

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.File
import android.R.attr.src




class OpenCvProxy {

    private companion object {
        val SCALAR = Scalar(255.0, 255.0, 255.0)
    }

    fun read(file: File): Mat = read(file.path)

    fun read(path: String): Mat = Imgcodecs.imread(path, Imgcodecs.CV_LOAD_IMAGE_UNCHANGED)

    fun write(img: Mat, out: File) {
        Imgcodecs.imwrite(out.path, img)
    }

    fun multiply(src: Mat): Mat {
        val filter = Mat(src.size(), src.type(), SCALAR)
        return src.mul(filter)
    }
}