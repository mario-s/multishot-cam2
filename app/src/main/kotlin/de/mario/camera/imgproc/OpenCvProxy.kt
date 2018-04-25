package de.mario.camera.imgproc

import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.photo.Photo
import java.io.File


internal class OpenCvProxy {

    private companion object {
        val SCALAR = Scalar(255.0, 255.0, 255.0)
    }

    fun merge(images: List<Mat>): Mat {
        val fusion = Mat()
        val merger = Photo.createMergeMertens()
        merger.process(images, fusion)
        return fusion
    }

    fun multiply(src: Mat): Mat {
        val filter = Mat(src.size(), src.type(), SCALAR)
        return src.mul(filter)
    }

    fun read(file: File): Mat = read(file.path)

    fun read(path: String): Mat = Imgcodecs.imread(path)

    fun write(img: Mat, out: File) = Imgcodecs.imwrite(out.path, img)

}