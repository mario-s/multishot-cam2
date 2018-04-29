package de.mario.camera.imgproc

import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.photo.Photo
import java.io.File
import org.opencv.imgproc.Imgproc
import org.opencv.video.Video


internal class OpenCvProxy {

    private companion object {
        val SCALAR = Scalar(255.0, 255.0, 255.0)
        val WARP_MATRIX = Mat.eye(3, 3, CvType.CV_32F)
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

    fun align(images: List<Mat>): List<Mat> {
        val size = images.size
        if (size > 1) {
            val result = mutableListOf<Mat>()
            val head = images[0]
            result.add(head)
            val tail = images.drop(1)

            //convert first image to gray
            val headGray = toGray(head)
            //align others
            tail.forEach { result.add(warp(headGray, it)) }

            return result
        }

        return images
    }

    private fun warp(first: Mat, img: Mat): Mat {
        Video.findTransformECC(toGray(img), first, WARP_MATRIX, Video.MOTION_HOMOGRAPHY)
        val dest = Mat()
        Imgproc.warpPerspective(img, dest, WARP_MATRIX, first.size())
        return dest
    }

    private fun toGray(bgr: Mat): Mat {
        val gray = Mat()
        Imgproc.cvtColor(bgr, gray, Imgproc.COLOR_BGR2GRAY)
        return gray
    }

    fun read(file: File): Mat = read(file.path)

    fun read(path: String): Mat = Imgcodecs.imread(path)

    fun write(img: Mat, out: File) = Imgcodecs.imwrite(out.path, img)

}